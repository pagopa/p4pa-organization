package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.OrgSubUnitEntityExtendedControllerApi;
import it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import it.gov.pagopa.pu.organization.util.SecurityUtils;
import it.gov.pagopa.pu.organization.util.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/** Controller to host spring-data-rest directly not supported methods */
@RestController
@Slf4j
public class OrgSubUnitEntityExtendedController implements OrgSubUnitEntityExtendedControllerApi {
  private final OrgSubUnitRepository repository;

  public OrgSubUnitEntityExtendedController(OrgSubUnitRepository repository) {
    this.repository = repository;
  }

  @Override
  public ResponseEntity<Void> updateStatus(Long organizationId, String subUnitCode, OrgSubUnitStatus status) {
    log.info("Updating status to {} for OrgSubUnit [organizationId={}, subUnitCode={}]", status, organizationId, subUnitCode);
    repository.updateStatus(organizationId, subUnitCode, status);
    return ResponseEntity.ok().build();
  }
}
