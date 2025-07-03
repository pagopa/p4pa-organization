package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.OrganizationSilServiceApi;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.service.organization.OrgSilConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrganizationSilServiceController implements OrganizationSilServiceApi {

  private final OrgSilConfigService orgSilConfigService;

  @Override
  public ResponseEntity<OrgSilServiceDTO> orgSilServiceCreateOrUpdate(Long organizationId, OrgSilServiceDTO orgSilServiceDTO) {
    return ResponseEntity.ok(orgSilConfigService.createOrUpdate(orgSilServiceDTO));
  }

  @Override
  public ResponseEntity<OrgSilServiceDTO> orgSilServiceGet(Long orgSilServiceId) {
    return ResponseEntity.ok(orgSilConfigService.getById(orgSilServiceId));
  }
}
