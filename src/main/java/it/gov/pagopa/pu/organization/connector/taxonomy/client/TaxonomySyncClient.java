package it.gov.pagopa.pu.organization.connector.taxonomy.client;

import it.gov.pagopa.pu.organization.connector.taxonomy.config.TaxonomyApisHolder;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
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
