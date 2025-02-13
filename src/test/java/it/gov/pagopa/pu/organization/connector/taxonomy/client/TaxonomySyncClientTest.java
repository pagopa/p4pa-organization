package it.gov.pagopa.pu.organization.connector.taxonomy.client;

import it.gov.pagopa.pu.organization.connector.taxonomy.config.TaxonomyApisHolder;
import it.gov.pagopa.pu.pagopapayments.controller.generated.TaxonomiesApi;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
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
  private TaxonomyApisHolder taxonomyApisHolderMock;

  @Mock
  private TaxonomiesApi taxonomiesApiMock;

  private TaxonomySyncClient taxonomySyncClient;

  @BeforeEach
  void setUp() {
    taxonomySyncClient = new TaxonomySyncClient(taxonomyApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      taxonomyApisHolderMock
    );
  }

  @Test
  void whenFindByIdThenInvokeWithAccessToken() {
    // Given
    String accessToken = "ACCESSTOKEN";
    List<Taxonomy> expectedResult = List.of(new Taxonomy());

    Mockito.when(taxonomyApisHolderMock.getTaxonomiesApi(accessToken))
      .thenReturn(taxonomiesApiMock);
    Mockito.when(taxonomiesApiMock.fetchTaxonomies())
      .thenReturn(expectedResult);

    // When
    List<Taxonomy> result = taxonomySyncClient.syncTaxonomy(accessToken);

    // Then
    Assertions.assertSame(expectedResult, result);
  }



}
