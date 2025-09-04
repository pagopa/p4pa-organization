package it.gov.pagopa.pu.organization.dto;

import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDTO {
  @NotNull
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
  private String password;
  private String segregationCode;
  private String cbillInterBankCode;
  private String orgLogo;
  @Enumerated(EnumType.STRING)
  @NotNull
  private OrganizationStatus status;
  private String additionalLanguage;
  private LocalDate startDate;
  private Long brokerId;
  private String ioApiKey;
  private String sendApiKey;
  private String generateNoticeApiKey;
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
}
