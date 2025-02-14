package it.gov.pagopa.pu.organization.connector.pagopapayments;

import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;

import java.util.List;

public interface TaxonomyService {
  List<Taxonomy> fetchTaxonomy(String accessToken);
}
