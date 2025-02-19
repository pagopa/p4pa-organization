package it.gov.pagopa.pu.organization.service.taxonomy;

import it.gov.pagopa.pu.organization.connector.pagopapayments.TaxonomyServiceImpl;
import it.gov.pagopa.pu.organization.mapper.TaxonomyMapper;
import it.gov.pagopa.pu.organization.model.Taxonomy;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import it.gov.pagopa.pu.organization.util.faker.TaxonomyFaker;
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
    .taxonomyCode("taxonomyCode1")
    .version("1")
    .collectionReason("collectionReason1Mod")
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
    .collectionReason("collectionReason2")
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
  private static final TaxonomyDTO TAXONOMY_DTO3 = TaxonomyDTO.builder()
    .taxonomyCode("code3")
    .version("3")
    .collectionReason("collectionReason3")
    .macroAreaCode("macroAreaCode3")
    .macroAreaName("macroAreaName3")
    .macroAreaDescription("macroAreaDescription3")
    .serviceTypeCode("serviceTypeCode3")
    .serviceType("serviceType3")
    .organizationType("organizationType3")
    .organizationTypeDescription("organizationTypeDescription3")
    .startDateValidity(OffsetDateTime.now())
    .endDateOfValidity(OffsetDateTime.now().plusDays(2))
    .build();


  private TaxonomySynchronizationService taxonomySynchronizationService;

  @BeforeEach
  void setUp() {
    taxonomySynchronizationService = new TaxonomySynchronizationService(taxonomyRepository, pagopaPaymentsClient, taxonomyMapperMock);
  }

  @Test
  void synchTaxonomies_withValidAccessToken_updatesAndInsertsTaxonomies() {
    String accessToken = "validAccessToken";

    List<TaxonomyDTO> fetchedTaxonomies = List.of(TAXONOMY_DTO1, TAXONOMY_DTO2, TAXONOMY_DTO3);

    Taxonomy existingTaxonomy = TaxonomyFaker.taxonomyBuilder(1L);
    Taxonomy existingTaxonomy2 = TaxonomyFaker.taxonomyBuilder(2L);
    List<Taxonomy> existingTaxonomies = List.of(existingTaxonomy, existingTaxonomy2);

    Taxonomy taxonomy1 = TaxonomyFaker.taxonomyBuilder(1L);
    taxonomy1.setCollectionReason("collectionReason1Mod");
    Taxonomy taxonomy2 = TaxonomyFaker.taxonomyBuilder(2L);
    Taxonomy taxonomy3 = TaxonomyFaker.taxonomyBuilder(3L);

    Mockito.when(pagopaPaymentsClient.fetchTaxonomy(accessToken)).thenReturn(fetchedTaxonomies);
    Mockito.when(taxonomyRepository.findAll()).thenReturn(existingTaxonomies);
    Mockito.when(taxonomyMapperMock.toModel(TAXONOMY_DTO1)).thenReturn(taxonomy1);
    Mockito.when(taxonomyMapperMock.toModel(TAXONOMY_DTO2)).thenReturn(taxonomy2);
    Mockito.when(taxonomyMapperMock.toModel(TAXONOMY_DTO3)).thenReturn(taxonomy3);

    taxonomySynchronizationService.synchronizeTaxonomies(accessToken);

    verify(taxonomyRepository, times(3)).save(Mockito.any(Taxonomy.class));
  }

  @Test
  void synchTaxonomies_withEmptyFetchedTaxonomies_doesNotSaveAnyTaxonomy() {
    String accessToken = "validAccessToken";
    List<TaxonomyDTO> fetchedTaxonomies = List.of();
    List<Taxonomy> existingTaxonomies = List.of();

    Mockito.when(pagopaPaymentsClient.fetchTaxonomy(accessToken)).thenReturn(fetchedTaxonomies);
    Mockito.when(taxonomyRepository.findAll()).thenReturn(existingTaxonomies);

    taxonomySynchronizationService.synchronizeTaxonomies(accessToken);

    verify(taxonomyRepository, times(0)).save(Mockito.any(Taxonomy.class));
  }

  @Test
  void synchTaxonomies_withNullExistingTaxonomies_insertsAllFetchedTaxonomies() {
    String accessToken = "validAccessToken";
    List<TaxonomyDTO> fetchedTaxonomies = List.of(TAXONOMY_DTO1, TAXONOMY_DTO2);

    Taxonomy taxonomy1 = TaxonomyFaker.taxonomyBuilder(1L);
    Taxonomy taxonomy2 = TaxonomyFaker.taxonomyBuilder(2L);

    Mockito.when(pagopaPaymentsClient.fetchTaxonomy(accessToken)).thenReturn(fetchedTaxonomies);
    Mockito.when(taxonomyRepository.findAll()).thenReturn(null);
    Mockito.when(taxonomyMapperMock.toModel(TAXONOMY_DTO1)).thenReturn(taxonomy1);
    Mockito.when(taxonomyMapperMock.toModel(TAXONOMY_DTO2)).thenReturn(taxonomy2);

    taxonomySynchronizationService.synchronizeTaxonomies(accessToken);

    verify(taxonomyRepository, times(2)).save(Mockito.any(Taxonomy.class));
  }
}
