package it.gov.pagopa.pu.organization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class SilServiceLegacyJwtAuthConfig implements SilServiceAuthConfig {

  private String kid;
  private String subject;
  private String issuer;
  private String algorithm;
  private byte[] signingKey;

}
