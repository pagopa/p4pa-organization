package it.gov.pagopa.pu.organization.model.taxonomy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "taxonomy")
@IdClass(TaxonomyCodeDTO.class)
public class TaxonomyCodeDTO {
  @Id
  private String organizationType;
  private String organizationTypeDescription;
  @Id
  private String macroAreaCode;
  private String macroAreaName;
  private String macroAreaDescription;
  @Id
  private String serviceTypeCode;
  private String serviceType;
  private String serviceTypeDescription;
  @Id
  private String collectionReason;
  @Id
  private String taxonomyCode;
}
