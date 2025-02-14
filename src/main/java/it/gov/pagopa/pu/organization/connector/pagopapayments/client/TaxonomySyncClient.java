package it.gov.pagopa.pu.organization.connector.pagopapayments.client;

import it.gov.pagopa.pu.organization.connector.pagopapayments.config.TaxonomyApisHolder;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxonomySyncClient {


  private final TaxonomyApisHolder taxonomyApisHolder;

  public TaxonomySyncClient(TaxonomyApisHolder taxonomyApisHolder) {
    this.taxonomyApisHolder = taxonomyApisHolder;
  }


  public List<Taxonomy> syncTaxonomy(String accessToken) {
    return taxonomyApisHolder.getTaxonomiesApi(accessToken)
            .fetchTaxonomies();
  }


}
