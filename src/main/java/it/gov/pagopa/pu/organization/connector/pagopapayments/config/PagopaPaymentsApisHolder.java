package it.gov.pagopa.pu.organization.connector.pagopapayments.config;

import it.gov.pagopa.pu.organization.config.rest.HttpClientErrorJsonBodyHandler;
import it.gov.pagopa.pu.organization.connector.pagopapayments.mapper.PagoPaPaymentsErrorDTOMapper;
import it.gov.pagopa.pu.pagopapayments.generated.ApiClient;
import it.gov.pagopa.pu.pagopapayments.generated.BaseApi;
import it.gov.pagopa.pu.pagopapayments.client.generated.TaxonomiesApi;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PagoPaPaymentsErrorDTO;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Service
public class PagopaPaymentsApisHolder {

  private final TaxonomiesApi taxonomiesApi;

  private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

  public PagopaPaymentsApisHolder(
    PagopaPaymentsApiClientConfig clientConfig,
    RestTemplateBuilder restTemplateBuilder,
    JsonMapper jsonMapper
  ) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    ApiClient apiClient = new ApiClient(restTemplate);
    apiClient.setBasePath(clientConfig.getBaseUrl());
    apiClient.setBearerToken(bearerTokenHolder::get);
    apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
    apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
    restTemplate.setErrorHandler(new HttpClientErrorJsonBodyHandler<>(jsonMapper, "PAGOPA-PAYMENTS", clientConfig.isPrintBodyWhenError(),
      PagoPaPaymentsErrorDTO.class, PagoPaPaymentsErrorDTOMapper::map)
    );

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
