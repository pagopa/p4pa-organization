package it.gov.pagopa.pu.organization.service;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("brokerService")
@Slf4j
public class BrokerService {

  private final String brokerEncryptPassword;
  private final BrokerRepository brokerRepository;

  public BrokerService(
    @Value("${app.brokerEncryptPassword}") String brokerEncryptPassword,
    BrokerRepository brokerRepository) {
    this.brokerEncryptPassword = brokerEncryptPassword;
    this.brokerRepository = brokerRepository;
  }

  private final Map<byte[], String> apiKeyDecryptMap = Collections.synchronizedMap(new HashMap<>());

  public BrokerApiKeys getBrokerApiKeys(Long brokerId){
    Broker broker = brokerRepository.findById(brokerId).orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND));

    BrokerApiKeys brokerApiKeys = BrokerApiKeys.builder()
      .syncKey(decryptKey(broker.getSyncKey()))
      .acaKey(decryptKey(broker.getAcaKey()))
      .gpdKey(decryptKey(broker.getGpdKey()))
      .build();

    return brokerApiKeys;
  }

  public String decryptKey(byte[] encryptedKey){
    if(encryptedKey==null || encryptedKey.length==0) {
      log.debug("null or empty api-key");
      return null;
    }
    String decrypted = apiKeyDecryptMap.computeIfAbsent(encryptedKey, c -> {
      log.debug("invoking AESUtils to decrypt api-key");
      return AESUtils.decrypt(brokerEncryptPassword,c);
    });
    log.debug("decrypted api-key: {}", StringUtils.abbreviateMiddle(decrypted, "..", 8));
    return decrypted;
  }
}
