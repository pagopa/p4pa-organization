package it.gov.pagopa.pu.organization.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgAndSubUnitDTO {
  @NotNull
  private Long organizationId;
  @NotNull
  private String orgName;
  @NotNull
  private String subUnitCode;
  private String subUnitName;
}
