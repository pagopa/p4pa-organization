package it.gov.pagopa.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class Organization {

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Column(name = "name")
  private String name;

  @Column(name = "role")
  private String role;

  @Column(name = "logo")
  @Lob
  private String logo;

}
