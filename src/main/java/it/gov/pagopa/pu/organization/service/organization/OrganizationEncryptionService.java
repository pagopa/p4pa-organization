package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.util.AESUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrganizationEncryptionService {

  private final String organizationEncryptPassword;

  public OrganizationEncryptionService(
    @Value("${app.organizationEncryptPassword}") String organizationEncryptPassword) {
    this.organizationEncryptPassword = organizationEncryptPassword;
  }

  public byte[] encrypt(String plainText) {
    return AESUtils.encrypt(organizationEncryptPassword, plainText);
  }
}
