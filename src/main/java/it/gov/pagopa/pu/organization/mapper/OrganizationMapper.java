package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.OrganizationUpdateDTO;
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
    organization.setPassword(createDTO.getPassword() != null ? encryptionService.encrypt(createDTO.getPassword()) : null);
    organization.setSegregationCode(createDTO.getSegregationCode());
    organization.setCbillInterBankCode(createDTO.getCbillInterBankCode());
    organization.setOrgLogo(createDTO.getOrgLogo());
    organization.setStatus(createDTO.getStatus());
    organization.setAdditionalLanguage(createDTO.getAdditionalLanguage());
    organization.setStartDate(createDTO.getStartDate());
    organization.setBrokerId(createDTO.getBrokerId());
    organization.setIoApiKey(createDTO.getIoApiKey() != null ? encryptionService.encrypt(createDTO.getIoApiKey()) : null);
    organization.setSendApiKey(createDTO.getSendApiKey() != null ? encryptionService.encrypt(createDTO.getSendApiKey()) : null);
    organization.setGenerateNoticeApiKey(createDTO.getGenerateNoticeApiKey() != null ? encryptionService.encrypt(createDTO.getGenerateNoticeApiKey()) : null);
    organization.setFlagNotifyIo(createDTO.getFlagNotifyIo());
    organization.setFlagNotifyOutcomePush(createDTO.getFlagNotifyOutcomePush());
    organization.setFlagPaymentNotification(createDTO.getFlagPaymentNotification());
    organization.setPdndEnabled(createDTO.getPdndEnabled());

    return organization;
  }

  public OrganizationDetailDTO mapToDTO(Organization org) {
    if (org == null) {
      return null;
    }

    OrganizationDetailDTO dto = new OrganizationDetailDTO();
    dto.setPassword(encryptionService.decryptKey(org.getPassword()));
    dto.setGenerateNoticeApiKey(encryptionService.decryptKey(org.getGenerateNoticeApiKey()));
    dto.setIoApiKey(encryptionService.decryptKey(org.getIoApiKey()));
    dto.setSendApiKey(encryptionService.decryptKey(org.getSendApiKey()));
    dto.setOrganizationId(org.getOrganizationId());
    dto.setExternalOrganizationId(org.getExternalOrganizationId());
    dto.setIpaCode(org.getIpaCode());
    dto.setOrgFiscalCode(org.getOrgFiscalCode());
    dto.setOrgName(org.getOrgName());
    dto.setOrgTypeCode(org.getOrgTypeCode());
    dto.setOrgEmail(org.getOrgEmail());
    dto.setPostalIban(org.getPostalIban());
    dto.setIban(org.getIban());
    dto.setSegregationCode(org.getSegregationCode());
    dto.setCbillInterBankCode(org.getCbillInterBankCode());
    dto.setOrgLogo(org.getOrgLogo());
    dto.setStatus(org.getStatus());
    dto.setAdditionalLanguage(org.getAdditionalLanguage());
    dto.setStartDate(org.getStartDate());
    dto.setFlagNotifyIo(org.isFlagNotifyIo());
    dto.setFlagNotifyOutcomePush(org.isFlagNotifyOutcomePush());
    dto.setFlagPaymentNotification(org.isFlagPaymentNotification());
    dto.setPdndEnabled(org.isPdndEnabled());
    dto.setFlagTreasury(org.isFlagTreasury());
    dto.setBrokerId(org.getBrokerId());

    return dto;
  }

  public Organization toModel(OrganizationUpdateDTO organizationUpdateDTO) {
    if (organizationUpdateDTO == null) {
      return null;
    }
    Organization organization = toModel((OrganizationCreateDTO) organizationUpdateDTO);
    organization.setOrganizationId(organizationUpdateDTO.getOrganizationId());
    organization.setFlagTreasury(organizationUpdateDTO.isFlagTreasury());
    return organization;
  }
}
