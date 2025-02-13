package it.gov.pagopa.pu.organization.connector.taxonomy.config;

import it.gov.pagopa.pu.pagopapayments.controller.ApiClient;
import it.gov.pagopa.pu.pagopapayments.controller.BaseApi;
import it.gov.pagopa.pu.pagopapayments.controller.generated.TaxonomiesApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class TaxonomyApisHolder {

  private final TaxonomiesApi taxonomiesApi;

  private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

  public TaxonomyApisHolder(@Value("${rest.pagopa-payments.base-url}") String baseUrl,
                            RestTemplateBuilder restTemplateBuilder) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    ApiClient apiClient = new ApiClient(restTemplate);
    apiClient.setBasePath(baseUrl);
    apiClient.setBearerToken(bearerTokenHolder::get);

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
