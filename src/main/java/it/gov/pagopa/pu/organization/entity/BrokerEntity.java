package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.enums.PagoPaInteractionModel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.rest.core.annotation.RestResource;

@Entity(name = "broker")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BrokerEntity implements Serializable {
  @Id
  private Long brokerId;

  private String brokerFiscalCode;

  private String stationId;

  private byte[] syncKey;

  private byte[] gpdKey;

  private byte[] acaKey;

  private String brokerName;

  private String personalisationFe;

  private String broadcastStationId;

  @Enumerated(EnumType.STRING)
  private PagoPaInteractionModel pagoPaInteractionModel;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "brokerId")
  @RestResource(path = "organization", rel="masterOrg")
  private List<OrganizationEntity> masterOrg;
}
