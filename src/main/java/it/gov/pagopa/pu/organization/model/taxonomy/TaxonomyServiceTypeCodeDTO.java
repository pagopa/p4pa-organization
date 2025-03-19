package it.gov.pagopa.pu.organization.model.taxonomy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "taxonomy")
@IdClass(TaxonomyServiceTypeCodeDTO.class)
public class TaxonomyServiceTypeCodeDTO {
  @Id
  @NotNull
  private String organizationType;
  @NotNull
  private String organizationTypeDescription;
  @Id
  @NotNull
  private String macroAreaCode;
  @NotNull
  private String macroAreaName;
  @NotNull
  private String macroAreaDescription;
  @Id
  @NotNull
  private String serviceTypeCode;
  @NotNull
  private String serviceType;
  @NotNull
  private String serviceTypeDescription;
}
