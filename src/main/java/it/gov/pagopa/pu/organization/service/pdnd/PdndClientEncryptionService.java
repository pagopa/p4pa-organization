package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PdndClientEncryptionService {

  private final String pdndClientEncryptPassword;

  public PdndClientEncryptionService(
    @Value("${app.pdndClientEncryptPassword}") String pdndClientEncryptPassword) {
    this.pdndClientEncryptPassword = pdndClientEncryptPassword;
  }

  public byte[] encrypt(String plainText) {
    return AESUtils.encrypt(pdndClientEncryptPassword, plainText);
  }

  public String decryptKey(byte[] encryptedKey){
    if(encryptedKey==null || encryptedKey.length==0) {
      log.debug("Null or empty api-key");
      return null;
    }
    return AESUtils.decrypt(pdndClientEncryptPassword, encryptedKey);
  }
}
