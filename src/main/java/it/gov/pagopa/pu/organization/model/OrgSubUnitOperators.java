package it.gov.pagopa.pu.organization.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "org_sub_unit_operators")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class OrgSubUnitOperators extends BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_sub_unit_operators_generator")
  @SequenceGenerator(name = "org_sub_unit_operators_generator", sequenceName = "org_sub_unit_operators_seq", allocationSize = 1)
  private Long orgSubUnitOperatorId;
  @NotNull
  private String operatorExternalUserId;
  @NotNull
  private Long organizationId;
  @NotNull
  private String subUnitCode;
}
