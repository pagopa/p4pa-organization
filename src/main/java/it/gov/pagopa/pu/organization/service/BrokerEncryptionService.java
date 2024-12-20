package it.gov.pagopa.pu.organization.service;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BrokerEncryptionService {

  private final String brokerEncryptPassword;

  public BrokerEncryptionService(
    @Value("${app.brokerEncryptPassword}") String brokerEncryptPassword) {
    this.brokerEncryptPassword = brokerEncryptPassword;
  }

  private final Map<byte[], String> apiKeyDecryptMap = Collections.synchronizedMap(new HashMap<>());

  public BrokerApiKeys getBrokerDecryptedApiKeys(Broker broker){
    return BrokerApiKeys.builder()
      .syncKey(decryptKey(broker.getSyncKey(),"SYNC", broker.getBrokerId()))
      .acaKey(decryptKey(broker.getAcaKey(),"ACA", broker.getBrokerId()))
      .gpdKey(decryptKey(broker.getGpdKey(),"GPD", broker.getBrokerId()))
      .build();
  }

  private String decryptKey(byte[] encryptedKey, String type, Long brokerId){
    if(encryptedKey==null || encryptedKey.length==0) {
      log.debug("null or empty api-key");
      return null;
    }
    String decrypted = apiKeyDecryptMap.computeIfAbsent(encryptedKey, c -> {
      log.debug("invoking AESUtils to decrypt api-key");
      return AESUtils.decrypt(brokerEncryptPassword,c);
    });
    log.debug("decrypted api-key[{}] for broker[{}]: {}", type, brokerId, StringUtils.abbreviateMiddle(decrypted, "..", 8));
    return decrypted;
  }
}
