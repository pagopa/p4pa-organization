package it.gov.pagopa.pu.organization.dto;

import it.gov.pagopa.pu.organization.enums.SubUnitType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSubUnitDTO {
  @NotNull
  private Long organizationId;
  @NotNull
  private String subUnitCode;
  @NotNull
  private SubUnitType subUnitType;
  @NotNull
  private String subUnitName;
}
