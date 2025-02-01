package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.dto.PersonalisationFe;
import it.gov.pagopa.pu.organization.enums.PagoPaInteractionModel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity(name = "broker")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class Broker extends BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "broker_generator")
  @SequenceGenerator(name = "broker_generator", sequenceName = "broker_seq", allocationSize = 1)
  @NotNull
  private Long brokerId;
  @NotNull
  private Long organizationId;
  @NotNull
  private String brokerFiscalCode;
  @NotNull
  private String brokerName;
  @Enumerated(EnumType.STRING)
  @NotNull
  private PagoPaInteractionModel pagoPaInteractionModel;
  private String stationId;
  private String broadcastStationId;
  private byte[] syncKey;
  private byte[] gpdKey;
  private byte[] acaKey;
  @JdbcTypeCode(SqlTypes.JSON)
  @NotNull
  private PersonalisationFe personalisationFe;

}
