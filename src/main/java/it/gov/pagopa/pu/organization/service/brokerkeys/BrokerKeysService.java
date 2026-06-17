package it.gov.pagopa.pu.organization.service.brokerkeys;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKey;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.model.BrokerKeys;
import it.gov.pagopa.pu.organization.repository.BrokerKeysRepository;
import it.gov.pagopa.pu.organization.service.broker.BrokerEncryptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BrokerKeysService {

  private final BrokerEncryptionService brokerEncryptionService;
  private final BrokerKeysRepository brokerKeysRepository;

  public BrokerKeysService(BrokerEncryptionService brokerEncryptionService, BrokerKeysRepository brokerKeysRepository) {
    this.brokerEncryptionService = brokerEncryptionService;
    this.brokerKeysRepository = brokerKeysRepository;
  }

  public void encryptAndSaveApiKey(Long brokerId, BrokerApiKey brokerApiKey) {
    byte[] encryptedKey = brokerEncryptionService.encryptKey(brokerApiKey.getApiKey());
    BrokerKeys brokerKeys = new BrokerKeys();
    brokerKeys.setBrokerId(brokerId);
    brokerKeys.setKeyType(brokerApiKey.getKeyType());
    brokerKeys.setKeyCipher(encryptedKey);

    brokerKeysRepository.save(brokerKeys);
  }

  public BrokerApiKeys getBrokerDecryptedApiKeys(Long brokerId) {
    List<BrokerKeys> brokerKeys = brokerKeysRepository.findByBrokerId(brokerId);

    Map<BrokerApiKeyType, byte[]> keysByType = brokerKeys.stream()
      .collect(Collectors.toMap(BrokerKeys::getKeyType, BrokerKeys::getKeyCipher));

    return BrokerApiKeys.builder()
      .syncPaymentsReportingKey(brokerEncryptionService.decryptKey(
        keysByType.get(BrokerApiKeyType.SYNC_PAYMENTS_REPORTING), BrokerApiKeyType.SYNC_PAYMENTS_REPORTING, brokerId))
      .syncKey(brokerEncryptionService.decryptKey(
        keysByType.get(BrokerApiKeyType.SYNC), BrokerApiKeyType.SYNC, brokerId))
      .acaKey(brokerEncryptionService.decryptKey(
        keysByType.get(BrokerApiKeyType.ACA), BrokerApiKeyType.ACA, brokerId))
      .gpdKey(brokerEncryptionService.decryptKey(
        keysByType.get(BrokerApiKeyType.GPD), BrokerApiKeyType.GPD, brokerId))
      .generateNoticeKey(brokerEncryptionService.decryptKey(
        keysByType.get(BrokerApiKeyType.GENERATE_NOTICE), BrokerApiKeyType.GENERATE_NOTICE, brokerId))
      .build();
  }

  public String getBrokerDecryptedApiKey(Long brokerId, BrokerApiKeyType keyType) {
    BrokerKeys brokerKey = brokerKeysRepository.findById(BrokerKeys.buildSemanticId(brokerId, keyType))
      .orElseThrow(() -> new BrokerNotFoundException(
        "Broker with id %s not found or Key of type %s not found".formatted(brokerId, keyType)));

    return brokerEncryptionService.decryptKey(brokerKey.getKeyCipher(), keyType, brokerId);
  }
}
