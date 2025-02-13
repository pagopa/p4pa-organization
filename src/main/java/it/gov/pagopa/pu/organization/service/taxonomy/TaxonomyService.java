package it.gov.pagopa.pu.organization.service.taxonomy;

import it.gov.pagopa.pu.organization.connector.taxonomy.TaxonomyServiceImpl;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TaxonomyService {

  private final TaxonomyRepository taxonomyRepository;
  private final TaxonomyServiceImpl pagopaPaymentsClient;

  public TaxonomyService(
    TaxonomyRepository taxonomyRepository,
    TaxonomyServiceImpl pagopaPaymentsClient
  ) {
    this.taxonomyRepository = taxonomyRepository;
    this.pagopaPaymentsClient = pagopaPaymentsClient;
  }

  public void synchTaxonomies(String accessToken) {
    List<Taxonomy> taxonomies = pagopaPaymentsClient.fetchTaxonomy(accessToken);
    //todo business logic to be implemented in P4ADEV-2150
  }
}
