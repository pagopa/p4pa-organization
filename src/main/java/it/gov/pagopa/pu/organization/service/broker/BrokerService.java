package it.gov.pagopa.pu.organization.service.broker;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKey;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrokerService {

  private final BrokerRepository brokerRepository;
  private final BrokerEncryptionService brokerEncryptionService;
  private static final String BROKER = "broker [%s]";

  public BrokerService(
    BrokerRepository brokerRepository,
    BrokerEncryptionService brokerEncryptionService) {
    this.brokerEncryptionService = brokerEncryptionService;
    this.brokerRepository = brokerRepository;
  }

  public BrokerApiKeys getBrokerApiKeys(Long brokerId) {
    Broker broker = brokerRepository.findById(brokerId).orElseThrow(() -> new ResourceNotFoundException(BROKER.formatted(brokerId)));
    return brokerEncryptionService.getBrokerDecryptedApiKeys(broker);
  }

  public void encryptAndSaveApiKey(Long brokerId, BrokerApiKey brokerApiKey) {
    Broker broker = brokerRepository.findById(brokerId).orElseThrow(() -> new ResourceNotFoundException(BROKER.formatted(brokerId)));
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
    Broker broker = brokerRepository.findById(brokerId).orElseThrow(() -> new ResourceNotFoundException(BROKER.formatted(brokerId)));
    return brokerEncryptionService.getBrokerDecryptedApiKey(broker, keyType);
  }
}
