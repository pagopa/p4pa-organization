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
@IdClass(TaxonomyMacroAreaCodeDTO.class)
public class TaxonomyMacroAreaCodeDTO {

  @Id
  private String organizationType;
  private String organizationTypeDescription;
  @Id
  private String macroAreaCode;
  private String macroAreaName;
  private String macroAreaDescription;

}
