package it.gov.pagopa.pu.organization.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "taxonomy")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Taxonomy implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taxonomy_generator")
  @SequenceGenerator(name = "taxonomy_generator", sequenceName = "taxonomy_seq", allocationSize = 1)
  private Long taxonomyId;
  private Long version;
  private String organizationType;
  private String organizationTypeDescription;
  private String macroAreaCode;
  private String macroAreaName;
  private String macroAreaDescription;
  private String serviceTypeCode;
  private String serviceType;
  private String serviceTypeDescription;
  private String collectionReason;
  private Timestamp startDateValidity;
  private Timestamp endDateOfValidity;
  private String taxonomyCode;
  private Timestamp creationDate;
  private Timestamp lastModifiedDate;
  private Long versionMyPay;

}
