package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.service.pdnd.PdndClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

  public List<PdndServiceDTO> toPdndServiceDTO(Long organizationId, List<PdndService> pdndServices, String subUnitCode) {
    if (pdndServices == null || pdndServices.isEmpty()) {
      return Collections.emptyList();
    }

    return pdndServices.stream()
      .map(pdndService -> {
        PdndServiceDTO dto = new PdndServiceDTO();
        dto.setPurposeId(pdndService.getPurposeId());
        dto.setClientId(pdndService.getClientId());
        dto.setServiceType(pdndService.getServiceType());
        dto.setServiceName(pdndService.getServiceName());

        PdndClientDTO clientDTO = pdndClientService.getUsablePdndClientByOrganizationIdAndPdndServiceType(organizationId, pdndService.getServiceType(), subUnitCode);
        dto.setClientName(clientDTO.getClientName());

        return dto;
      })
      .collect(Collectors.toList());
  }
}
