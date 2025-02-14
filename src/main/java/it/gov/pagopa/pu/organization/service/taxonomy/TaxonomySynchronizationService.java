package it.gov.pagopa.pu.organization.service.taxonomy;

import it.gov.pagopa.pu.organization.connector.pagopapayments.TaxonomyServiceImpl;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TaxonomySynchronizationService {
  private final TaxonomyRepository taxonomyRepository;
  private final TaxonomyServiceImpl pagopaPaymentsClient;

  public TaxonomySynchronizationService(
    TaxonomyRepository taxonomyRepository,
    TaxonomyServiceImpl pagopaPaymentsClient
  ) {
    this.taxonomyRepository = taxonomyRepository;
    this.pagopaPaymentsClient = pagopaPaymentsClient;
  }
  public void synchronizeTaxonomies(String accessToken) {
    log.info("TaxonomySynchronizationService :: Synchronizing taxonomy");
    List<Taxonomy> taxonomies = pagopaPaymentsClient.fetchTaxonomy(accessToken);
    //todo business logic to be implemented in P4ADEV-2150
  }

}
