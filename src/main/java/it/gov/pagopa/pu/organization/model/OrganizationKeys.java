package it.gov.pagopa.pu.organization.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "organization_keys")
@AllArgsConstructor
@NoArgsConstructor(onConstructor_ = @JsonCreator)
@Data
@EqualsAndHashCode(of = "organizationKeyId", callSuper = false)
public class OrganizationKeys extends BaseEntity implements Serializable {
  @Id
  private String organizationKeyId;
  @NotNull
  private Long organizationId;
  private String subUnitCode;
  @NotNull
  @Enumerated(EnumType.STRING)
  private OrganizationApiKeyType keyType;
  @NotNull
  private byte[] keyCipher;

  //region keep updated semanticId
  public static String buildSemanticId(OrganizationKeys organizationKeys) {
    return buildSemanticId(
      organizationKeys.getOrganizationId(),
      organizationKeys.getSubUnitCode(),
      organizationKeys.getKeyType()
    );
  }

  public static String buildSemanticId(Long organizationId, String subUnitCode, OrganizationApiKeyType keyType) {
    return organizationId + "_" +
      (subUnitCode != null ? subUnitCode + "_" : "_") +
      keyType;
  }

  private void setSemanticId() {
    this.organizationKeyId = buildSemanticId(this);
  }

  public void setOrganizationId(Long organizationId) {
    this.organizationId = organizationId;
    setSemanticId();
  }

  public void setSubUnitCode(String subUnitCode) {
    this.subUnitCode = subUnitCode;
    setSemanticId();
  }

  public void setKeyType(OrganizationApiKeyType keyType) {
    this.keyType = keyType;
    setSemanticId();
  }
//endregion
}
