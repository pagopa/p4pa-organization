package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.Taxonomy;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies")
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long> {

  Optional<Taxonomy> findByTaxonomyCode(String taxonomyCode);

  @Query("SELECT distinct t from Taxonomy t WHERE " +
    "(:organizationType IS NULL OR t.organizationType=:organizationType) AND " +
    "(:macroAreaCode IS NULL OR t.macroAreaCode=:macroAreaCode) AND " +
    "(:serviceTypeCode IS NULL OR t.serviceTypeCode=:serviceTypeCode) AND " +
    "(:collectionReason IS NULL OR t.collectionReason=:collectionReason)")
  Page<Taxonomy> findTaxonomies(String organizationType, String macroAreaCode, String serviceTypeCode, String collectionReason, Pageable pageable);
}
