package it.gov.pagopa.pu.organization.model;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "dataView", types = Taxonomy.class)
public interface DistinctOrganizationTypeDTO {
  String getOrganizationType();
  String getOrganizationTypeDescription();

}
