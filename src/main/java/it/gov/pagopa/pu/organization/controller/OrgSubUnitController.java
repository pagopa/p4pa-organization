package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.OrgSubUnitApi;
import it.gov.pagopa.pu.organization.dto.OrgAndSubUnitDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.service.orgsubunit.OrgSubUnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class OrgSubUnitController implements OrgSubUnitApi {

  private final OrgSubUnitService orgSubUnitService;

  public OrgSubUnitController(OrgSubUnitService orgSubUnitService) {
    this.orgSubUnitService = orgSubUnitService;
  }

  @Override
  public ResponseEntity<List<OrgAndSubUnitDTO>> getOrgSubUnitWithNoServiceType(Long organizationId, PdndServiceType pdndServiceType) {
    log.info("Retrieving orgSubUnit for organization {} having no serviceType {} configured", organizationId, pdndServiceType);
    return ResponseEntity.ok(orgSubUnitService.getOrgSubUnitWithNoServiceType(organizationId, pdndServiceType));
  }
}
