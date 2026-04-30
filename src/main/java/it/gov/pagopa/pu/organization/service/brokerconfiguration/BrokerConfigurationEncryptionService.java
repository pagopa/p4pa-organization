package it.gov.pagopa.pu.organization.service.brokerconfiguration;

import it.gov.pagopa.pu.organization.dto.EmailServerConfig;
import it.gov.pagopa.pu.organization.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BrokerConfigurationEncryptionService {

  private final String brokerEncryptPassword;
  private final JsonMapper jsonMapper;

  public BrokerConfigurationEncryptionService(
    @Value("${app.brokerEncryptPassword}") String brokerEncryptPassword, JsonMapper jsonMapper) {
    this.brokerEncryptPassword = brokerEncryptPassword;
    this.jsonMapper = jsonMapper;
  }

  private final Map<byte[], EmailServerConfig> emailServerConfigDecryptMap = new ConcurrentHashMap<>();

  public EmailServerConfig decryptEmailServerConfig(byte[] encryptedEmailServerConfig, Long brokerId) {
    if (encryptedEmailServerConfig == null || encryptedEmailServerConfig.length == 0) {
      log.debug("null or empty email server config");
      return null;
    }
    return emailServerConfigDecryptMap.computeIfAbsent(encryptedEmailServerConfig, c -> {
      log.debug("invoking AESUtils to decrypt email server config for broker[{}]", brokerId);
      return jsonMapper.readValue(AESUtils.decrypt(brokerEncryptPassword, c),EmailServerConfig.class);
    });
  }

  public byte[] encryptEmailServerConfig(EmailServerConfig emailServerConfig) {
    if (emailServerConfig == null) {
      return null;
    }
    byte[] encryptedKey = AESUtils.encrypt(brokerEncryptPassword, jsonMapper.writeValueAsString(emailServerConfig));
    emailServerConfigDecryptMap.put(encryptedKey, emailServerConfig);
    return encryptedKey;
  }
}
