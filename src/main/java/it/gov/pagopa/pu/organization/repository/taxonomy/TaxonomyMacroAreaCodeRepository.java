package it.gov.pagopa.pu.organization.repository.taxonomy;

import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyMacroAreaCodeDTO;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies-macro-area")
public interface TaxonomyMacroAreaCodeRepository extends
  Repository<TaxonomyMacroAreaCodeDTO, Long> {

  @Query("SELECT distinct m FROM TaxonomyMacroAreaCodeDTO m WHERE " +
    "m.organizationType=:organizationType " +
    "ORDER BY m.macroAreaCode")
  List<TaxonomyMacroAreaCodeDTO> findMacroAreaCodes(String organizationType);

}
