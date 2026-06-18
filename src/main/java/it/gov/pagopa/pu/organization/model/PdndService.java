package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "pdnd_services")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class PdndService extends BaseEntity implements Serializable {
  @Id
  private String purposeId;
  @NotNull
  private String serviceName;
  @NotNull
  @Enumerated(EnumType.STRING)
  private PdndServiceType serviceType;
  @NotNull
  private String clientId;
}
