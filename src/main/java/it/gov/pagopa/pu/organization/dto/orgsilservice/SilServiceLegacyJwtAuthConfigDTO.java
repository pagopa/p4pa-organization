package it.gov.pagopa.pu.organization.dto.orgsilservice;

import it.gov.pagopa.pu.organization.enums.JwtAlgorithm;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class SilServiceLegacyJwtAuthConfigDTO implements SilServiceAuthConfigDTO {

  @NotNull
  private String kid;
  @NotNull
  private String subject;
  @NotNull
  private String issuer;
  @NotNull
  private JwtAlgorithm algorithm;
  @NotNull
  private String signingKey;

}
