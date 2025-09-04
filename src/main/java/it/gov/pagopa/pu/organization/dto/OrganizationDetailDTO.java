package it.gov.pagopa.pu.organization.dto;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class OrganizationDetailDTO extends OrganizationCreateDTO {
  private Long organizationId;
  private Boolean flagTreasury;
}
