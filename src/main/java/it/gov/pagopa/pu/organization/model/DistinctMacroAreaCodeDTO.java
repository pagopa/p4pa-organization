package it.gov.pagopa.pu.organization.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "taxonomy")
public class TaxonomyMacroAreaCodeDTO {

  @Id
  private String organizationType;
  private String organizationTypeDescription;
  private String macroAreaCode;
  private String macroAreaName;
  private String macroAreaDescription;

}
