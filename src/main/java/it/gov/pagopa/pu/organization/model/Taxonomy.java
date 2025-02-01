package it.gov.pagopa.pu.organization.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "taxonomy")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Taxonomy extends BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taxonomy_generator")
  @SequenceGenerator(name = "taxonomy_generator", sequenceName = "taxonomy_seq", allocationSize = 1)
  @NotNull
  private Long taxonomyId;
  @NotNull
  private String organizationType;
  @NotNull
  private String organizationTypeDescription;
  @NotNull
  private String macroAreaCode;
  @NotNull
  private String macroAreaName;
  @NotNull
  private String macroAreaDescription;
  @NotNull
  private String serviceTypeCode;
  @NotNull
  private String serviceType;
  @NotNull
  private String serviceTypeDescription;
  @NotNull
  private String collectionReason;
  @NotNull
  private OffsetDateTime startDateValidity;
  @NotNull
  private OffsetDateTime endDateOfValidity;
  @NotNull
  private String taxonomyCode;

}
