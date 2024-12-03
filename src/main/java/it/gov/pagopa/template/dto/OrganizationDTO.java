package it.gov.pagopa.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizationDTO {

  private String orgId;
  private String name;
  private String role;
  private String logo;

}
