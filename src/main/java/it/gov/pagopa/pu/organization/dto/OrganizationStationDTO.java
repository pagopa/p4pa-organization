package it.gov.pagopa.pu.organization.dto;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.enums.PagoPaInteractionModel;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class OrganizationStationDTO extends OrganizationCreateDTO {
  // Organization
  @NotNull
  private Long organizationId;

  // Station
  private String stationId;
  private Long brokerId;
  private PagoPaInteractionModel pagoPaInteractionModel;
  private String broadcastStationId;
  private Boolean enabled;

  // OrganizationStation
  private String segregationCode;
}
