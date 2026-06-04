package it.gov.pagopa.pu.organization.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;

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
  private Long brokerId;
  @NotNull
  private Long organizationId;
  @NotNull
  private String brokerFiscalCode;
  @NotNull
  private String brokerName;
  private byte[] syncPaymentsReportingKey;
  private byte[] syncKey;
  private byte[] gpdKey;
  private byte[] generateNoticeKey;
  private byte[] acaKey;
  @NotNull
  private boolean flagDelegate;
  @NotNull
  private boolean flagPaymentsReporting;
  @NotNull
  private String externalId;
  private String defaultStationId;
  @Pattern(regexp = "^\\d{2}$", message = "The informationSystemId must be 2 digits long")
  private String iuvSystemId;
}
