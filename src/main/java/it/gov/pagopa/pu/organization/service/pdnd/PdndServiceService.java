package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.exception.common.ConflictException;
import it.gov.pagopa.pu.organization.exception.common.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.common.NotFoundException;
import it.gov.pagopa.pu.organization.mapper.PdndServiceMapper;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.model.view.PdndServiceView;
import it.gov.pagopa.pu.organization.repository.PdndServiceRepository;
import it.gov.pagopa.pu.organization.repository.view.PdndServiceViewRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PdndServiceService {

  private final PdndServiceRepository pdndServiceRepository;
  private final PdndServiceMapper pdndServiceMapper;
  private final PdndClientService pdndClientService;
  private final PdndServiceViewRepository pdndServiceViewRepository;

  public PdndServiceService(PdndServiceRepository pdndServiceRepository, PdndServiceMapper pdndServiceMapper, PdndClientService pdndClientService, PdndServiceViewRepository pdndServiceViewRepository) {
    this.pdndServiceRepository = pdndServiceRepository;
    this.pdndServiceMapper = pdndServiceMapper;
    this.pdndClientService = pdndClientService;
    this.pdndServiceViewRepository = pdndServiceViewRepository;
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

  public List<PdndServiceView> getPdndServices(Long organizationId, PdndServiceType serviceType, String subUnitCode) {
    return pdndServiceViewRepository.findByOrganizationIdAndServiceTypeAndSubUnitCode(organizationId, serviceType, subUnitCode);
  }

  public PdndServiceView getPdndService(Long organizationId, String purposeId) {
    return pdndServiceViewRepository.findByOrganizationIdAndPurposeId(organizationId, purposeId)
      .orElseThrow(() -> new NotFoundException(
        ErrorCodeConstants.ERROR_CODE_PDND_SERVICE_NOT_FOUND,
        "PdndService having purposeId %s, organizationId %d not found".formatted(purposeId, organizationId)));
  }

  @Transactional
  public void deletePdndService(String purposeId) {
    PdndService pdndService = findPdndService(purposeId);
    pdndServiceRepository.delete(pdndService);
  }

  private PdndService findPdndService(String purposeId) {
    return pdndServiceRepository.findById(purposeId)
      .orElseThrow(() -> new NotFoundException(
        ErrorCodeConstants.ERROR_CODE_PDND_SERVICE_NOT_FOUND,
        "PdndService having purposeId %s not found".formatted(purposeId)));
  }
}
