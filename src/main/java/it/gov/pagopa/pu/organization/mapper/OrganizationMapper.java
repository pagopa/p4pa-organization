package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationMapper {

  private final OrganizationEncryptionService encryptionService;

  public Organization toModel(OrganizationCreateDTO createDTO) {
    if (createDTO == null) {
      return null;
    }

    Organization organization = new Organization();
    organization.setExternalOrganizationId(createDTO.getExternalOrganizationId());
    organization.setIpaCode(createDTO.getIpaCode());
    organization.setOrgFiscalCode(createDTO.getOrgFiscalCode());
    organization.setOrgName(createDTO.getOrgName());
    organization.setOrgTypeCode(createDTO.getOrgTypeCode());
    organization.setOrgEmail(createDTO.getOrgEmail());
    organization.setPostalIban(createDTO.getPostalIban());
    organization.setIban(createDTO.getIban());
    organization.setPassword(encryptionService.encrypt(createDTO.getPassword()));
    organization.setSegregationCode(createDTO.getSegregationCode());
    organization.setCbillInterBankCode(createDTO.getCbillInterBankCode());
    organization.setOrgLogo(createDTO.getOrgLogo());
    organization.setStatus(createDTO.getStatus());
    organization.setAdditionalLanguage(createDTO.getAdditionalLanguage());
    organization.setStartDate(createDTO.getStartDate());
    organization.setBrokerId(createDTO.getBrokerId());
    organization.setIoApiKey(encryptionService.encrypt(createDTO.getIoApiKey()));
    organization.setSendApiKey(encryptionService.encrypt(createDTO.getSendApiKey()));
    organization.setGenerateNoticeApiKey(encryptionService.encrypt(createDTO.getGenerateNoticeApiKey()));
    organization.setFlagNotifyIo(createDTO.getFlagNotifyIo());
    organization.setFlagNotifyOutcomePush(createDTO.getFlagNotifyOutcomePush());
    organization.setFlagPaymentNotification(createDTO.getFlagPaymentNotification());
    organization.setPdndEnabled(createDTO.getPdndEnabled());

    return organization;
  }

}
