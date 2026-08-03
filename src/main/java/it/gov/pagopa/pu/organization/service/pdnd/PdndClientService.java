package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndClientResponse;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.exception.custom.NotFoundException;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.mapper.PdndClientMapper;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import it.gov.pagopa.pu.organization.repository.PdndClientRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PdndClientService {
  private final PdndClientRepository pdndClientRepository;
  private final OrganizationRepository organizationRepository;
  private final OrgSubUnitRepository orgSubUnitRepository;
  private final PdndClientMapper pdndClientMapper;

  public PdndClientService(PdndClientRepository pdndClientRepository, OrganizationRepository organizationRepository, OrgSubUnitRepository orgSubUnitRepository, PdndClientMapper pdndClientMapper) {
    this.pdndClientRepository = pdndClientRepository;
    this.organizationRepository = organizationRepository;
    this.orgSubUnitRepository = orgSubUnitRepository;
    this.pdndClientMapper = pdndClientMapper;
  }

  @Transactional
  public PdndClient savePdndClient(PdndClientDTO pdndClientDTO) {
    validatePdndClient(pdndClientDTO);
    return pdndClientRepository.save(pdndClientMapper.toModel(pdndClientDTO));
  }

  private void validatePdndClient(PdndClientDTO pdndClientDTO) {
    organizationRepository.findById(pdndClientDTO.getOrganizationId())
      .orElseThrow(() -> new OrganizationNotFoundException("Organization having organizationId %d not found".formatted(pdndClientDTO.getOrganizationId())));
    if(StringUtils.isNotBlank(pdndClientDTO.getSubUnitCode())){
     orgSubUnitRepository.findById(new OrgSubUnit.OrgSubUnitId(pdndClientDTO.getOrganizationId(), pdndClientDTO.getSubUnitCode()))
       .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_ORG_SUB_UNIT_NOT_FOUND,
         "OrgSubUnit having organizationId %d and subUnitCode %s not found".formatted(pdndClientDTO.getOrganizationId(), pdndClientDTO.getSubUnitCode())));
    }
  }

  public PdndClientDTO getUsablePdndClientByOrganizationIdAndPdndServiceType(Long organizationId, PdndServiceType pdndServiceType, String subUnitCode) {
    PdndClient pdndClient = pdndClientRepository.findUsableByOrganizationIdAndServiceTypeAndSubUnitCode(organizationId, pdndServiceType, subUnitCode)
      .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_NOT_FOUND,
        "PdndClient having organizationId %d and pdndServiceType %s and subUnitCode %s not found".formatted(organizationId, pdndServiceType, subUnitCode)));
    return pdndClientMapper.toDTO(pdndClient);
  }

  public List<PdndClientResponse> getPdndClientsByOrganizationIdAndSubUnitCode(Long organizationId, String subUnitCode) {
    List<PdndClient> pdndClients;

    if (subUnitCode == null) {
      pdndClients = pdndClientRepository.findAllByOrganizationIdAndSubUnitCodeIsNull(organizationId);
    } else {
      pdndClients = pdndClientRepository.findAllByOrganizationIdAndSubUnitCode(organizationId, subUnitCode);
    }

    return pdndClients.stream()
      .map(pdndClientMapper::mapToPdndClientResponse)
      .toList();
  }
}
