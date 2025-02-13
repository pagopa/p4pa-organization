package it.gov.pagopa.pu.organization.connector.taxonomy;

import it.gov.pagopa.pu.organization.connector.taxonomy.client.TaxonomySyncClient;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxonomyServiceImpl implements TaxonomyService {

  private final TaxonomySyncClient taxonomySyncClient;


public TaxonomyServiceImpl(TaxonomySyncClient taxonomySyncClient) {
    this.taxonomySyncClient = taxonomySyncClient;
  }

  @Override
  public List<Taxonomy> fetchTaxonomy(String accessToken) {
    return taxonomySyncClient.syncTaxonomy(accessToken);
  }

}
