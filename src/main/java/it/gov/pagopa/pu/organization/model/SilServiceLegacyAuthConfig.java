package it.gov.pagopa.pu.organization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class SilServiceLegacyAuthConfig implements SilServiceAuthConfig {

  private String legacyJwtId;
  private String legacyJwtMail;
  private String legacyJwtSecretKeyId;
  private byte[] legacyJwtSecretKey;

}
