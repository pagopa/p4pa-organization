package it.gov.pagopa.pu.organization.repository.taxonomy;

import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyServiceTypeCodeDTO;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies-service-type")
public interface TaxonomyServiceTypeCodeRepository extends
  Repository<TaxonomyServiceTypeCodeDTO, Long> {

  @Query("SELECT distinct s FROM TaxonomyServiceTypeCodeDTO s WHERE " +
    "s.organizationType=:organizationType AND " +
    "s.macroAreaCode=:macroAreaCode " +
    "ORDER BY s.serviceTypeCode")
  List<TaxonomyServiceTypeCodeDTO> findServiceTypeCodes(String organizationType,String macroAreaCode);

}
