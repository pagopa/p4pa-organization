package it.gov.pagopa.pu.organization.connector;

import it.gov.pagopa.pu.organization.util.RestUtil;
import it.gov.pagopa.pu.pagopapayments.controller.ApiClient;
import it.gov.pagopa.pu.pagopapayments.controller.generated.TaxonomiesApi;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PagopaPaymentsClientImpl implements PagopaPaymentsClient {

  private final TaxonomiesApi taxonomiesApi;

  private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();


public PagopaPaymentsClientImpl(@Value("${rest.pagopa-payments.base-url}") String baseUrl,
                                  RestTemplateBuilder restTemplateBuilder) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    ApiClient apiClient = new ApiClient(restTemplate)
      .setBasePath(baseUrl);
    apiClient.setBearerToken(bearerTokenHolder::get);
    this.taxonomiesApi = new TaxonomiesApi(apiClient);
  }

  @PreDestroy
  public void unload(){
    bearerTokenHolder.remove();
  }

  @Override
  public List<Taxonomy> fetchTaxonomy(String accessToken) {
    bearerTokenHolder.set(accessToken);
    return RestUtil.handleRestException(taxonomiesApi :: fetchTaxonomies,"fetchTaxonomies");
  }

}
