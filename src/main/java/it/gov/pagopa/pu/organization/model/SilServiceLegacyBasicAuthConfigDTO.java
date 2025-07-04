package it.gov.pagopa.pu.organization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class SilServiceLegacyBasicAuthConfigDTO implements SilServiceAuthConfigDTO {

  private String authUrl;
  private String user;
  private String psw;

}
