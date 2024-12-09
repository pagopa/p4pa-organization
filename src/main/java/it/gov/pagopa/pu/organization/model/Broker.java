package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.enums.PagoPaInteractionModel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "broker")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Broker implements Serializable {
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

  private Long masterOrg;
}
