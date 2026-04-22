package it.gov.pagopa.pu.organization.service.broker;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKey;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrokerService {

  private final BrokerRepository brokerRepository;
  private final BrokerEncryptionService brokerEncryptionService;

  public BrokerService(
    BrokerRepository brokerRepository,
    BrokerEncryptionService brokerEncryptionService) {
    this.brokerEncryptionService = brokerEncryptionService;
    this.brokerRepository = brokerRepository;
  }

  public BrokerApiKeys getBrokerApiKeys(Long brokerId) {
    Broker broker = getBrokerById(brokerId);
    return brokerEncryptionService.getBrokerDecryptedApiKeys(broker);
  }

  public void encryptAndSaveApiKey(Long brokerId, BrokerApiKey brokerApiKey) {
    Broker broker = getBrokerById(brokerId);
    byte[] encryptedKey = brokerEncryptionService.encryptKey(brokerApiKey.getApiKey());
    switch (brokerApiKey.getKeyType()) {
      case SYNC_PAYMENTS_REPORTING -> broker.setSyncPaymentsReportingKey(encryptedKey);
      case SYNC -> broker.setSyncKey(encryptedKey);
      case ACA -> broker.setAcaKey(encryptedKey);
      case GPD -> broker.setGpdKey(encryptedKey);
      case GENERATE_NOTICE -> broker.setGenerateNoticeKey(encryptedKey);
    }
    brokerRepository.save(broker);
  }

  public String getBrokerApiKey(Long brokerId, BrokerApiKeyType keyType) {
    Broker broker = getBrokerById(brokerId);
    return brokerEncryptionService.getBrokerDecryptedApiKey(broker, keyType);
  }

  private @NonNull Broker getBrokerById(Long brokerId) {
    return brokerRepository.findById(brokerId).orElseThrow(() -> new BrokerNotFoundException("broker [%s]".formatted(brokerId)));
  }
}
