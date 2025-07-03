package it.gov.pagopa.pu.organization.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.model.OrgSilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrgSilServiceMapper {

  private final ObjectMapper objectMapper;

  public OrgSilServiceDTO fromEntity(OrgSilService orgSilService) {
    if (orgSilService == null) {
      return null;
    }

    OrgSilServiceDTO dto = new OrgSilServiceDTO();
    dto.setOrgSilServiceId(orgSilService.getOrgSilServiceId());
    dto.setOrganizationId(orgSilService.getOrganizationId());
    dto.setServiceType(orgSilService.getServiceType());
    dto.setServiceUrl(orgSilService.getServiceUrl());
    dto.setApplicationName(orgSilService.getApplicationName());
    dto.setFlagLegacy(orgSilService.isFlagLegacy());
    dto.setAuthConfig(orgSilService.getAuthConfig());

    return dto;
  }

  public OrgSilService fromDTO(OrgSilServiceDTO dto) {
    if (dto == null) {
      return null;
    }

    OrgSilService orgSilService = new OrgSilService();
    orgSilService.setOrgSilServiceId(dto.getOrgSilServiceId());
    orgSilService.setOrganizationId(dto.getOrganizationId());
    orgSilService.setServiceType(dto.getServiceType());
    orgSilService.setServiceUrl(dto.getServiceUrl());
    orgSilService.setApplicationName(dto.getApplicationName());
    orgSilService.setFlagLegacy(dto.getFlagLegacy());
    orgSilService.setAuthConfig(dto.getAuthConfig());

    return orgSilService;
  }

}
