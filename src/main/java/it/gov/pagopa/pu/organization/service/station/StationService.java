package it.gov.pagopa.pu.organization.service.station;

import it.gov.pagopa.pu.organization.mapper.StationMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Station;
import it.gov.pagopa.pu.organization.repository.StationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StationService {

  private final StationRepository stationRepository;
  private final StationMapper stationMapper;

  public StationService(StationRepository stationRepository, StationMapper stationMapper) {
    this.stationRepository = stationRepository;
    this.stationMapper = stationMapper;
  }

  public Station upsertStation(Broker broker) {
    return stationRepository.save(stationMapper.toModel(broker));
  }
}
