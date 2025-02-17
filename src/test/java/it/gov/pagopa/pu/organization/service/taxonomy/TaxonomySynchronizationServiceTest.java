package it.gov.pagopa.pu.organization.service.taxonomy;

import it.gov.pagopa.pu.organization.connector.pagopapayments.TaxonomyServiceImpl;
import it.gov.pagopa.pu.organization.mapper.TaxonomyMapper;
import it.gov.pagopa.pu.organization.model.Taxonomy;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import it.gov.pagopa.pu.pagopapayments.dto.generated.TaxonomyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaxonomySynchronizationServiceTest {

  @Mock
  private TaxonomyRepository taxonomyRepository;

  @Mock
  private TaxonomyServiceImpl pagopaPaymentsClient;

  @Mock
  private TaxonomyMapper taxonomyMapperMock;

  private static final TaxonomyDTO TAXONOMY_DTO1 = TaxonomyDTO.builder()
    .taxonomyCode("code1")
    .version("1")
    .collectionReason("reason1")
    .macroAreaCode("macroAreaCode1")
    .macroAreaName("macroAreaName1")
    .macroAreaDescription("macroAreaDescription1")
    .serviceTypeCode("serviceTypeCode1")
    .serviceType("serviceType1")
    .organizationType("organizationType1")
    .organizationTypeDescription("organizationTypeDescription1")
    .startDateValidity(OffsetDateTime.now())
    .endDateOfValidity(OffsetDateTime.now().plusDays(1))
    .build();


  private static final TaxonomyDTO TAXONOMY_DTO2 = TaxonomyDTO.builder()
    .taxonomyCode("code2")
    .version("2")
    .collectionReason("reason2")
    .macroAreaCode("macroAreaCode2")
    .macroAreaName("macroAreaName2")
    .macroAreaDescription("macroAreaDescription2")
    .serviceTypeCode("serviceTypeCode2")
    .serviceType("serviceType2")
    .organizationType("organizationType2")
    .organizationTypeDescription("organizationTypeDescription2")
    .startDateValidity(OffsetDateTime.now())
    .endDateOfValidity(OffsetDateTime.now().plusDays(4))
    .build();


  private TaxonomySynchronizationService taxonomySynchronizationService;

  @BeforeEach
  void setUp() {
    taxonomySynchronizationService = new TaxonomySynchronizationService(taxonomyRepository, pagopaPaymentsClient, taxonomyMapperMock);
  }

  @Test
  void synchTaxonomies_withValidAccessToken_updatesAndInsertsTaxonomies() {
    String accessToken = "validAccessToken";
    List<TaxonomyDTO> fetchedTaxonomies = List.of(TAXONOMY_DTO1, TAXONOMY_DTO2);
    Taxonomy existingTaxonomy = new Taxonomy();
    existingTaxonomy.setTaxonomyCode("code1");
    existingTaxonomy.setEndDateOfValidity(OffsetDateTime.now().plusDays(5));
    List<Taxonomy> existingTaxonomies = List.of(existingTaxonomy);

    Taxonomy taxonomy1 = new Taxonomy();
    taxonomy1.setTaxonomyCode("code1");
    Taxonomy taxonomy2 = new Taxonomy();
    taxonomy2.setTaxonomyCode("code2");

    Mockito.when(pagopaPaymentsClient.fetchTaxonomy(accessToken)).thenReturn(fetchedTaxonomies);
    Mockito.when(taxonomyRepository.findAll()).thenReturn(existingTaxonomies);
    Mockito.when(taxonomyMapperMock.toModel(TAXONOMY_DTO1)).thenReturn(taxonomy1);
    Mockito.when(taxonomyMapperMock.toModel(TAXONOMY_DTO2)).thenReturn(taxonomy2);

    taxonomySynchronizationService.synchronizeTaxonomies(accessToken);

    verify(taxonomyRepository, times(2)).save(Mockito.any(Taxonomy.class));
  }

  @Test
  void synchTaxonomies_withExpiredTaxonomies_deletesExpiredTaxonomies() {
    String accessToken = "validAccessToken";
    List<TaxonomyDTO> fetchedTaxonomies = List.of(new TaxonomyDTO().taxonomyCode("code1").endDateOfValidity(OffsetDateTime.now().minusDays(3)));
    Taxonomy existingTaxonomy = new Taxonomy();
    existingTaxonomy.setTaxonomyCode("code1");
    existingTaxonomy.setEndDateOfValidity(OffsetDateTime.now().minusDays(1));
    List<Taxonomy> existingTaxonomies = List.of(existingTaxonomy);
    Mockito.when(pagopaPaymentsClient.fetchTaxonomy(accessToken)).thenReturn(fetchedTaxonomies);
    Mockito.when(taxonomyRepository.findAll()).thenReturn(existingTaxonomies);

    taxonomySynchronizationService.synchronizeTaxonomies(accessToken);

    verify(taxonomyRepository, times(1)).delete(existingTaxonomies.getFirst());
  }
}
