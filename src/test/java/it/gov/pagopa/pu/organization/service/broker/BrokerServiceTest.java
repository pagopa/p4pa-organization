package it.gov.pagopa.pu.organization.service.broker;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKey;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.mapper.BrokerMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Station;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.service.brokerkeys.BrokerKeysService;
import it.gov.pagopa.pu.organization.service.station.StationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BrokerServiceTest {

  private static final byte[] VALID_ENCRYPTED_SYNC_PASSWORD = new byte[]{1, 2, 3};
  private static final byte[] VALID_ENCRYPTED_ACA_PASSWORD = new byte[]{4, 5, 6};
  private static final byte[] VALID_ENCRYPTED_GPD_PASSWORD = new byte[]{7, 8, 9};
  private static final byte[] VALID_ENCRYPTED_GENERATE_NOTICE_PASSWORD = new byte[]{10, 11, 12};
  private static final Long VALID_BROKER_ID = 1L;
  private static final BrokerApiKeys VALID_BROKER_API_KEYS = BrokerApiKeys.builder()
    .syncKey(List.of(VALID_ENCRYPTED_SYNC_PASSWORD).toString())
    .acaKey(List.of(VALID_ENCRYPTED_ACA_PASSWORD).toString())
    .gpdKey(List.of(VALID_ENCRYPTED_GPD_PASSWORD).toString())
    .generateNoticeKey(List.of(VALID_ENCRYPTED_GENERATE_NOTICE_PASSWORD).toString())
    .build();

  @Mock
  private BrokerRepository brokerRepositoryMock;

  @Mock
  private BrokerMapper brokerMapperMock;

  @Mock
  private StationService stationServiceMock;

  @Mock
  private BrokerKeysService brokerKeysServiceMock;

  private BrokerService brokerService;

  @BeforeEach
  void setUp() {
    brokerService = new BrokerService(brokerRepositoryMock, brokerMapperMock, stationServiceMock, brokerKeysServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(brokerRepositoryMock, brokerMapperMock, stationServiceMock, brokerKeysServiceMock);
  }

  @Test
  void givenValidBrokerIdWhenGetBrokerApiKeysThenOk(){
    //given
    Mockito.when(brokerKeysServiceMock.getBrokerDecryptedApiKeys(VALID_BROKER_ID)).thenReturn(VALID_BROKER_API_KEYS);

    //when
    BrokerApiKeys response = brokerService.getBrokerApiKeys(VALID_BROKER_ID);

    //verify
    Assertions.assertEquals(List.of(VALID_ENCRYPTED_SYNC_PASSWORD).toString(),response.getSyncKey());
    Assertions.assertEquals(List.of(VALID_ENCRYPTED_ACA_PASSWORD).toString(),response.getAcaKey());
    Assertions.assertEquals(List.of(VALID_ENCRYPTED_GPD_PASSWORD).toString(),response.getGpdKey());
  }

  @ParameterizedTest
  @EnumSource(BrokerApiKeyType.class)
  void whenEncryptAndSaveApiKeyThenOk(BrokerApiKeyType keyType){
    //given
    String apiKey = "apiKey";
    BrokerApiKey brokerApiKey = new BrokerApiKey(keyType, apiKey);

    //when
    brokerService.encryptAndSaveApiKey(VALID_BROKER_ID, brokerApiKey);

    verify(brokerKeysServiceMock).encryptAndSaveApiKey(1L, brokerApiKey);

  }

  @Test
  void givenValidBrokerIdWhenGetBrokerApiKeyThenOk(){
    //given
    Mockito.when(brokerKeysServiceMock.getBrokerDecryptedApiKey(VALID_BROKER_ID, BrokerApiKeyType.GENERATE_NOTICE)).thenReturn("noticeKey");

    //when
    String response = brokerService.getBrokerApiKey(VALID_BROKER_ID, BrokerApiKeyType.GENERATE_NOTICE);

    //verify
    Assertions.assertEquals("noticeKey", response);
  }

  @Test
  void givenValidBrokerRequestDTOWhenCreateBrokerThenOk(){
    // Given
    Long expectedBrokerId = 1L;

    BrokerRequestDTO brokerRequestDTO = buildBrokerRequestDTO();

     Broker broker = buildBroker();

    Station station = new Station();
    station.setBrokerId(1L);
    station.setStationId("12345000000_01");

    String syncKey = brokerRequestDTO.getSyncKey();
    String acaKey = brokerRequestDTO.getAcaKey();
    String gpdKey = brokerRequestDTO.getGpdKey();
    String generateNoticeKey = brokerRequestDTO.getGenerateNoticeKey();
    String syncPaymentsReportingKey = brokerRequestDTO.getSyncPaymentsReportingKey();

    Mockito.when(brokerMapperMock.toModel(brokerRequestDTO)).thenReturn(broker);
    Mockito.when(brokerRepositoryMock.save(broker))
      .thenReturn(broker);
    Mockito.when(stationServiceMock.upsertStation(brokerRequestDTO)).thenReturn(station);
    // When
    Broker result = brokerService.createBroker(brokerRequestDTO);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(broker.getBrokerId(), result.getBrokerId());
    Assertions.assertEquals(station.getStationId(), brokerRequestDTO.getDefaultStationId());

    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.SYNC, syncKey)
    );
    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.ACA, acaKey)
    );
    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.GPD, gpdKey)
    );
    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.GENERATE_NOTICE, generateNoticeKey)
    );
    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.SYNC_PAYMENTS_REPORTING, syncPaymentsReportingKey)
    );
  }

  @Test
  void testEncryptKeyNotCalledWhenAllKeysNull() {
    BrokerRequestDTO brokerRequestDTO = buildBrokerRequestDTO();
    brokerRequestDTO.setAcaKey(null);
    brokerRequestDTO.setGpdKey(null);
    brokerRequestDTO.setSyncPaymentsReportingKey(null);
    brokerRequestDTO.setSyncKey(null);
    brokerRequestDTO.setGenerateNoticeKey(null);

    Broker broker = buildBroker();

    Station station = new Station();
    station.setBrokerId(1L);
    station.setStationId("12345000000_01");

    Mockito.when(brokerMapperMock.toModel(brokerRequestDTO)).thenReturn(broker);
    Mockito.when(brokerRepositoryMock.save(broker))
      .thenReturn(broker);
    Mockito.when(stationServiceMock.upsertStation(brokerRequestDTO)).thenReturn(station);

    brokerService.createBroker(brokerRequestDTO);

    verifyNoInteractions(brokerKeysServiceMock);
  }

  private BrokerRequestDTO buildBrokerRequestDTO(){
    return BrokerRequestDTO.builder()
      .organizationId(23L)
      .brokerFiscalCode("99999000099")
      .brokerName("Broker Test")
      .pagoPaInteractionModel("ASYNC_GPD")
      .broadcastStationId("99999000015_04")
      .flagDelegate(true)
      .flagPaymentsReporting(true)
      .externalId("testcreate")
      .defaultStationId("12345000000_01")
      .syncPaymentsReportingKey("syncKey")
      .syncKey("sync")
      .gpdKey("gpd")
      .generateNoticeKey("generate")
      .acaKey("aca")
      .build();
  }

  private Broker buildBroker() {
    return Broker.builder()
      .brokerId(1L)
      .organizationId(23L)
      .brokerFiscalCode("99999000099")
      .brokerName("Broker Test")
      .defaultStationId("12345000000_01")
      .build();
  }
}
