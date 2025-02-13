package it.gov.pagopa.pu.organization.connector;

import it.gov.pagopa.pu.organization.connector.taxonomy.TaxonomyService;
import it.gov.pagopa.pu.organization.connector.taxonomy.TaxonomyServiceImpl;
import it.gov.pagopa.pu.organization.connector.taxonomy.client.TaxonomySyncClient;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaxonomyServiceImplTest {

  @Mock
  private TaxonomySyncClient taxonomySyncClientMock;

  private TaxonomyService taxonomyService;

  @BeforeEach
  void setUp() {
    taxonomyService = new TaxonomyServiceImpl(taxonomySyncClientMock);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
      taxonomySyncClientMock
    );
  }

  @Test
  void fetchTaxonomy_withValidAccessToken_returnsTaxonomies() {
    String accessToken = "validAccessToken";
    List<Taxonomy> expectedTaxonomies = List.of(new Taxonomy());
    Mockito.when(taxonomySyncClientMock.syncTaxonomy(accessToken)).thenReturn(expectedTaxonomies);

    List<Taxonomy> result = taxonomyService.fetchTaxonomy(accessToken);

    assertEquals(expectedTaxonomies, result);
    verify(taxonomySyncClientMock, times(1)).syncTaxonomy(accessToken);
  }

  @Test
  void fetchTaxonomy_withInvalidAccessToken_throwsException() {
    String accessToken = "invalidAccessToken";
    Mockito.when(taxonomySyncClientMock.syncTaxonomy(accessToken)).thenThrow(new RuntimeException("Invalid token"));

    assertThrows(RuntimeException.class, () -> taxonomyService.fetchTaxonomy(accessToken));
    verify(taxonomySyncClientMock, times(1)).syncTaxonomy(accessToken);
  }

  @Test
  void fetchTaxonomy_withEmptyResponse_returnsEmptyList() {
    String accessToken = "validAccessToken";
    Mockito.when(taxonomySyncClientMock.syncTaxonomy(accessToken)).thenReturn(List.of());

    List<Taxonomy> result = taxonomyService.fetchTaxonomy(accessToken);

    assertEquals(List.of(), result);
    verify(taxonomySyncClientMock, times(1)).syncTaxonomy(accessToken);
  }
}
