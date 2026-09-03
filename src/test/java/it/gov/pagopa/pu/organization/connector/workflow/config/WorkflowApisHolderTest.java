package it.gov.pagopa.pu.organization.connector.workflow.config;

import it.gov.pagopa.pu.organization.config.json.JsonConfig;
import it.gov.pagopa.pu.organization.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.workflowhub.dto.generated.SyncDebtPositionRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowApisHolderTest extends BaseApiHolderTest {
  @Mock
  private RestTemplateBuilder restTemplateBuilderMock;

  private WorkflowApisHolder apisHolder;
  private WorkflowApiClientConfig apiClientConfig;

  @BeforeEach
  void setUp() {
    when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
    when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

    apiClientConfig = WorkflowApiClientConfig.builder()
      .baseUrl("http://example.com")
      .maxAttempts(3)
      .build();
    apisHolder = new WorkflowApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

    verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getDebtPositionApi(null));
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      restTemplateBuilderMock,
      restTemplateMock
    );
  }

  @Test
  void testRetryConfiguration() {
    assertRetry(apiClientConfig,
      accessToken -> apisHolder.getDebtPositionApi(accessToken)
        .syncDebtPosition(new SyncDebtPositionRequestDTO(), null, null, null, null),
      new ParameterizedTypeReference<>() {}
    );
  }

  @Test
  void whenDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getDebtPositionApi(accessToken)
        .syncDebtPosition(new SyncDebtPositionRequestDTO(), null, null, null, null),
      new ParameterizedTypeReference<>() {},
      apisHolder::unload);
  }
}
