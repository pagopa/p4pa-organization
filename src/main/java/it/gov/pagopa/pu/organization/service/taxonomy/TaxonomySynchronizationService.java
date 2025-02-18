package it.gov.pagopa.pu.organization.service.taxonomy;

import it.gov.pagopa.pu.organization.connector.pagopapayments.TaxonomyServiceImpl;
import it.gov.pagopa.pu.organization.mapper.TaxonomyMapper;
import it.gov.pagopa.pu.organization.model.Taxonomy;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import it.gov.pagopa.pu.pagopapayments.dto.generated.TaxonomyDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TaxonomySynchronizationService {
  private final TaxonomyRepository taxonomyRepository;
  private final TaxonomyServiceImpl pagopaPaymentsClient;

  private final TaxonomyMapper taxonomyMapper;

  public TaxonomySynchronizationService(
    TaxonomyRepository taxonomyRepository,
    TaxonomyServiceImpl pagopaPaymentsClient, TaxonomyMapper taxonomyMapper
  ) {
    this.taxonomyRepository = taxonomyRepository;
    this.pagopaPaymentsClient = pagopaPaymentsClient;
    this.taxonomyMapper = taxonomyMapper;
  }

  public void synchronizeTaxonomies(String accessToken) {
    log.info("TaxonomySynchronizationService :: Synchronizing taxonomy");
    List<TaxonomyDTO> fetchedTaxonomies = pagopaPaymentsClient.fetchTaxonomy(accessToken);

    List<Taxonomy> existingTaxonomies = taxonomyRepository.findAll();

    // Update or insert fetched taxonomies
    for (TaxonomyDTO fetchedTaxonomy : fetchedTaxonomies) {
      Taxonomy existingTaxonomy = existingTaxonomies.stream()
        .filter(t -> t.getTaxonomyCode().equals(fetchedTaxonomy.getTaxonomyCode()))
        .findFirst()
        .orElse(null);

      Taxonomy mappedTaxonomy = taxonomyMapper.toModel(fetchedTaxonomy);

      boolean taxonomyIsUpdated = taxonomyIsUpdated(existingTaxonomy, mappedTaxonomy);

      if (taxonomyIsUpdated) {
        // Update existing taxonomy
        mappedTaxonomy.setTaxonomyId(existingTaxonomy.getTaxonomyId());
        taxonomyRepository.save(mappedTaxonomy);
      } else {
        // Insert new taxonomy
        taxonomyRepository.save(mappedTaxonomy);
      }
    }
  }

  private boolean taxonomyIsUpdated(Taxonomy existingTaxonomy, Taxonomy mappedTaxonomy) {
    if(existingTaxonomy == null || mappedTaxonomy == null) {
      return false;
    }
    return !existingTaxonomy.equals(mappedTaxonomy);
  }
}
