package it.gov.pagopa.pu.organization.util.faker;

import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.model.Organization;

import java.time.LocalDate;

public class OrganizationFaker {

  public static Organization buildOrganization(){
    Organization organization = new Organization();
    organization.setOrganizationId(1L);
    organization.setIpaCode("IPA_CODE");
    organization.setOrgFiscalCode("12345678901");
    organization.setOrgName("orgName");
    organization.setOrgEmail("orgEmail");
    organization.setPostalIban("postalIban");
    organization.setIban("iban");
    organization.setPassword(new byte[]{});
    organization.setSegregationCode("01");
    organization.setCbillInterBankCode("XX");
    organization.setOrgLogo("orgLogo");
    organization.setStatus(OrganizationStatus.ACTIVE);
    organization.setAdditionalLanguage("EN");
    organization.setStartDate(LocalDate.now());
    organization.setBrokerId(1L);
    organization.setIoApiKey(new byte[]{1,2,3});
    organization.setSendApiKey(new byte[]{4,5,6});
    organization.setGenerateNoticeApiKey(new byte[]{7,8,9});
    organization.setFlagNotifyIo(true);
    organization.setFlagNotifyOutcomePush(true);
    organization.setPdndEnabled(false);
    return organization;
  }
}
