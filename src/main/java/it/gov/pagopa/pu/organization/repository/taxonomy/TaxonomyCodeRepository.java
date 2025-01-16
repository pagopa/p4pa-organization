package it.gov.pagopa.pu.organization.repository.taxonomy;

import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyCodeDTO;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies-taxonomy-code")
public interface TaxonomyCodeRepository extends
  Repository<TaxonomyCodeDTO, Long> {

  @Query("SELECT distinct t from TaxonomyCodeDTO t WHERE " +
    "t.organizationType=:organizationType AND " +
    "t.macroAreaCode=:macroAreaCode AND " +
    "t.serviceTypeCode=:serviceTypeCode AND " +
    "t.collectionReason=:collectionReason " +
    "ORDER BY t.taxonomyCode")
  List<TaxonomyCodeDTO> findTaxonomyCodes(String organizationType, String macroAreaCode, String serviceTypeCode, String collectionReason);

}
