package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.model.PdndService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PdndServiceMapper {

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
}
