package it.gov.pagopa.template.controller;

import it.gov.pagopa.template.dto.OrganizationDTO;
import it.gov.pagopa.template.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

  private final OrganizationService organizationService;

  @Autowired
  public OrganizationController(OrganizationService organizationService) {
    this.organizationService = organizationService;
  }

  @GetMapping
  public List<OrganizationDTO> getOrganizations() {
    return organizationService.getOrganizations();
  }

}
