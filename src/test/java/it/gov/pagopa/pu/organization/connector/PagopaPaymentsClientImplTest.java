package it.gov.pagopa.pu.organization.connector;

import it.gov.pagopa.pu.organization.util.TestUtils;
import it.gov.pagopa.pu.pagopapayments.controller.generated.TaxonomiesApi;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PagopaPaymentsClientImplTest {

  @Mock
  private RestTemplateBuilder restTemplateBuilderMock;

  @Mock
  private RestTemplate restTemplateMock;

  @Mock
  private TaxonomiesApi taxonomiesApi;

  private PagopaPaymentsClientImpl pagopaPaymentsClient;

  private static final List<Taxonomy> TAXONOMIES_LIST = List.of(new Taxonomy());

  @BeforeEach
  void setUp() {
    Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
    Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
    pagopaPaymentsClient = new PagopaPaymentsClientImpl("http://localhost", restTemplateBuilderMock);
  }

  @Test
  void fetchTaxonomy_withValidAccessToken_returnsTaxonomies() {
    //given
    ResponseEntity<List<Taxonomy>> responseEntity = new ResponseEntity<>(TAXONOMIES_LIST, HttpStatus.OK);
    Mockito.when(restTemplateMock.exchange(
      Mockito.any(RequestEntity.class),
      Mockito.eq(new ParameterizedTypeReference<List<Taxonomy>>() {})
    )).thenReturn(responseEntity);

    String accessToken = TestUtils.getFakeAccessToken();

    //when
    List<Taxonomy> result = pagopaPaymentsClient.fetchTaxonomy(accessToken);

    //verify
    assertEquals(TAXONOMIES_LIST, result);
    Mockito.verify(restTemplateMock, Mockito.times(1))
      .exchange(Mockito.any(RequestEntity.class), Mockito.eq(new ParameterizedTypeReference<List<Taxonomy>>() {}));
  }
}
