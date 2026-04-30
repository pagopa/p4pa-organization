package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StationMapper {

  public Station toModel(Broker broker) {
    Station station = new Station();
    station.setBrokerId(broker.getBrokerId());
    station.setStationId(broker.getStationId());
    station.setBroadcastStationId(broker.getBroadcastStationId());
    station.setPagoPaInteractionModel(broker.getPagoPaInteractionModel());
    station.setEnabled(true);
    return station;
  }
}
