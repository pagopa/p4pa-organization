package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.repository.PdndClientRepository;
import it.gov.pagopa.pu.organization.service.pdnd.PdndClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PdndServiceMapper {

  private final PdndClientService pdndClientService;
  private final PdndClientRepository pdndClientRepository;

  public PdndService toModel(PdndServiceRequestDTO requestDTO) {
    if(requestDTO == null) {
      return null;
    }

    PdndService pdndService = new PdndService();
    pdndService.setPurposeId(requestDTO.getPurposeId());
    pdndService.setServiceName(requestDTO.getServiceName());
    pdndService.setServiceType(requestDTO.getServiceType());
    pdndService.setClientId(requestDTO.getClientId());

    return pdndService;
  }

  public PdndServiceDTO toPdndServiceDTO(PdndService pdndService) {
    if (pdndService == null) {
      return null;
    }

    PdndServiceDTO dto = new PdndServiceDTO();
    dto.setPurposeId(pdndService.getPurposeId());
    dto.setClientId(pdndService.getClientId());
    dto.setServiceType(pdndService.getServiceType());
    dto.setServiceName(pdndService.getServiceName());

    pdndClientRepository.findById(pdndService.getClientId())
      .ifPresent(pdndClient -> dto.setClientName(pdndClient.getClientName()));

    return dto;
  }
}
