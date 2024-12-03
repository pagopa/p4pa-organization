package it.gov.pagopa.template.mapper;

import it.gov.pagopa.template.dto.OrganizationDTO;
import it.gov.pagopa.template.entity.Organization;

public class OrganizationMapper {

  public OrganizationDTO toDTO(Organization organization) {
    return new OrganizationDTO(
      organization.getOrgId(),
      organization.getName(),
      organization.getRole(),
      organization.getLogo()
    );
  }

}

