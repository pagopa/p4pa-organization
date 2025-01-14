package it.gov.pagopa.pu.organization.repository.taxonomy;

import it.gov.pagopa.pu.organization.model.taxonomy.TaxonomyServiceTypeCodeDTO;
import java.util.List;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "taxonomies-service-type")
public interface TaxonomyServiceTypeCodeRepository extends
  Repository<TaxonomyServiceTypeCodeDTO, Long> {

  List<TaxonomyServiceTypeCodeDTO> findDistinctServiceTypeCodeByOrganizationTypeAndMacroAreaCodeOrderByServiceTypeCodeAsc(String organizationType,String macroAreaCode);

}
