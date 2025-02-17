package it.gov.pagopa.pu.organization.connector.pagopapayments.config;

import it.gov.pagopa.pu.organization.config.RestTemplateConfig;
import it.gov.pagopa.pu.pagopapayments.controller.ApiClient;
import it.gov.pagopa.pu.pagopapayments.controller.BaseApi;
import it.gov.pagopa.pu.pagopapayments.controller.generated.TaxonomiesApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PagopaPaymentsApisHolder {

  private final TaxonomiesApi taxonomiesApi;

  private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

  public PagopaPaymentsApisHolder(
    PagopaPaymentsApiClientConfig clientConfig,
    RestTemplateBuilder restTemplateBuilder
  ) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    ApiClient apiClient = new ApiClient(restTemplate);
    apiClient.setBasePath(clientConfig.getBaseUrl());
    apiClient.setBearerToken(bearerTokenHolder::get);
    apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
    apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
    if (clientConfig.isPrintBodyWhenError()) {
      restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("PAGOPA-PAYMENTS"));
    }

    this.taxonomiesApi = new TaxonomiesApi(apiClient);
  }

  @PreDestroy
  public void unload(){
    bearerTokenHolder.remove();
  }


  public TaxonomiesApi getTaxonomiesApi(String accessToken) {
    return getApi(accessToken, taxonomiesApi);
  }


  private <T extends BaseApi> T getApi(String accessToken, T api) {
    bearerTokenHolder.set(accessToken);
    return api;
  }

}
