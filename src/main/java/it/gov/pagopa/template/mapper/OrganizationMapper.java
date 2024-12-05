package it.gov.pagopa.template.mapper;

import it.gov.pagopa.template.dto.OrganizationDTO;
import it.gov.pagopa.template.entity.Organization;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

  public static OrganizationDTO toDTO(Organization organization) {
    if (organization == null) {
      return null;
    }

    OrganizationDTO dto = new OrganizationDTO();
    dto.setOrgId(organization.getOrgId());
    dto.setIpaCode(organization.getIpaCode());
    dto.setOrgFiscalCode(organization.getOrgFiscalCode());
    dto.setOrgName(organization.getOrgName());
    dto.setAdminEmail(organization.getAdminEmail());
    dto.setCreationDate(organization.getCreationDate());
    dto.setLastUpdateDate(organization.getLastUpdateDate());
    dto.setPaymentTypeCode(organization.getPaymentTypeCode());
    dto.setFee(organization.getFee());
    dto.setIban(organization.getIban());
    dto.setMyBoxClientKey(organization.getMyBoxClientKey());
    dto.setMyBoxClientSecret(organization.getMyBoxClientSecret());
    dto.setUrlOrgSendSILPaymentResult(organization.getUrlOrgSendSILPaymentResult());
    dto.setCodeGlobalLocationNumber(organization.getCodeGlobalLocationNumber());
    dto.setPassword(organization.getPassword());
    dto.setCreditBicSeller(organization.getCreditBicSeller());
    dto.setBeneficiaryOrgName(organization.getBeneficiaryOrgName());
    dto.setBeneficiaryOrgAddress(organization.getBeneficiaryOrgAddress());
    dto.setBeneficiaryOrgCivic(organization.getBeneficiaryOrgCivic());
    dto.setBeneficiaryOrgPostalCode(organization.getBeneficiaryOrgPostalCode());
    dto.setBeneficiaryOrgLocation(organization.getBeneficiaryOrgLocation());
    dto.setBeneficiaryOrgProvince(organization.getBeneficiaryOrgProvince());
    dto.setBeneficiaryOrgNation(organization.getBeneficiaryOrgNation());
    dto.setBeneficiaryOrgPhoneNumber(organization.getBeneficiaryOrgPhoneNumber());
    dto.setBeneficiaryOrgWebSite(organization.getBeneficiaryOrgWebSite());
    dto.setBeneficiaryOrgEmail(organization.getBeneficiaryOrgEmail());
    dto.setApplicationCode(organization.getApplicationCode());
    dto.setCbillInterbankCode(organization.getCbillInterbankCode());
    dto.setOrgInformation(organization.getOrgInformation());
    dto.setOrgLogoDesc(organization.getOrgLogoDesc());
    dto.setAuthorizationDesc(organization.getAuthorizationDesc());
    dto.setStatus(organization.getStatus());
    dto.setUrlActiveExternal(organization.getUrlActiveExternal());
    dto.setAdditionalLanguage(organization.getAdditionalLanguage());
    dto.setOrgTypeCode(organization.getOrgTypeCode());
    dto.setStartDate(organization.getStartDate());
    dto.setBrokerId(organization.getBrokerId());

    return dto;
  }

}
