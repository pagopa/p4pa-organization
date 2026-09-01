package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.exception.common.ConflictException;
import it.gov.pagopa.pu.organization.exception.common.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.common.NotFoundException;
import it.gov.pagopa.pu.organization.mapper.PdndServiceMapper;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.repository.PdndServiceRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PdndServiceService {

  private final PdndServiceRepository pdndServiceRepository;
  private final PdndServiceMapper pdndServiceMapper;
  private final PdndClientService pdndClientService;

  public PdndServiceService(PdndServiceRepository pdndServiceRepository, PdndServiceMapper pdndServiceMapper, PdndClientService pdndClientService) {
    this.pdndServiceRepository = pdndServiceRepository;
    this.pdndServiceMapper = pdndServiceMapper;
    this.pdndClientService = pdndClientService;
  }

  @Transactional
  public PdndService savePdndService(Long organizationId, PdndServiceRequestDTO requestDTO, String subUnitCode) {
    List<PdndService> pdndServices = pdndServiceRepository.findByOrganizationIdAndServiceTypeAndSubUnitCode(organizationId, requestDTO.getServiceType(), subUnitCode);
    if (!pdndServices.isEmpty()) {
      throw new ConflictException(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_SERVICE_TYPE,
        String.format("PdndService having pdndServiceType %s already exists for organizationId %s and subUnitCode %s", requestDTO.getServiceType(), organizationId, subUnitCode));
    }

    try{
      PdndClientDTO pdndClientDTO = pdndClientService.getUsablePdndClientByOrganizationIdAndPdndServiceType(organizationId, requestDTO.getServiceType(), subUnitCode);
      if(pdndClientDTO!=null){
        throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_SERVICE_TYPE,
          String.format("PdndServiceType %s already in use for organizationId %s and subUnitCode %s", requestDTO.getServiceType(), organizationId, subUnitCode)
        );
      }
    }catch (NotFoundException ignored){
      // getUsablePdndClientByOrganizationIdAndPdndServiceType throw NotFoundExeption if not exists PdndClient,
      // but in this case we need to test if no other pdnd-services already exists with same type for this org or subunit to proceed
    }

    return pdndServiceRepository.save(pdndServiceMapper.toModel(requestDTO));
  }

  public List<PdndServiceDTO> getPdndServices(Long organizationId, PdndServiceType serviceType, String subUnitCode) {
    List<PdndService> pdndServices = pdndServiceRepository.findByOrganizationIdAndServiceTypeAndSubUnitCode(organizationId, serviceType, subUnitCode);
    return pdndServices.stream()
      .map(pdndServiceMapper::toPdndServiceDTO)
      .toList();
  }

  public PdndServiceDTO getPdndService(Long organizationId, String purposeId, String subUnitCode) {
    return pdndServiceMapper.toPdndServiceDTO(findPdndService(organizationId, purposeId, subUnitCode));
  }

  @Transactional
  public void deletePdndService(Long organizationId, String purposeId, String subUnitCode) {
    PdndService pdndService = findPdndService(organizationId, purposeId, subUnitCode);
    pdndServiceRepository.delete(pdndService);
  }

  private PdndService findPdndService(Long organizationId, String purposeId, String subUnitCode) {
    return pdndServiceRepository.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, subUnitCode)
      .orElseThrow(() -> new NotFoundException(
        ErrorCodeConstants.ERROR_CODE_PDND_SERVICE_NOT_FOUND,
        "PdndService having purposeId %s, organizationId %d and subUnitCode %s not found".formatted(purposeId, organizationId, subUnitCode)));
  }
}
