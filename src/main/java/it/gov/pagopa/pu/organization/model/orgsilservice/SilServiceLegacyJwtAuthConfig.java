package it.gov.pagopa.pu.organization.model.orgsilservice;

import it.gov.pagopa.pu.organization.enums.JwtAlgorithm;
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
  private JwtAlgorithm algorithm;
  private byte[] signingKey;

}
