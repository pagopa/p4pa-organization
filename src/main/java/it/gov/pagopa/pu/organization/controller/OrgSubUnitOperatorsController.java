package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.OrgSubUnitOperatorsApi;
import it.gov.pagopa.pu.organization.service.orgsubunitoperators.OrgSubUnitOperatorsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class OrgSubUnitOperatorsController implements OrgSubUnitOperatorsApi {

  private final OrgSubUnitOperatorsService orgSubUnitOperatorsService;

  public OrgSubUnitOperatorsController(OrgSubUnitOperatorsService orgSubUnitOperatorsService) {
    this.orgSubUnitOperatorsService = orgSubUnitOperatorsService;
  }

  @Override
  public ResponseEntity<Void> addOrgSubUnitsToOperator(Long organizationId, String mappedExternalUserId, List<String> orgSubUnitCodes) {
    log.info("Requested to add orgSubUnits {} to operator {} for organization {}", orgSubUnitCodes, mappedExternalUserId, organizationId);
    orgSubUnitOperatorsService.addOrgSubUnitsToOperator(organizationId, mappedExternalUserId, orgSubUnitCodes);
    return ResponseEntity.ok().build();
  }
}
