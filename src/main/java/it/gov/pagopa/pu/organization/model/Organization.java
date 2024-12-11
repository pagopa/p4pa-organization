package it.gov.pagopa.pu.organization.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "organization")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Organization implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_generator")
  @SequenceGenerator(name = "organization_generator", sequenceName = "organization_seq", allocationSize = 1)
  private Long organizationId;
  private String ipaCode;
  private String orgFiscalCode;
  private String orgName;
  private String adminEmail;
  private Instant creationDate;
  private Instant lastUpdateDate;
  private Long fee;
  private String iban;
  private String urlOrgSendSilPaymentResult;
  private String password;
  private Boolean creditBicSeller;
  private String beneficiaryOrgName;
  private String beneficiaryOrgAddress;
  private String beneficiaryOrgCivic;
  private String beneficiaryOrgPostalCode;
  private String beneficiaryOrgLocation;
  private String beneficiaryOrgProvince;
  private String beneficiaryOrgNation;
  private String beneficiaryOrgPhoneNumber;
  private String beneficiaryOrgWebSite;
  private String beneficiaryOrgEmail;
  private String applicationCode;
  private String cbillInterBankCode;
  private String orgInformation;
  private String orgLogoDesc;
  private String authorizationDesc;
  private String status;
  private String urlActiveExternal;
  private String additionalLanguage;
  private String orgTypeCode;
  private LocalDate startDate;
  private Long brokerId;

}
