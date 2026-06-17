package it.gov.pagopa.pu.organization.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "pdnd_clients")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class PdndClient extends BaseEntity implements Serializable {
  @Id
  private String clientId;
  @NotNull
  private Long organizationId;
  private String subUnitCode;
  @NotNull
  private String clientName;
  @NotNull
  private String kid;
  @NotNull
  private byte[] privateKeyCipher;
  @NotNull
  private String publicKey;
}
