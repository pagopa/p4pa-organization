package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    organization.setCbillInterBankCode(createDTO.getCbillInterBankCode());
    organization.setOrgLogo(createDTO.getOrgLogo());
    organization.setStatus(createDTO.getStatus());
    organization.setAdditionalLanguage(createDTO.getAdditionalLanguage());
    organization.setStartDate(createDTO.getStartDate());
    organization.setBrokerId(createDTO.getBrokerId());
    organization.setFlagNotifyIo(createDTO.getFlagNotifyIo());
    organization.setFlagNotifyOutcomePush(createDTO.getFlagNotifyOutcomePush());
    organization.setFlagPaymentNotification(createDTO.getFlagPaymentNotification());
    organization.setFlagTreasury(createDTO.getFlagTreasury());
    organization.setFlagPaymentsReporting(Optional.ofNullable(createDTO.getFlagPaymentsReporting()).orElse(Boolean.TRUE));
    organization.setFlagClassification(Optional.ofNullable(createDTO.getFlagClassification()).orElse(Boolean.TRUE));
    organization.setPdndEnabled(createDTO.getPdndEnabled());
    organization.setAddress(createDTO.getAddress());
    organization.setZipCode(createDTO.getZipCode());
    organization.setCity(createDTO.getCity());

    return organization;
  }

  public OrganizationDetailDTO mapToDTO(Organization org, String segregationCode) {
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
    dto.setSegregationCode(segregationCode);
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
    dto.setFlagPaymentsReporting(org.isFlagPaymentsReporting());
    dto.setFlagClassification(org.isFlagClassification());
    dto.setBrokerId(org.getBrokerId());
    dto.setAddress(org.getAddress());
    dto.setZipCode(org.getZipCode());
    dto.setCity(org.getCity());

    return dto;
  }

  public Organization toModel(OrganizationDetailDTO organizationDetailDTO) {
    if (organizationDetailDTO == null) {
      return null;
    }
    Organization organization = toModel((OrganizationCreateDTO) organizationDetailDTO);
    organization.setOrganizationId(organizationDetailDTO.getOrganizationId());
    organization.setFlagTreasury(organizationDetailDTO.getFlagTreasury());
    organization.setDefaultOrganizationStationId(organizationDetailDTO.getDefaultOrganizationStationId());
    return organization;
  }
}
