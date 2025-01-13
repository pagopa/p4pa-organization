package it.gov.pagopa.pu.organization.service;

import it.gov.pagopa.pu.organization.model.DistinctOrganizationTypeDTO;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface TaxonomyService {
  List<DistinctOrganizationTypeDTO> getDistinctOrganizationType(Sort sort);

}
