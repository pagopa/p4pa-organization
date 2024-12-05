package it.gov.pagopa.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "organization")
public class Organization {

  @Id
  @Column
  private Long orgId;
  @Column
  private String ipaCode;
  @Column
  private String orgFiscalCode;
  @Column
  private String orgName;
  @Column
  private String adminEmail;
  @Column
  private Instant creationDate;
  @Column
  private Instant lastUpdateDate;
  @Column
  private String paymentTypeCode;
  @Column
  private Long fee;
  @Column
  private String iban;
  @Column
  private String myBoxClientKey;
  @Column
  private String myBoxClientSecret;
  @Column
  private String urlOrgSendSILPaymentResult;
  @Column
  private String codeGlobalLocationNumber;
  @Column
  private String password;
  @Column
  private Boolean creditBicSeller;
  @Column
  private String beneficiaryOrgName;
  @Column
  private String beneficiaryOrgAddress;
  @Column
  private String beneficiaryOrgCivic;
  @Column
  private String beneficiaryOrgPostalCode;
  @Column
  private String beneficiaryOrgLocation;
  @Column
  private String beneficiaryOrgProvince;
  @Column
  private String beneficiaryOrgNation;
  @Column
  private String beneficiaryOrgPhoneNumber;
  @Column
  private String beneficiaryOrgWebSite;
  @Column
  private String beneficiaryOrgEmail;
  @Column
  private String applicationCode;
  @Column
  private String cbillInterbankCode;
  @Column
  private String orgInformation;
  @Column
  private String orgLogoDesc;
  @Column
  private String authorizationDesc;
  @Column
  private String status;
  @Column
  private String urlActiveExternal;
  @Column
  private String additionalLanguage;
  @Column
  private String orgTypeCode;
  @Column
  private LocalDate startDate;
  @Column
  private Long brokerId;

}
