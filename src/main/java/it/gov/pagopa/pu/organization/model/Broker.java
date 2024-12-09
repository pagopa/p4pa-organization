package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.enums.PagoPaInteractionModel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

@Entity(name = "broker")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Broker implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "broker_generator")
  @SequenceGenerator(name = "broker_generator", sequenceName = "broker_seq", allocationSize = 1)
  private Long brokerId;

  private String brokerFiscalCode;

  private String stationId;

  private byte[] syncKey;

  private byte[] gpdKey;

  private byte[] acaKey;

  private String brokerName;

  @ColumnTransformer(write = "?::jsonb")
  private String personalisationFe;

  private String broadcastStationId;

  @Enumerated(EnumType.STRING)
  private PagoPaInteractionModel pagoPaInteractionModel;

  private Long organization_id;
}
