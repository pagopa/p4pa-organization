package it.gov.pagopa.pu.organization.dto;

import it.gov.pagopa.pu.organization.model.Taxonomy;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "distinctOrganizationType", types = { Taxonomy.class })
public interface DistinctOrganizationTypeDTO {
  String getOrganizationType();
  String getOrganizationTypeDescription();

}
