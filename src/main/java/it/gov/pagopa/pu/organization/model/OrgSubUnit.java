package it.gov.pagopa.pu.organization.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus;
import it.gov.pagopa.pu.organization.enums.SubUnitType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "org_sub_unit")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class OrgSubUnit extends BaseEntity implements Serializable {
  @EmbeddedId
  @JsonUnwrapped
  OrgSubUnitId id;
  @Enumerated(EnumType.STRING)
  SubUnitType subUnitType;
  @Enumerated(EnumType.STRING)
  @NotNull
  OrgSubUnitStatus status;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Embeddable
  public static class OrgSubUnitId implements Serializable{
    @NotNull
    Long organizationId;
    @NotNull
    String subUnitCode;

    // This should be aligned with it.gov.pagopa.pu.organization.config.RepositoryRestCustomConverters.OrgSubUnitIdConverter
    @Override
    public String toString() {
      return "%s-%s".formatted(organizationId, subUnitCode);
    }
  }
}


