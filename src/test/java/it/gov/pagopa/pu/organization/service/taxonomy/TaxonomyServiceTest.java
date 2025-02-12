package it.gov.pagopa.pu.organization.service.taxonomy;

import it.gov.pagopa.pu.organization.connector.PagopaPaymentsClientImpl;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaxonomyServiceTest {

  @Mock
  private TaxonomyRepository taxonomyRepository;

  @Mock
  private PagopaPaymentsClientImpl pagopaPaymentsClient;

  private TaxonomyService taxonomyService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    taxonomyService = new TaxonomyService(taxonomyRepository, pagopaPaymentsClient);
  }

  @Test
  void synchTaxonomies_withValidAccessToken_invokesClientAndRepository() {
    String accessToken = "validAccessToken";
    List<Taxonomy> taxonomies = List.of(new Taxonomy());
    Mockito.when(pagopaPaymentsClient.fetchTaxonomy(accessToken)).thenReturn(taxonomies);

    taxonomyService.synchTaxonomies(accessToken);

    verify(pagopaPaymentsClient, times(1)).fetchTaxonomy(accessToken);
  }

  @Test
  void synchTaxonomies_withInvalidAccessToken_throwsException() {
    String accessToken = "invalidAccessToken";
    Mockito.when(pagopaPaymentsClient.fetchTaxonomy(accessToken)).thenThrow(new RuntimeException("Invalid token"));

    assertThrows(RuntimeException.class, () -> taxonomyService.synchTaxonomies(accessToken));
    verify(pagopaPaymentsClient, times(1)).fetchTaxonomy(accessToken);
  }


  @Test
  void synchTaxonomies_withEmptyTaxonomies_doesNotInvokeRepository() {
    String accessToken = "validAccessToken";
    Mockito.when(pagopaPaymentsClient.fetchTaxonomy(accessToken)).thenReturn(List.of());

    taxonomyService.synchTaxonomies(accessToken);

    verify(pagopaPaymentsClient, times(1)).fetchTaxonomy(accessToken);
  }
}
