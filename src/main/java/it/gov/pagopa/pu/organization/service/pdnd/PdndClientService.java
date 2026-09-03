package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndClientNoSecretDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.exception.common.ConflictException;
import it.gov.pagopa.pu.organization.exception.common.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.common.NotFoundException;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.mapper.PdndClientMapper;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import it.gov.pagopa.pu.organization.repository.PdndClientRepository;
import it.gov.pagopa.pu.organization.repository.PdndServiceRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PdndClientService {
  private final PdndClientRepository pdndClientRepository;
  private final PdndServiceRepository pdndServiceRepository;
  private final OrganizationRepository organizationRepository;
  private final OrgSubUnitRepository orgSubUnitRepository;
  private final PdndClientMapper pdndClientMapper;

  public PdndClientService(
    PdndClientRepository pdndClientRepository,
    PdndServiceRepository pdndServiceRepository,
    OrganizationRepository organizationRepository,
    OrgSubUnitRepository orgSubUnitRepository,
    PdndClientMapper pdndClientMapper) {
    this.pdndClientRepository = pdndClientRepository;
    this.pdndServiceRepository = pdndServiceRepository;
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

  public List<PdndClientNoSecretDTO> getPdndClientsByOrganizationIdAndSubUnitCode(Long organizationId, String subUnitCode) {
    List<PdndClient> pdndClients;

    if (subUnitCode == null) {
      pdndClients = pdndClientRepository.findAllByOrganizationIdAndSubUnitCodeIsNull(organizationId);
    } else {
      pdndClients = pdndClientRepository.findAllByOrganizationIdAndSubUnitCode(organizationId, subUnitCode);
    }

    return pdndClients.stream()
      .map(pdndClientMapper::mapToPdndClientNoSecretDTO)
      .toList();
  }

  public PdndClientNoSecretDTO getPdndClientDetail(Long organizationId, String clientId) {
    PdndClient pdndClient = pdndClientRepository.findByClientIdAndOrganizationId(clientId, organizationId)
      .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_NOT_FOUND,
        "PdndClient having clientId %s and organizationId %d not found".formatted(clientId, organizationId)));

    return pdndClientMapper.mapToPdndClientNoSecretDTO(pdndClient);
  }

  @Transactional
  public void deletePdndClient(Long organizationId, String clientId) {
    PdndClient pdndClient = pdndClientRepository.findById(clientId)
      .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_NOT_FOUND,
        "PdndClient having clientId %s not found".formatted(clientId)));

    if (!organizationId.equals(pdndClient.getOrganizationId())) {
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_CLIENT,
        "PdndClient having clientId %s is not associated with organizationId %d".formatted(clientId, organizationId));
    }

    if (pdndServiceRepository.existsByClientId(clientId)) {
      throw new ConflictException(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_ID_IN_USE,
        "PdndClient having clientId %s cannot be deleted because it is referenced by PdndServices".formatted(clientId));
    }

    pdndClientRepository.delete(pdndClient);
  }
}
