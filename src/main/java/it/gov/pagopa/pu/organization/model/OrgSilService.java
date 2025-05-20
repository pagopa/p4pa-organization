package it.gov.pagopa.pu.organization.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Entity
@Table(name = "orgSilService")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class OrgSilService extends BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orgSilServiceGenerator")
  @SequenceGenerator(name = "orgSilServiceGenerator", sequenceName = "orgSilServiceSeq", allocationSize = 1)
  private Long orgSilServiceId;
  @NotNull
  private Long organizationId;
  @NotNull
  private String applicationName;
  @NotNull
  private String serviceUrl;
  @NotNull
  private String serviceType;
  private boolean flagLegacy;
  @JdbcTypeCode(SqlTypes.JSON)
  private SilServiceAuthConfig authConfig;
}
