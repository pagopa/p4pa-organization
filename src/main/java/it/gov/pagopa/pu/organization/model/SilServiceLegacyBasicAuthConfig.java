package it.gov.pagopa.pu.organization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class SilServiceLegacyBasicAuthConfig implements SilServiceAuthConfig {

  private String authUrl;
  private byte[] user;
  private byte[] psw;

}
