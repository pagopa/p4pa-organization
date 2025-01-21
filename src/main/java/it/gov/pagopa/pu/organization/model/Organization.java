package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "organization")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Organization extends BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_generator")
  @SequenceGenerator(name = "organization_generator", sequenceName = "organization_seq", allocationSize = 1)
  private Long organizationId;
  private String ipaCode;
  private String orgFiscalCode;
  private String orgName;
  private String orgTypeCode;
  private String orgEmail;
  private String postalIban;
  private String iban;
  private byte[] password;
  private String segregationCode;
  private String cbillInterBankCode;
  private String orgLogo;
  @Enumerated(EnumType.STRING)
  private OrganizationStatus status;
  private String additionalLanguage;
  private LocalDate startDate;
  private Long brokerId;
  private byte[] ioApiKey;
  private boolean flagNotifyIo;
  private boolean flagNotifyOutcomePush;

}
