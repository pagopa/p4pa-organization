package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.enums.PagoPaInteractionModel;
import it.gov.pagopa.pu.organization.model.Station;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StationMapperTest {

  @InjectMocks
  StationMapper mapper;


  private BrokerRequestDTO brokerRequestDTO;

  @BeforeEach
  void setUp() {
    brokerRequestDTO = BrokerRequestDTO.builder()
      .brokerId(1L)
      .stationId("12345000000_01")
      .broadcastStationId("99999000015_04")
      .pagoPaInteractionModel("SYNC_ACA")
      .build();
  }

  @Test
  void givenNullBrokerRequestDTOWhenMapToModelThenReturnNull() {
    assertNull(mapper.toModel(null));
  }

  @Test
  void testToModelMapsAllFields() {
    Station result = mapper.toModel(brokerRequestDTO);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");

    assertThat(result.getBrokerId()).isEqualTo(1L);
    assertThat(result.getStationId()).isEqualTo("12345000000_01");
    assertThat(result.getBroadcastStationId()).isEqualTo("99999000015_04");
    assertThat(result.getPagoPaInteractionModel()).isEqualTo(PagoPaInteractionModel.SYNC_ACA);
    assertThat(result.isEnabled()).isTrue();
  }
}
