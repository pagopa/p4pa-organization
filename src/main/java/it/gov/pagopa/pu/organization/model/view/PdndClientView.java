package it.gov.pagopa.pu.organization.model.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pdnd_clients")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class PdndClientView {
  @Id
  @NotNull
  private String clientId;
  @NotNull
  private Long organizationId;
  @NotNull
  private String subUnitCode;
  @NotNull
  private String clientName;
  @NotNull
  private String kid;
  @NotNull
  private String subUnitName;
}
