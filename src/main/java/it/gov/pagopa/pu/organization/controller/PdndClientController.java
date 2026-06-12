package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.PdndClientApi;
import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.service.pdnd.PdndClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PdndClientController implements PdndClientApi {

  private final PdndClientService service;

  public PdndClientController(PdndClientService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<PdndClient> createPdndClient(PdndClientDTO pdndClientDTO) {
    log.info("Requested createPdndClient with clientId {}, organizationId {} and subUnitCode {}", pdndClientDTO.getClientId(), pdndClientDTO.getOrganizationId(), pdndClientDTO.getSubUnitCode());
    return ResponseEntity.ok(service.createPdndClient(pdndClientDTO));
  }
}
