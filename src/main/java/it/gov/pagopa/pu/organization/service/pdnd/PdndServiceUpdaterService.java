package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PdndServiceUpdateDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.exception.common.ConflictException;
import it.gov.pagopa.pu.organization.exception.common.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.common.NotFoundException;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.repository.PdndClientRepository;
import it.gov.pagopa.pu.organization.repository.PdndServiceRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class PdndServiceUpdaterService {

  private final PdndServiceRepository pdndServiceRepository;
  private final PdndClientRepository pdndClientRepository;

  public PdndServiceUpdaterService(PdndServiceRepository pdndServiceRepository, PdndClientRepository pdndClientRepository) {
    this.pdndServiceRepository = pdndServiceRepository;
    this.pdndClientRepository = pdndClientRepository;
  }

  @Transactional
  public PdndService updatePdndService(String purposeId, PdndServiceUpdateDTO updateDTO) {
    PdndService pdndService = pdndServiceRepository.findById(purposeId)
      .orElseThrow(() -> new NotFoundException(
        ErrorCodeConstants.ERROR_CODE_PDND_SERVICE_NOT_FOUND,
        "PdndService having purposeId %s not found".formatted(purposeId)));

    if (!pdndService.getClientId().equals(updateDTO.getClientId())) {
      validateNewPdndClient(pdndService.getClientId(), updateDTO.getClientId(), pdndService.getServiceType());
    }

    pdndService.setServiceName(updateDTO.getServiceName());
    pdndService.setClientId(updateDTO.getClientId());
    return pdndServiceRepository.save(pdndService);
  }

  private void validateNewPdndClient(String oldClientId, String newClientId, PdndServiceType serviceType) {
    PdndClient oldPdndClient = findPdndClient(oldClientId);
    PdndClient newPdndClient = findPdndClient(newClientId);

    if (!oldPdndClient.getOrganizationId().equals(newPdndClient.getOrganizationId())
      || !Objects.equals(oldPdndClient.getSubUnitCode(), newPdndClient.getSubUnitCode())) {
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_CLIENT,
        "PdndClient having clientId %s is not associated with the same organization and subUnit of PdndClient having clientId %s".formatted(newClientId, oldClientId));
    }

    if (pdndServiceRepository.findByClientIdAndServiceType(newClientId, serviceType) != null) {
      throw new ConflictException(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_SERVICE_TYPE,
        "PdndService having pdndServiceType %s already exists for clientId %s".formatted(serviceType, newClientId));
    }
  }

  private PdndClient findPdndClient(String clientId) {
    return pdndClientRepository.findById(clientId)
      .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_NOT_FOUND,
        "PdndClient having clientId %s not found".formatted(clientId)));
  }
}
