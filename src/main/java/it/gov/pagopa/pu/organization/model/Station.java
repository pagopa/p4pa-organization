package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.enums.PagoPaInteractionModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity(name = "station")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class Station extends BaseEntity implements Serializable {
  @Id
  private String stationId;
  @NotNull
  private Long brokerId;
  @NotNull
  private PagoPaInteractionModel pagoPaInteractionModel;
  private String broadcastStationId;
  @NotNull
  private boolean enabled;
}
