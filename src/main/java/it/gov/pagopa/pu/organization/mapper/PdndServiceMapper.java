package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.service.pdnd.PdndClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PdndServiceMapper {

  private final PdndClientService pdndClientService;

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

  public PdndServiceDTO toPdndServiceDTO(Long organizationId, PdndService pdndService, String subUnitCode) {
    if (pdndService == null) {
      return null;
    }

    PdndServiceDTO dto = new PdndServiceDTO();
    dto.setPurposeId(pdndService.getPurposeId());
    dto.setClientId(pdndService.getClientId());
    dto.setServiceType(pdndService.getServiceType());
    dto.setServiceName(pdndService.getServiceName());

    PdndClientDTO clientDTO = pdndClientService.getUsablePdndClientByOrganizationIdAndPdndServiceType(organizationId, pdndService.getServiceType(), subUnitCode);
    dto.setClientName(clientDTO.getClientName());

    return dto;
  }
}
