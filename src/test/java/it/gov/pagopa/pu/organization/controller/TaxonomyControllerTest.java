package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.service.taxonomy.TaxonomyService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaxonomyControllerTest {

  @Mock
  private TaxonomyService taxonomyServiceMock;

  @InjectMocks
  private TaxonomyController taxonomyController;

  @AfterEach
  void clear() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void syncTaxonomies_invokesServiceAndReturnsNull() {
    TestUtils.setFakeAccessTokenInContext();

    ResponseEntity<Void> response = taxonomyController.syncTaxonomies();

    assertEquals(ResponseEntity.ofNullable(null), response);
    verify(taxonomyServiceMock, times(1)).synchTaxonomies( TestUtils.getFakeAccessToken());
  }
}
