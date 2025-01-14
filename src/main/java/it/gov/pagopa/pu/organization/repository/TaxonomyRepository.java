package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.Taxonomy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies")
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long> {

}
