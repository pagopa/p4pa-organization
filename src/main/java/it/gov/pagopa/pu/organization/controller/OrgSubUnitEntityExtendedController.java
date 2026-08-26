package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.OrgSubUnitEntityExtendedControllerApi;
import it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/** Controller to host spring-data-rest directly not supported methods */
@RestController
public class OrgSubUnitEntityExtendedController implements OrgSubUnitEntityExtendedControllerApi {
  private final OrgSubUnitRepository repository;

  public OrgSubUnitEntityExtendedController(OrgSubUnitRepository repository) {
    this.repository = repository;
  }

  @Override
  public ResponseEntity<Void> updateStatus(Long organizationId, String subUnitCode, OrgSubUnitStatus status) {
    repository.updateStatus(organizationId, subUnitCode, status);
    return ResponseEntity.ok().build();
  }
}
