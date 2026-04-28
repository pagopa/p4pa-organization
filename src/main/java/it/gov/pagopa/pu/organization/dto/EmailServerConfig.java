package it.gov.pagopa.pu.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailServerConfig implements Serializable {
  private String host;
  private int port;
  private String username;
  private String password;
  private boolean startTls;
  private boolean startTlsRequired;
}
