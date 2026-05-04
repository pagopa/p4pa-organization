package it.gov.pagopa.pu.organization.service.station;

import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.mapper.StationMapper;
import it.gov.pagopa.pu.organization.model.Station;
import it.gov.pagopa.pu.organization.repository.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

  @Mock
  private StationMapper stationMapperMock;
  @Mock
  private StationRepository stationRepositoryMock;

  @InjectMocks
  private StationService service;

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(stationMapperMock, stationRepositoryMock);
  }

  @Test
  void givenBrokerWhenUpsertStationThenOk() {
    BrokerRequestDTO requestDTO = new BrokerRequestDTO();
    requestDTO.setBrokerId(1L);
    requestDTO.setStationId("STATION");

    Station expectedStation = new Station();
    expectedStation.setStationId("STATION");
    expectedStation.setBrokerId(1L);

    Mockito.when(stationMapperMock.toModel(requestDTO)).thenReturn(expectedStation);
    Mockito.when(stationRepositoryMock.save(expectedStation)).thenReturn(expectedStation);

    Station result = service.upsertStation(requestDTO);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedStation, result);
    Mockito.verify(stationRepositoryMock).save(expectedStation);
  }
}
