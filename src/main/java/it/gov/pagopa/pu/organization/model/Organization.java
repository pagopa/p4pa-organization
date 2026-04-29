package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.dto.BaseOrganization;
import it.gov.pagopa.pu.organization.enums.OrganizationAdditionalLanguage;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class Organization extends BaseEntity implements Serializable, BaseOrganization {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_generator")
  @SequenceGenerator(name = "organization_generator", sequenceName = "organization_seq", allocationSize = 1)
  private Long organizationId;
  private String externalOrganizationId;
  @NotNull
  private String ipaCode;
  @NotNull
  private String orgFiscalCode;
  @NotNull
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
  @NotNull
  private OrganizationStatus status;
  @Enumerated(EnumType.STRING)
  private OrganizationAdditionalLanguage additionalLanguage;
  private LocalDate startDate;
  private Long brokerId;
  private byte[] ioApiKey;
  private byte[] sendApiKey;
  private byte[] generateNoticeApiKey;
  @NotNull
  private boolean flagNotifyIo;
  @NotNull
  private boolean flagNotifyOutcomePush;
  @NotNull
  private boolean flagPaymentNotification;
  @NotNull
  private boolean pdndEnabled;
  @NotNull
  private boolean flagTreasury;
  @NotNull
  private boolean flagPaymentsReporting;
  @NotNull
  private boolean flagClassification;
  private String address;
  private String zipCode;
  private String city;
  private Long defaultOrganizationStationId;
}
