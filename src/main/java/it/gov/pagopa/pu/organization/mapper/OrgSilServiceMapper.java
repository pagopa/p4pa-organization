package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.dto.orgsilservice.SilServiceAuthConfigDTO;
import it.gov.pagopa.pu.organization.dto.orgsilservice.SilServiceLegacyBasicAuthConfigDTO;
import it.gov.pagopa.pu.organization.dto.orgsilservice.SilServiceLegacyJwtAuthConfigDTO;
import it.gov.pagopa.pu.organization.model.orgsilservice.OrgSilService;
import it.gov.pagopa.pu.organization.model.orgsilservice.SilServiceAuthConfig;
import it.gov.pagopa.pu.organization.model.orgsilservice.SilServiceLegacyBasicAuthConfig;
import it.gov.pagopa.pu.organization.model.orgsilservice.SilServiceLegacyJwtAuthConfig;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrgSilServiceMapper {

  private final OrganizationEncryptionService encryptionService;

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
    dto.setAuthConfig(toAuthConfigDTO(orgSilService.getAuthConfig()));

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
    orgSilService.setAuthConfig(fromAuthConfigDTO(dto.getAuthConfig()));

    return orgSilService;
  }

  private SilServiceAuthConfig fromAuthConfigDTO(SilServiceAuthConfigDTO dto) {
    if (dto == null) {
      return null;
    }

    return switch (dto) {
      case SilServiceLegacyBasicAuthConfigDTO basicAuthConfigDTO -> fromLegacyBasicAuthConfigDTO(basicAuthConfigDTO);
      case SilServiceLegacyJwtAuthConfigDTO legacyJwtAuthConfigDTO -> fromLegacyJwtAuthConfigDTO(legacyJwtAuthConfigDTO);
      default -> null;
    };
  }

  private SilServiceLegacyBasicAuthConfig fromLegacyBasicAuthConfigDTO(SilServiceLegacyBasicAuthConfigDTO dto) {
    if (dto == null) {
      return null;
    }

    SilServiceLegacyBasicAuthConfig legacyBasicAuthConfig = new SilServiceLegacyBasicAuthConfig();
    legacyBasicAuthConfig.setAuthUrl(dto.getAuthUrl());
    legacyBasicAuthConfig.setUser(encryptionService.encrypt(dto.getUser()));
    legacyBasicAuthConfig.setPsw(encryptionService.encrypt(dto.getPsw()));

    return legacyBasicAuthConfig;
  }

  private SilServiceLegacyJwtAuthConfig fromLegacyJwtAuthConfigDTO(SilServiceLegacyJwtAuthConfigDTO dto) {
    if (dto == null) {
      return null;
    }

    SilServiceLegacyJwtAuthConfig legacyJwtAuthConfig = new SilServiceLegacyJwtAuthConfig();
    legacyJwtAuthConfig.setKid(dto.getKid());
    legacyJwtAuthConfig.setSubject(dto.getSubject());
    legacyJwtAuthConfig.setIssuer(dto.getIssuer());
    legacyJwtAuthConfig.setAlgorithm(dto.getAlgorithm());
    legacyJwtAuthConfig.setSigningKey(encryptionService.encrypt(dto.getSigningKey()));

    return legacyJwtAuthConfig;
  }

  private SilServiceAuthConfigDTO toAuthConfigDTO(SilServiceAuthConfig authConfig) {
    if (authConfig == null) {
      return null;
    }

    return switch (authConfig) {
      case SilServiceLegacyBasicAuthConfig basicAuthConfig -> toLegacyBasicAuthConfigDTO(basicAuthConfig);
      case SilServiceLegacyJwtAuthConfig jwtAuthConfig -> toLegacyJwtAuthConfigDTO(jwtAuthConfig);
      default -> null;
    };
  }

  private SilServiceLegacyBasicAuthConfigDTO toLegacyBasicAuthConfigDTO(SilServiceLegacyBasicAuthConfig basicAuthConfig) {
    if (basicAuthConfig == null) {
      return null;
    }

    SilServiceLegacyBasicAuthConfigDTO legacyBasicAuthConfigDTO = new SilServiceLegacyBasicAuthConfigDTO();
    legacyBasicAuthConfigDTO.setAuthUrl(basicAuthConfig.getAuthUrl());
    legacyBasicAuthConfigDTO.setUser(encryptionService.decryptKey(basicAuthConfig.getUser()));
    legacyBasicAuthConfigDTO.setPsw(encryptionService.decryptKey(basicAuthConfig.getPsw()));

    return legacyBasicAuthConfigDTO;
  }

  private SilServiceLegacyJwtAuthConfigDTO toLegacyJwtAuthConfigDTO(SilServiceLegacyJwtAuthConfig jwtAuthConfig) {
    if (jwtAuthConfig == null) {
      return null;
    }

    SilServiceLegacyJwtAuthConfigDTO legacyJwtAuthConfigDTO = new SilServiceLegacyJwtAuthConfigDTO();
    legacyJwtAuthConfigDTO.setKid(jwtAuthConfig.getKid());
    legacyJwtAuthConfigDTO.setSubject(jwtAuthConfig.getSubject());
    legacyJwtAuthConfigDTO.setIssuer(jwtAuthConfig.getIssuer());
    legacyJwtAuthConfigDTO.setAlgorithm(jwtAuthConfig.getAlgorithm());
    legacyJwtAuthConfigDTO.setSigningKey(encryptionService.decryptKey(jwtAuthConfig.getSigningKey()));

    return legacyJwtAuthConfigDTO;
  }

}
