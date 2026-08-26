package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.PdndServiceApi;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.service.pdnd.PdndServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PdndServiceController implements PdndServiceApi {

  private final PdndServiceService service;

  public PdndServiceController(PdndServiceService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<PdndService> savePdndService(Long organizationId, PdndServiceRequestDTO pdndServiceRequestDTO, String subUnitCode) {
    log.info("Requested savePdndService with purposeId {}", pdndServiceRequestDTO.getPurposeId());
    return ResponseEntity.ok(service.savePdndService(organizationId, pdndServiceRequestDTO, subUnitCode));
  }
}
