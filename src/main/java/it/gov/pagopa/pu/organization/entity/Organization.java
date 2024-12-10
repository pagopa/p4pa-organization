package it.gov.pagopa.pu.organization.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "organization")
@Data
public class Organization implements Serializable {

  @Id
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