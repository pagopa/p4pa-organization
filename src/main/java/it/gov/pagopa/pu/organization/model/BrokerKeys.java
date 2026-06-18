package it.gov.pagopa.pu.organization.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "broker_keys")
@AllArgsConstructor
@NoArgsConstructor(onConstructor_ = @JsonCreator)
@Data
@EqualsAndHashCode(of = "brokerKeyId", callSuper = false)
public class BrokerKeys extends BaseEntity implements Serializable {

  @Id
  private String brokerKeyId;
  @NotNull
  private Long brokerId;
  @NotNull
  @Enumerated(EnumType.STRING)
  private BrokerApiKeyType keyType;
  @NotNull
  private byte[] keyCipher;

  //region keep updated semanticId
  public static String buildSemanticId(BrokerKeys brokerKeys) {
    return buildSemanticId(
      brokerKeys.getBrokerId(),
      brokerKeys.getKeyType()
    );
  }

  public static String buildSemanticId(Long brokerId, BrokerApiKeyType keyType) {
    return brokerId + "_" + keyType;
  }

  private void setSemanticId() {
    this.brokerKeyId = buildSemanticId(this);
  }

  public void getBrokerId(Long brokerId) {
    this.brokerId = brokerId;
    setSemanticId();
  }

  public void setKeyType(BrokerApiKeyType keyType) {
    this.keyType = keyType;
    setSemanticId();
  }
//endregion
}
