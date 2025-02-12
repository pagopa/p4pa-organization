package it.gov.pagopa.pu.organization.connector;

import it.gov.pagopa.pu.pagopapayments.dto.generated.Taxonomy;

import java.util.List;

public interface PagopaPaymentsClient {
  List<Taxonomy> fetchTaxonomy(String accessToken);
}
