package it.gov.pagopa.pu.organization.repository.taxonomy;

import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyCollectionReasonDTO;
import java.util.List;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies-collection-reason")
public interface TaxonomyCollectionReasonRepository extends
  Repository<TaxonomyCollectionReasonDTO, Long> {

  List<TaxonomyCollectionReasonDTO> findDistinctCollectionReasonByOrganizationTypeAndMacroAreaCodeAndServiceTypeCodeOrderByCollectionReasonAsc(String organizationType,String macroAreaCode,String serviceTypeCode);

}
