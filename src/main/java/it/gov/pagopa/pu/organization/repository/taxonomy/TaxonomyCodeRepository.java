package it.gov.pagopa.pu.organization.repository.taxonomy;

import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyCodeDTO;
import java.util.List;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies-taxonomy-code")
public interface TaxonomyCodeRepository extends
  Repository<TaxonomyCodeDTO, Long> {

  List<TaxonomyCodeDTO> findDistinctTaxonomyCodeByOrganizationTypeAndMacroAreaCodeAndServiceTypeCodeAndCollectionReasonOrderByTaxonomyCodeAsc(String organizationType,String macroAreaCode,String serviceTypeCode,String collectionReason);

}
