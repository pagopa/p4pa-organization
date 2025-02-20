package it.gov.pagopa.pu.organization.connector.pagopapayments;


import it.gov.pagopa.pu.pagopapayments.dto.generated.TaxonomyDTO;

import java.util.List;

public interface TaxonomyService {
  List<TaxonomyDTO> fetchTaxonomy(String accessToken);
}
