package it.gov.pagopa.pu.organization.service.taxonomy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaxonomyServiceTest {

  @Mock
  private TaxonomySynchronizationService taxonomySynchronizationServiceMock;

  private TaxonomyService taxonomyService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    taxonomyService = new TaxonomyService(taxonomySynchronizationServiceMock);
  }

  @Test
  void synchTaxonomies_withValidAccessToken_invokesClientAndRepository() {
    String accessToken = "validAccessToken";
    Mockito.doNothing().when(taxonomySynchronizationServiceMock).synchronizeTaxonomies(accessToken);

    taxonomyService.synchTaxonomies(accessToken);

    verify(taxonomySynchronizationServiceMock, times(1)).synchronizeTaxonomies(accessToken);
  }

  @Test
  void synchTaxonomies_withInvalidAccessToken_throwsException() {
    String accessToken = "invalidAccessToken";
    Mockito.doThrow(new RuntimeException("Invalid token")).when(taxonomySynchronizationServiceMock).synchronizeTaxonomies(accessToken);

    assertThrows(RuntimeException.class, () -> taxonomyService.synchTaxonomies(accessToken));
    verify(taxonomySynchronizationServiceMock, times(1)).synchronizeTaxonomies(accessToken);
  }

  @Test
  void synchTaxonomies_withEmptyAccessToken_invokesClientAndRepository() {
    String accessToken = "";
    Mockito.doNothing().when(taxonomySynchronizationServiceMock).synchronizeTaxonomies(accessToken);

    taxonomyService.synchTaxonomies(accessToken);

    verify(taxonomySynchronizationServiceMock, times(1)).synchronizeTaxonomies(accessToken);
  }
}
