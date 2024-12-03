package it.gov.pagopa.template.service.impl;

import it.gov.pagopa.template.dto.OrganizationDTO;
import it.gov.pagopa.template.entity.Organization;
import it.gov.pagopa.template.mapper.OrganizationMapper;
import it.gov.pagopa.template.repository.OrganizationRepository;
import it.gov.pagopa.template.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements OrganizationService {

  private final OrganizationRepository organizationRepository;
  private final OrganizationMapper organizationMapper;

  @Autowired
  public OrganizationServiceImpl(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper) {
    this.organizationRepository = organizationRepository;
    this.organizationMapper = organizationMapper;
  }

  @Override
  public List<OrganizationDTO> getOrganizations() {
    List<Organization> organizations = organizationRepository.findAll();
    return organizations.stream()
      .map(organizationMapper::toDTO)
      .collect(Collectors.toList());
  }

}
