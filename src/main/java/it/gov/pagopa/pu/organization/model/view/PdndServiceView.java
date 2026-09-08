package it.gov.pagopa.pu.organization.model.view;

import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pdnd_service_view")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class PdndServiceView {
  @Id
  private String purposeId;
  @NotNull
  private String serviceName;
  @NotNull
  private PdndServiceType serviceType;
  @NotNull
  private String clientId;
  @NotNull
  private String clientName;
  String subUnitCode;
  String subUnitName;
}
