package it.gov.pagopa.pu.organization.service.broker;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKey;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BrokerEncryptionService {

  private final String brokerEncryptPassword;

  public BrokerEncryptionService(
    @Value("${app.brokerEncryptPassword}") String brokerEncryptPassword) {
    this.brokerEncryptPassword = brokerEncryptPassword;
  }

  private final Map<byte[], String> apiKeyDecryptMap = new ConcurrentHashMap<>();

  public BrokerApiKeys getBrokerDecryptedApiKeys(Broker broker){
    return BrokerApiKeys.builder()
      .syncKey(decryptKey(broker.getSyncKey(),BrokerApiKey.KeyTypeEnum.SYNC, broker.getBrokerId()))
      .acaKey(decryptKey(broker.getAcaKey(),BrokerApiKey.KeyTypeEnum.ACA, broker.getBrokerId()))
      .gpdKey(decryptKey(broker.getGpdKey(),BrokerApiKey.KeyTypeEnum.GPD, broker.getBrokerId()))
      .build();
  }

  private String decryptKey(byte[] encryptedKey, BrokerApiKey.KeyTypeEnum type, Long brokerId){
    if(encryptedKey==null || encryptedKey.length==0) {
      log.debug("null or empty api-key");
      return null;
    }
    return apiKeyDecryptMap.computeIfAbsent(encryptedKey, c -> {
      log.debug("invoking AESUtils to decrypt api-key[{}] for broker[{}]", type, brokerId);
      return AESUtils.decrypt(brokerEncryptPassword,c);
    });
  }

  public byte[] encryptKey(String apiKey){
    if(StringUtils.isEmpty(apiKey)) {
      return null;
    }
    byte[] encryptedKey = AESUtils.encrypt(brokerEncryptPassword, apiKey);
    apiKeyDecryptMap.put(encryptedKey, apiKey);
    return encryptedKey;
  }
}
