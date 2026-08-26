package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
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
          String.format("PdndService having pdndServiceType %s not usable for organizationId %s and subUnitCode %s", requestDTO.getServiceType(), organizationId, subUnitCode)
        );
      }
    }catch (NotFoundException ignored){}

    return pdndServiceRepository.save(pdndServiceMapper.toModel(requestDTO));
  }

}
