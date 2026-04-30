package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.OrganizationApi;
import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.OrganizationStationDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.service.organization.OrganizationService;
import it.gov.pagopa.pu.organization.util.SecurityUtils;
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
  public ResponseEntity<Organization> createOrganization(OrganizationCreateDTO organizationCreateDTO) {
    log.info("Requested organization creation with CF {} and ipaCode {}", organizationCreateDTO.getOrgFiscalCode(), organizationCreateDTO.getIpaCode());
    return ResponseEntity.ok(service.createOrganization(organizationCreateDTO, SecurityUtils.getAccessToken()));
  }

  @Override
  public ResponseEntity<Void> encryptAndSaveApiKey(Long organizationId, OrganizationApiKeys organizationApiKeys) {
    log.info("Updating organization {} api key {}", organizationId, organizationApiKeys.getKeyType());
    service.encryptAndSaveApiKey(organizationId, organizationApiKeys);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<String> getOrganizationApiKey(Long organizationId, OrganizationApiKeyType keyType) {
    log.info("Retrieving organization {} api key {}", organizationId, keyType);
    String apiKey = service.getApiKey(organizationId, keyType);
    return new ResponseEntity<>(
      apiKey,
      apiKey != null
        ? HttpStatus.OK
        : HttpStatus.NO_CONTENT);
  }

  @Override
  public ResponseEntity<OrganizationStationDTO> getOrganizationStation(Long organizationId, String stationId) {
    log.info("Retrieving organizationStationDTO having orgId{} and stationId {}", organizationId, stationId);
    return ResponseEntity.ok(service.getOrganizationStation(organizationId, stationId));
  }

  @Override
  public ResponseEntity<OrganizationDetailDTO> getOrganization(Long organizationId) {
    log.info("Retrieving organization {}", organizationId);
    return ResponseEntity.ok(service.getOrganization(organizationId));
  }

  @Override
  public ResponseEntity<Void> updateOrganization(OrganizationDetailDTO organizationDetailDTO) {
    log.info("Updating organization {}", organizationDetailDTO.getOrganizationId());
    service.updateOrganization(organizationDetailDTO, SecurityUtils.getAccessToken());
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> updateOrganizationStatus(Long organizationId, OrganizationStatus newStatus) {
    log.info("Updating status of organization {} to {}", organizationId, newStatus);
    service.updateOrganizationStatus(organizationId, newStatus);
    return ResponseEntity.ok().build();
  }
}
