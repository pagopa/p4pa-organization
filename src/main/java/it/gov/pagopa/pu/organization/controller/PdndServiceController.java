package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.PdndServiceApi;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.service.pdnd.PdndServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class PdndServiceController implements PdndServiceApi {

  private final PdndServiceService service;

  public PdndServiceController(PdndServiceService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<List<PdndServiceDTO>> getPdndServices(Long organizationId, String subUnitCode, PdndServiceType serviceType) {
    log.info("Requested PdndServices for organizationId {}", organizationId);
    return ResponseEntity.ok(service.getPdndServices(organizationId, serviceType, subUnitCode));
  }

  @Override
  public ResponseEntity<PdndService> savePdndService(Long organizationId, PdndServiceRequestDTO pdndServiceRequestDTO, String subUnitCode) {
    log.info("Requested savePdndService with purposeId {}", pdndServiceRequestDTO.getPurposeId());
    return ResponseEntity.ok(service.savePdndService(organizationId, pdndServiceRequestDTO, subUnitCode));
  }

  @Override
  public ResponseEntity<PdndService> getPdndService(Long organizationId, String purposeId) {
    log.info("Requested getPdndService having organizationId {} and purposeId {}", organizationId, purposeId);
    return ResponseEntity.ok(service.getPdndService(organizationId, purposeId));
  }
}
