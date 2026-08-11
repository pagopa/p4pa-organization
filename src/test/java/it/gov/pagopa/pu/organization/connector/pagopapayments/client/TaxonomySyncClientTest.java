package it.gov.pagopa.pu.organization.connector.pagopapayments.client;

import it.gov.pagopa.pu.organization.connector.pagopapayments.config.PagopaPaymentsApisHolder;
import it.gov.pagopa.pu.pagopapayments.client.generated.TaxonomiesApi;
import it.gov.pagopa.pu.pagopapayments.dto.generated.TaxonomyDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TaxonomySyncClientTest {

  @Mock
  private PagopaPaymentsApisHolder pagopaPaymentsApisHolderMock;

  @Mock
  private TaxonomiesApi taxonomiesApiMock;

  private TaxonomySyncClient taxonomySyncClient;

  @BeforeEach
  void setUp() {
    taxonomySyncClient = new TaxonomySyncClient(pagopaPaymentsApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
            pagopaPaymentsApisHolderMock
    );
  }

  @Test
  void whenFindByIdThenInvokeWithAccessToken() {
    // Given
    String accessToken = "ACCESSTOKEN";
    List<TaxonomyDTO> expectedResult = List.of(new TaxonomyDTO());

    Mockito.when(pagopaPaymentsApisHolderMock.getTaxonomiesApi(accessToken))
      .thenReturn(taxonomiesApiMock);
    Mockito.when(taxonomiesApiMock.fetchTaxonomies())
      .thenReturn(expectedResult);

    // When
    List<TaxonomyDTO> result = taxonomySyncClient.syncTaxonomy(accessToken);

    // Then
    Assertions.assertSame(expectedResult, result);
  }



}
