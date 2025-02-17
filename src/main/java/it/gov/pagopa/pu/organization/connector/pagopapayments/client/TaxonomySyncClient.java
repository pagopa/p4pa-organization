package it.gov.pagopa.pu.organization.connector.pagopapayments.client;

import it.gov.pagopa.pu.organization.connector.pagopapayments.config.PagopaPaymentsApisHolder;
import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxonomySyncClient {


  private final PagopaPaymentsApisHolder pagopaPaymentsApisHolder;

  public TaxonomySyncClient(PagopaPaymentsApisHolder pagopaPaymentsApisHolder) {
    this.pagopaPaymentsApisHolder = pagopaPaymentsApisHolder;
  }


  public List<Taxonomy> syncTaxonomy(String accessToken) {
    return pagopaPaymentsApisHolder.getTaxonomiesApi(accessToken)
            .fetchTaxonomies();
  }


}
