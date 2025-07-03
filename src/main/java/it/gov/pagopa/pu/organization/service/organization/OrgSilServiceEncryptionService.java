package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.model.SilServiceAuthConfig;
import it.gov.pagopa.pu.organization.model.SilServiceLegacyBasicAuthConfig;
import it.gov.pagopa.pu.organization.model.SilServiceLegacyJwtAuthConfig;
import it.gov.pagopa.pu.organization.util.AESUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class OrgSilServiceEncryptionService {

  private final String encryptPassword;

  public OrgSilServiceEncryptionService(
    @Value("${app.orgSilServiceEncryptPassword}") String encryptPassword
  ) {
    this.encryptPassword = encryptPassword;
  }

  public SilServiceAuthConfig encryptAuthConfig(SilServiceAuthConfig authConfig) {
    return switch (authConfig) {
      case null -> null;
      case SilServiceLegacyBasicAuthConfig silServiceLegacyBasicAuthConfig ->
        this.encryptLegacyBasicAuthConfig(silServiceLegacyBasicAuthConfig);
      case SilServiceLegacyJwtAuthConfig silServiceLegacyJwtAuthConfig ->
        this.encryptLegacyJwtAuthConfig(silServiceLegacyJwtAuthConfig);
      default -> authConfig;
    };
  }

  public SilServiceAuthConfig decryptAuthConfig(SilServiceAuthConfig authConfig) {
    return switch (authConfig) {
      case null -> null;
      case SilServiceLegacyBasicAuthConfig silServiceLegacyBasicAuthConfig ->
        this.decryptLegacyBasicAuthConfig(silServiceLegacyBasicAuthConfig);
      case SilServiceLegacyJwtAuthConfig silServiceLegacyJwtAuthConfig ->
        this.decryptLegacyJwtAuthConfig(silServiceLegacyJwtAuthConfig);
      default -> authConfig;
    };
  }

  public SilServiceLegacyJwtAuthConfig encryptLegacyJwtAuthConfig(
    SilServiceLegacyJwtAuthConfig legacyJwtAuthConfig
  ) {
    if (legacyJwtAuthConfig == null) {
      return null;
    }

    String signingKey = new String(legacyJwtAuthConfig.getSigningKey(), StandardCharsets.UTF_8);
    legacyJwtAuthConfig.setSigningKey(AESUtils.encrypt(encryptPassword, signingKey));

    return legacyJwtAuthConfig;
  }

  public SilServiceLegacyJwtAuthConfig decryptLegacyJwtAuthConfig(
    SilServiceLegacyJwtAuthConfig legacyJwtAuthConfig
  ) {
    if (legacyJwtAuthConfig == null) {
      return null;
    }

    legacyJwtAuthConfig.setSigningKey(AESUtils.decrypt(encryptPassword, legacyJwtAuthConfig.getSigningKey()).getBytes(StandardCharsets.UTF_8));

    return legacyJwtAuthConfig;
  }

  public SilServiceLegacyBasicAuthConfig encryptLegacyBasicAuthConfig(
    SilServiceLegacyBasicAuthConfig legacyBasicAuthConfig
  ) {
    if (legacyBasicAuthConfig == null) {
      return null;
    }

    String user = new String(legacyBasicAuthConfig.getUser(), StandardCharsets.UTF_8);
    String psw = new String(legacyBasicAuthConfig.getPsw(), StandardCharsets.UTF_8);

    legacyBasicAuthConfig.setUser(AESUtils.encrypt(encryptPassword, user));
    legacyBasicAuthConfig.setPsw(AESUtils.encrypt(encryptPassword, psw));

    return legacyBasicAuthConfig;
  }

  public SilServiceLegacyBasicAuthConfig decryptLegacyBasicAuthConfig(
    SilServiceLegacyBasicAuthConfig legacyBasicAuthConfig
  ) {
    if (legacyBasicAuthConfig == null) {
      return null;
    }

    legacyBasicAuthConfig.setUser(AESUtils.decrypt(encryptPassword, legacyBasicAuthConfig.getUser()).getBytes(StandardCharsets.UTF_8));
    legacyBasicAuthConfig.setPsw(AESUtils.decrypt(encryptPassword, legacyBasicAuthConfig.getPsw()).getBytes(StandardCharsets.UTF_8));

    return legacyBasicAuthConfig;
  }

}
