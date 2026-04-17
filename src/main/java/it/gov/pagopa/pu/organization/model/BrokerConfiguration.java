package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.dto.PersonalisationFe;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Entity
@Table(name = "broker_configuration")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class BrokerConfiguration extends BaseEntity implements Serializable {

  @Id
  private Long brokerId;
  @JdbcTypeCode(SqlTypes.JSON)
  private PersonalisationFe personalisationFe;
  @JdbcTypeCode(SqlTypes.JSON)
  private String arpuConfig;
  @NotNull
  private String mailSenderAddress;
  private String receiptFooter;

}
