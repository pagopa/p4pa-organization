package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.dto.DistinctCollectionReasonDTO;
import it.gov.pagopa.pu.organization.dto.DistinctMacroAreaDTO;
import it.gov.pagopa.pu.organization.dto.DistinctOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.dto.DistinctServiceTypeDTO;
import it.gov.pagopa.pu.organization.dto.DistinctTaxonomyCodeDTO;
import it.gov.pagopa.pu.organization.model.Taxonomy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "taxonomy", path = "taxonomy")
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long> {
  @Query("SELECT DISTINCT t.organizationType,t.organizationTypeDescription FROM Taxonomy t")
  List<DistinctOrganizationTypeDTO> findDistinctOrganizationType();

  List<DistinctMacroAreaDTO> findDistinctMacroAreaCodeByOrganizationType(String organizationType);

  List<DistinctServiceTypeDTO> findDistinctServiceTypeCodeByOrganizationTypeAndMacroAreaCode(String organizationType,String macroAreaCode);

  List<DistinctCollectionReasonDTO> findDistinctCollectionReasonByOrganizationTypeAndMacroAreaCodeAndServiceTypeCode(String organizationType,String macroAreaCode,String serviceTypeCode);

  List<DistinctTaxonomyCodeDTO> findDistinctTaxonomyCodeByOrganizationTypeAndMacroAreaCodeAndServiceTypeCodeAndCollectionReason(String organizationType,String macroAreaName,String serviceType,String collectionReason);

}
