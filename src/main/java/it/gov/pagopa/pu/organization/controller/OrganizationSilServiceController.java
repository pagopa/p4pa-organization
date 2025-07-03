package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.OrganizationSilServiceApi;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.service.organization.OrganizationSilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrganizationSilServiceController implements OrganizationSilServiceApi {

  private final OrganizationSilService organizationSilService;

  @Override
  public ResponseEntity<OrgSilServiceDTO> orgSilServiceCreateOrUpdate(OrgSilServiceDTO orgSilServiceDTO) {
    return ResponseEntity.ok(organizationSilService.createOrUpdate(orgSilServiceDTO));
  }

  @Override
  public ResponseEntity<OrgSilServiceDTO> orgSilServiceGet(Long orgSilServiceId) {
    return ResponseEntity.ok(organizationSilService.getById(orgSilServiceId));
  }

}
