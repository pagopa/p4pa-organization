package it.gov.pagopa.pu.organization.service.taxonomy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaxonomyService {

  private final TaxonomySynchronizationService taxonomySynchronizationService;

  public TaxonomyService(
    TaxonomySynchronizationService taxonomySynchronizationService
  ) {
    this.taxonomySynchronizationService = taxonomySynchronizationService;
  }

  public void synchTaxonomies(String accessToken) {
    taxonomySynchronizationService.synchronizeTaxonomies(accessToken);
  }
}
