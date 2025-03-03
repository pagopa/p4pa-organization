package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrganizationEncryptionService {

  private final String organizationEncryptPassword;

  public OrganizationEncryptionService(
    @Value("${app.organizationEncryptPassword}") String organizationEncryptPassword) {
    this.organizationEncryptPassword = organizationEncryptPassword;
  }

  public byte[] encrypt(String plainText) {
    return AESUtils.encrypt(organizationEncryptPassword, plainText);
  }

  public String decryptKey(byte[] encryptedKey){
    if(encryptedKey==null || encryptedKey.length==0) {
      log.debug("Null or empty api-key");
      return null;
    }
    return AESUtils.decrypt(organizationEncryptPassword, encryptedKey);
  }
}
