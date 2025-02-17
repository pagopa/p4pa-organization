package it.gov.pagopa.pu.organization.connector;

import it.gov.pagopa.pu.organization.connector.pagopapayments.TaxonomyService;
import it.gov.pagopa.pu.organization.connector.pagopapayments.TaxonomyServiceImpl;
import it.gov.pagopa.pu.organization.connector.pagopapayments.client.TaxonomySyncClient;
import it.gov.pagopa.pu.pagopapayments.dto.generated.TaxonomyDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    List<TaxonomyDTO> expectedTaxonomies = List.of(new TaxonomyDTO());
    Mockito.when(taxonomySyncClientMock.syncTaxonomy(accessToken)).thenReturn(expectedTaxonomies);

    List<TaxonomyDTO> result = taxonomyService.fetchTaxonomy(accessToken);

    assertEquals(expectedTaxonomies, result);
    verify(taxonomySyncClientMock, times(1)).syncTaxonomy(accessToken);
  }
}
