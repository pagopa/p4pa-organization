package it.gov.pagopa.pu.organization.model;

import it.gov.pagopa.pu.organization.enums.PagoPaInteractionModel;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "brokerProjection", types = { Broker.class })
public interface BrokerProjection {
  Long getBrokerId();
  String getOrganizationId();
  String getBrokerFiscalCode();
  String getBrokerName();
  PagoPaInteractionModel pagoPaInteractionMode();
  String getStationId();
  String getBroadcastStationId();
  byte[] getSyncKey();
  byte[] getGpdKey();
  byte[] getAcaKey();
  String getPersonalisationFe();
}
