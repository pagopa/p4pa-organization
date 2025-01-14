package it.gov.pagopa.pu.organization.model.taxonomy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "taxonomy")
@IdClass(TaxonomyCodeDTO.class)
public class TaxonomyCodeDTO {
  @Id
  private String organizationType;
  private String organizationTypeDescription;
  private String macroAreaCode;
  private String macroAreaName;
  private String macroAreaDescription;
  private String serviceTypeCode;
  private String serviceType;
  private String serviceTypeDescription;
  private String collectionReason;
  private String taxonomyCode;
}
