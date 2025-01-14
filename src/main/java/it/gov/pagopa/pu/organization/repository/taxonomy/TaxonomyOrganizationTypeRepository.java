package it.gov.pagopa.pu.organization.repository.taxonomy;

import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyOrganizationTypeDTO;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "taxonomies-organization-types")
public interface TaxonomyOrganizationTypeRepository extends Repository<TaxonomyOrganizationTypeDTO, Long> {
  List<TaxonomyOrganizationTypeDTO> findDistinctOrganizationTypeByOrderByOrganizationTypeAsc();
}
