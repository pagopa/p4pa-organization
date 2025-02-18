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
    existingTaxonomy.setOrganizationType("organizationType1");
    existingTaxonomy.setOrganizationTypeDescription("organizationTypeDescription1");
    existingTaxonomy.setMacroAreaCode("macroAreaCode1");
    existingTaxonomy.setMacroAreaName("macroAreaName1");
    existingTaxonomy.setMacroAreaDescription("macroAreaDescription1");
    existingTaxonomy.setServiceTypeCode("serviceTypeCode1");
    existingTaxonomy.setServiceType("serviceType1");
    existingTaxonomy.setServiceTypeDescription("serviceTypeDescription1");
    existingTaxonomy.setCollectionReason("reason1");
    existingTaxonomy.setStartDateValidity(OffsetDateTime.now());
    existingTaxonomy.setEndDateOfValidity(OffsetDateTime.now().plusDays(5));
    existingTaxonomy.setTaxonomyCode("code1");
    existingTaxonomy.setEndDateOfValidity(OffsetDateTime.now().plusDays(5));
    List<Taxonomy> existingTaxonomies = List.of(existingTaxonomy);

    Taxonomy taxonomy1 = new Taxonomy();
    taxonomy1.setTaxonomyId(1L);
    taxonomy1.setOrganizationType("organizationType1_updated");
    taxonomy1.setOrganizationTypeDescription("organizationTypeDescription1_updated");
    taxonomy1.setMacroAreaCode("macroAreaCode1_updated");
    taxonomy1.setMacroAreaName("macroAreaName1_updated");
    taxonomy1.setMacroAreaDescription("macroAreaDescription1_updated");
    taxonomy1.setServiceTypeCode("serviceTypeCode1_updated");
    taxonomy1.setServiceType("serviceType1_updated");
    taxonomy1.setServiceTypeDescription("serviceTypeDescription1_updated");
    taxonomy1.setCollectionReason("reason1_updated");
    taxonomy1.setStartDateValidity(OffsetDateTime.now().plusDays(1));
    taxonomy1.setEndDateOfValidity(OffsetDateTime.now().plusDays(6));
    taxonomy1.setTaxonomyCode("code1_updated");

    Taxonomy taxonomy2 = new Taxonomy();
    taxonomy2.setOrganizationType("organizationType2");
    taxonomy2.setOrganizationTypeDescription("organizationTypeDescription2");
    taxonomy2.setMacroAreaCode("macroAreaCode2");
    taxonomy2.setMacroAreaName("macroAreaName2");
    taxonomy2.setMacroAreaDescription("macroAreaDescription2");
    taxonomy2.setServiceTypeCode("serviceTypeCode2");
    taxonomy2.setServiceType("serviceType2");
    taxonomy2.setServiceTypeDescription("serviceTypeDescription2");
    taxonomy2.setCollectionReason("reason2");
    taxonomy2.setStartDateValidity(OffsetDateTime.now().plusDays(2));
    taxonomy2.setEndDateOfValidity(OffsetDateTime.now().plusDays(7));
    taxonomy2.setTaxonomyCode("code2");


    Mockito.when(pagopaPaymentsClient.fetchTaxonomy(accessToken)).thenReturn(fetchedTaxonomies);
    Mockito.when(taxonomyRepository.findAll()).thenReturn(existingTaxonomies);
    Mockito.when(taxonomyMapperMock.toModel(TAXONOMY_DTO1)).thenReturn(taxonomy1);
    Mockito.when(taxonomyMapperMock.toModel(TAXONOMY_DTO2)).thenReturn(taxonomy2);

    taxonomySynchronizationService.synchronizeTaxonomies(accessToken);

    verify(taxonomyRepository, times(2)).save(Mockito.any(Taxonomy.class));
  }

}
