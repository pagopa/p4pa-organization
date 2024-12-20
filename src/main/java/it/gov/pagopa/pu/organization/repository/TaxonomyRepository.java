package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.DistinctOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.model.Taxonomy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "taxonomy", path = "taxonomy")
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long> {

  @Query("select distinct t.organizationType as organizationType, t.organizationTypeDescription as organizationTypeDescription from Taxonomy t")
  List<Taxonomy> findDistinctOrganizationTypeAndOrganizationTypeDescriptionBy();
  List<Taxonomy> findDistinctOrganizationTypeBy();

  List<Taxonomy> findDistinctMacroAreaCodeByOrganizationType(String organizationType);

  List<Taxonomy> findDistinctServiceTypeCodeByOrganizationTypeAndMacroAreaCode(String organizationType,String macroAreaCode);

  List<Taxonomy> findDistinctCollectionReasonByOrganizationTypeAndMacroAreaCodeAndServiceTypeCode(String organizationType,String macroAreaCode,String serviceTypeCode);

  List<Taxonomy> findDistinctTaxonomyCodeByOrganizationTypeAndMacroAreaCodeAndServiceTypeCodeAndCollectionReason(String organizationType,String macroAreaName,String serviceType,String collectionReason);

}
