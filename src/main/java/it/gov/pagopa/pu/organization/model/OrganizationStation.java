package it.gov.pagopa.pu.organization.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity(name = "organization_station")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class OrganizationStation extends BaseEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_station_generator")
  @SequenceGenerator(name = "organization_station_generator", sequenceName = "organization_station_seq", allocationSize = 1)
  private Long organizationStationId;
  @NotNull
  private Long organizationId;
  @NotNull
  private String stationId;
  @NotNull
  private String segregationCode;
}
