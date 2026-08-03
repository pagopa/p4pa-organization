package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.PdndClientApi;
import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndClientResponse;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.service.pdnd.PdndClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class PdndClientController implements PdndClientApi {

  private final PdndClientService service;

  public PdndClientController(PdndClientService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<PdndClient> savePdndClient(PdndClientDTO pdndClientDTO) {
    log.info("Requested savePdndClient with clientId {}, organizationId {} and subUnitCode {}", pdndClientDTO.getClientId(), pdndClientDTO.getOrganizationId(), pdndClientDTO.getSubUnitCode());
    return ResponseEntity.ok(service.savePdndClient(pdndClientDTO));
  }

  @Override
  public ResponseEntity<PdndClientDTO> getUsablePdndClientByOrganizationIdAndPdndServiceType(Long organizationId, PdndServiceType pdndServiceType, String subUnitCode) {
    log.info("Requested PDND client of organizationId {} and subUnitCode {} related to PDND service type {}", organizationId, subUnitCode, pdndServiceType);
    return ResponseEntity.ok(service.getUsablePdndClientByOrganizationIdAndPdndServiceType(organizationId,pdndServiceType,subUnitCode));
  }

  @Override
  public ResponseEntity<List<PdndClientResponse>> getPdndClientsByOrganizationIdAndSubUnitCode(Long organizationId, String subUnitCode) {
    log.info("Requested PDND client of organizationId {} and subUnitCode {}", organizationId, subUnitCode);
    return ResponseEntity.ok(service.getPdndClientsByOrganizationIdAndSubUnitCode(organizationId,subUnitCode));
  }
}
