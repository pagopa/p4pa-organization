package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.OrganizationApi;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.service.organization.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OrganizationController implements OrganizationApi {

  private final OrganizationService service;

  public OrganizationController(OrganizationService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Void> encryptAndSaveApiKey(Long organizationId, OrganizationApiKeys organizationApiKeys) {
    service.encryptAndSaveApiKey(organizationId, organizationApiKeys);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<String> getOrganizationApiKey(Long organizationId, OrganizationApiKeyType keyType){
    String apiKey = service.getApiKey(organizationId, keyType);
    return new ResponseEntity<>(apiKey, HttpStatus.OK);
  }
}
