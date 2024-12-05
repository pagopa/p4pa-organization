package it.gov.pagopa.template.controller;

import it.gov.pagopa.template.dto.OrganizationDTO;
import it.gov.pagopa.template.entity.Organization;
import it.gov.pagopa.template.mapper.OrganizationMapper;
import it.gov.pagopa.template.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class OrganizationController {

  private final OrganizationRepository organizationRepository;

  @Autowired
  public OrganizationController(OrganizationRepository organizationRepository) {
    this.organizationRepository = organizationRepository;
  }

  @GetMapping("/api/organizations/{ipaCode}")
  public OrganizationDTO getOrganizationByIpaCode(@PathVariable String ipaCode) {
    Optional<Organization> organization = organizationRepository.findByIpaCode(ipaCode);
    return organization.map(OrganizationMapper::toDTO).orElse(null);
  }

}
