package it.gov.pagopa.pu.organization.service.taxonomy;

import it.gov.pagopa.pu.organization.connector.pagopapayments.TaxonomyServiceImpl;
import it.gov.pagopa.pu.organization.mapper.TaxonomyMapper;
import it.gov.pagopa.pu.organization.model.Taxonomy;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import it.gov.pagopa.pu.pagopapayments.dto.generated.TaxonomyDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  @Transactional
  public void synchronizeTaxonomies(String accessToken) {
    log.info("TaxonomySynchronizationService :: Synchronizing taxonomy");
    List<TaxonomyDTO> fetchedTaxonomies = pagopaPaymentsClient.fetchTaxonomy(accessToken);

    List<Taxonomy> existingTaxonomies = taxonomyRepository.findAll();
    Map<String, Taxonomy> existingTaxonomyMap = CollectionUtils.emptyMap();
    if(existingTaxonomies != null) {
      existingTaxonomyMap = existingTaxonomies.stream()
        .collect(Collectors.toMap(Taxonomy::getTaxonomyCode, Function::identity));
    }

    // Update or insert fetched taxonomies
    for (TaxonomyDTO fetchedTaxonomy : fetchedTaxonomies) {
      Taxonomy existingTaxonomy = existingTaxonomyMap.get(fetchedTaxonomy.getTaxonomyCode());
      Taxonomy mappedTaxonomy = taxonomyMapper.toModel(fetchedTaxonomy);

      boolean taxonomyIsChanged = taxonomyIsChanged(existingTaxonomy, mappedTaxonomy);

      if (taxonomyIsChanged) {
        // Update existing taxonomy if it has changed
        mappedTaxonomy.setTaxonomyId(existingTaxonomy.getTaxonomyId());
        taxonomyRepository.save(mappedTaxonomy);
      } else if (existingTaxonomy == null) {
        // Insert new taxonomy
        taxonomyRepository.save(mappedTaxonomy);
      }
    }
  }

  boolean taxonomyIsChanged(Taxonomy existingTax, Taxonomy mappedTax) {
    if (existingTax == null) {
      return false;
    }
    return !existingTax.getOrganizationType().equals(mappedTax.getOrganizationType()) ||
      !existingTax.getOrganizationTypeDescription().equals(mappedTax.getOrganizationTypeDescription()) ||
      !existingTax.getMacroAreaCode().equals(mappedTax.getMacroAreaCode()) ||
      !existingTax.getMacroAreaName().equals(mappedTax.getMacroAreaName()) ||
      !existingTax.getMacroAreaDescription().equals(mappedTax.getMacroAreaDescription()) ||
      !existingTax.getServiceTypeCode().equals(mappedTax.getServiceTypeCode()) ||
      !existingTax.getServiceType().equals(mappedTax.getServiceType()) ||
      !existingTax.getServiceTypeDescription().equals(mappedTax.getServiceTypeDescription()) ||
      !existingTax.getStartDateValidity().equals(mappedTax.getStartDateValidity()) ||
      !existingTax.getEndDateOfValidity().equals(mappedTax.getEndDateOfValidity()) ||
      !existingTax.getTaxonomyCode().equals(mappedTax.getTaxonomyCode()) ||
      !existingTax.getCollectionReason().equals(mappedTax.getCollectionReason());
  }
}
