package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.enums.PagoPaInteractionModel;
import it.gov.pagopa.pu.organization.model.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StationMapper {

  public Station toModel(BrokerRequestDTO broker) {
    if (broker == null) {
      return null;
    }

    Station station = new Station();
    station.setBrokerId(broker.getBrokerId());
    station.setStationId(broker.getStationId());
    station.setBroadcastStationId(broker.getBroadcastStationId());
    station.setPagoPaInteractionModel(PagoPaInteractionModel.valueOf(broker.getPagoPaInteractionModel()));
    station.setEnabled(true);
    return station;
  }
}
