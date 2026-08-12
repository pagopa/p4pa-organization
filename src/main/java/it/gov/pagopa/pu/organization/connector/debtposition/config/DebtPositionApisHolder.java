package it.gov.pagopa.pu.organization.connector.debtposition.config;

import it.gov.pagopa.pu.debtpositions.client.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionErrorDTO;
import it.gov.pagopa.pu.debtpositions.generated.ApiClient;
import it.gov.pagopa.pu.debtpositions.generated.BaseApi;
import it.gov.pagopa.pu.organization.config.rest.HttpClientErrorJsonBodyHandler;
import it.gov.pagopa.pu.organization.connector.debtposition.mapper.DebtPositionErrorDTOMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Lazy
@Service
public class DebtPositionApisHolder {

  private final DebtPositionTypeOrgApi debtPositionTypeOrgApi;

  private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

  public DebtPositionApisHolder(
    DebtPositionApiClientConfig clientConfig,
    RestTemplateBuilder restTemplateBuilder,
    JsonMapper jsonMapper
  ) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    ApiClient apiClient = new ApiClient(restTemplate);
    apiClient.setBasePath(clientConfig.getBaseUrl());
    apiClient.setBearerToken(bearerTokenHolder::get);
    apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
    apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
    restTemplate.setErrorHandler(new HttpClientErrorJsonBodyHandler<>(jsonMapper, "DEBT-POSITIONS", clientConfig.isPrintBodyWhenError(),
      DebtPositionErrorDTO.class, DebtPositionErrorDTOMapper::map)
    );

    this.debtPositionTypeOrgApi = new DebtPositionTypeOrgApi(apiClient);
  }

  @PreDestroy
  public void unload() {
    bearerTokenHolder.remove();
  }

  public DebtPositionTypeOrgApi getDebtPositionTypeOrgApi(String accessToken) {
    return getApi(accessToken, debtPositionTypeOrgApi);
  }

  private <T extends BaseApi> T getApi(String accessToken, T api) {
    bearerTokenHolder.set(accessToken);
    return api;
  }
}
