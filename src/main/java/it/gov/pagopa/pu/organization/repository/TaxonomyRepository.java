package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.Taxonomy;
import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyCodeDTO;
import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyCollectionReasonDTO;
import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyMacroAreaCodeDTO;
import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyServiceTypeCodeDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies")
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long> {

  List<TaxonomyOrganizationTypeDTO> findDistinctOrganizationTypeByOrderByOrganizationTypeAsc();
  List<TaxonomyMacroAreaCodeDTO> findDistinctMacroAreaCodeByOrganizationTypeOrderByMacroAreaCodeAsc(String organizationType);
  List<TaxonomyServiceTypeCodeDTO> findDistinctServiceTypeCodeByOrganizationTypeAndMacroAreaCodeOrderByServiceTypeCodeAsc(String organizationType,String macroAreaCode);
  List<TaxonomyCollectionReasonDTO> findDistinctCollectionReasonByOrganizationTypeAndMacroAreaCodeAndServiceTypeCodeOrderByCollectionReasonAsc(String organizationType,String macroAreaCode,String serviceTypeCode);
  List<TaxonomyCodeDTO> findDistinctTaxonomyCodeByOrganizationTypeAndMacroAreaCodeAndServiceTypeCodeAndCollectionReasonOrderByTaxonomyCodeAsc(String organizationType,String macroAreaCode,String serviceTypeCode,String collectionReason);

}
