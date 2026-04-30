package it.gov.pagopa.pu.organization.model.view;

import it.gov.pagopa.pu.organization.enums.OrgSilServiceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "org_sil_service")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrgSilServiceView implements Serializable {
  @Id
  @NotNull
  private Long orgSilServiceId;
  @NotNull
  private Long organizationId;
  @NotNull
  private String applicationName;
  @NotNull
  private String serviceUrl;
  @NotNull
  @Enumerated(EnumType.STRING)
  private OrgSilServiceType serviceType;
  private boolean flagLegacy;
  private LocalDateTime updateDate;
}
