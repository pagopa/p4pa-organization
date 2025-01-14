package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.DistinctCollectionReasonDTO;
import it.gov.pagopa.pu.organization.model.DistinctMacroAreaCodeDTO;
import it.gov.pagopa.pu.organization.model.DistinctOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.model.DistinctServiceTypeCodeDTO;
import it.gov.pagopa.pu.organization.model.DistinctTaxonomyCodeDTO;
import it.gov.pagopa.pu.organization.model.Taxonomy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies")
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long> {

  List<DistinctOrganizationTypeDTO> findDistinctOrganizationTypeByOrderByOrganizationTypeAsc();
  List<DistinctMacroAreaCodeDTO> findDistinctMacroAreaCodeByOrganizationTypeOrderByMacroAreaCodeAsc(String organizationType);
  List<DistinctServiceTypeCodeDTO> findDistinctServiceTypeCodeByOrganizationTypeAndMacroAreaCodeOrderByServiceTypeCodeAsc(String organizationType,String macroAreaCode);
  List<DistinctCollectionReasonDTO> findDistinctCollectionReasonByOrganizationTypeAndMacroAreaCodeAndServiceTypeCodeOrderByCollectionReasonAsc(String organizationType,String macroAreaCode,String serviceTypeCode);
  List<DistinctTaxonomyCodeDTO> findDistinctTaxonomyCodeByOrganizationTypeAndMacroAreaCodeAndServiceTypeCodeAndCollectionReasonOrderByTaxonomyCodeAsc(String organizationType,String macroAreaCode,String serviceTypeCode,String collectionReason);

}
