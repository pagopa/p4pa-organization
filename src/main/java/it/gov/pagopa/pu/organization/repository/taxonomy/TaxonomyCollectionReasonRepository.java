package it.gov.pagopa.pu.organization.repository.taxonomy;

import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyCollectionReasonDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "taxonomies-collection-reason")
public interface TaxonomyCollectionReasonRepository extends
  Repository<TaxonomyCollectionReasonDTO, Long> {

  @Query("SELECT distinct c FROM TaxonomyCollectionReasonDTO c WHERE " +
    "c.organizationType=:organizationType AND " +
    "c.macroAreaCode=:macroAreaCode AND " +
    "c.serviceTypeCode=:serviceTypeCode " +
    "ORDER BY c.collectionReason"
  )
  List<TaxonomyCollectionReasonDTO> findCollectionReasons(String organizationType, String macroAreaCode, String serviceTypeCode);

}
