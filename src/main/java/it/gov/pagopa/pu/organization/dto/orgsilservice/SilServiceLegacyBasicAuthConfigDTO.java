package it.gov.pagopa.pu.organization.dto.orgsilservice;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class SilServiceLegacyBasicAuthConfigDTO implements SilServiceAuthConfigDTO {

  @NotNull
  private String authUrl;
  @NotNull
  private String user;
  @NotNull
  private String psw;

}
