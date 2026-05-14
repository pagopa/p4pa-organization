package it.gov.pagopa.pu.organization.service.broker;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKey;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.mapper.BrokerMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Station;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BrokerServiceTest {

  private static final byte[] VALID_ENCRYPTED_SYNC_PASSWORD = new byte[]{1, 2, 3};
  private static final byte[] VALID_ENCRYPTED_ACA_PASSWORD = new byte[]{4, 5, 6};
  private static final byte[] VALID_ENCRYPTED_GPD_PASSWORD = new byte[]{7, 8, 9};
  private static final byte[] VALID_ENCRYPTED_GENERATE_NOTICE_PASSWORD = new byte[]{10, 11, 12};
  private static final Long VALID_BROKER_ID = 1L;
  private static final Broker VALID_BROKER = Broker.builder()
    .brokerId(VALID_BROKER_ID)
    .syncKey(VALID_ENCRYPTED_SYNC_PASSWORD)
    .acaKey(VALID_ENCRYPTED_ACA_PASSWORD)
    .gpdKey(VALID_ENCRYPTED_GPD_PASSWORD)
    .generateNoticeKey(VALID_ENCRYPTED_GENERATE_NOTICE_PASSWORD)
    .build();
  private static final BrokerApiKeys VALID_BROKER_API_KEYS = BrokerApiKeys.builder()
    .syncKey(List.of(VALID_ENCRYPTED_SYNC_PASSWORD).toString())
    .acaKey(List.of(VALID_ENCRYPTED_ACA_PASSWORD).toString())
    .gpdKey(List.of(VALID_ENCRYPTED_GPD_PASSWORD).toString())
    .generateNoticeKey(List.of(VALID_ENCRYPTED_GENERATE_NOTICE_PASSWORD).toString())
    .build();

  @Mock
  private BrokerRepository brokerRepositoryMock;

  @Mock
  private BrokerEncryptionService brokerEncryptionServiceMock;

  @Mock
  private BrokerMapper brokerMapperMock;

  @Mock
  private StationService stationServiceMock;

  private BrokerService brokerService;

  @BeforeEach
  void setUp() {
    brokerService = new BrokerService(brokerRepositoryMock, brokerEncryptionServiceMock, brokerMapperMock, stationServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(brokerRepositoryMock, brokerEncryptionServiceMock, brokerMapperMock, stationServiceMock);
  }

  @Test
  void givenValidBrokerIdWhenGetBrokerApiKeysThenOk(){
    //given
    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID)).thenReturn(Optional.of(VALID_BROKER));
    Mockito.when(brokerEncryptionServiceMock.getBrokerDecryptedApiKeys(VALID_BROKER)).thenReturn(VALID_BROKER_API_KEYS);

    //when
    BrokerApiKeys response = brokerService.getBrokerApiKeys(VALID_BROKER_ID);

    //verify
    Assertions.assertEquals(List.of(VALID_ENCRYPTED_SYNC_PASSWORD).toString(),response.getSyncKey());
    Assertions.assertEquals(List.of(VALID_ENCRYPTED_ACA_PASSWORD).toString(),response.getAcaKey());
    Assertions.assertEquals(List.of(VALID_ENCRYPTED_GPD_PASSWORD).toString(),response.getGpdKey());
  }

  @Test
  void givenNotFoundBrokerIdWhenGetBrokerApiKeysThenException(){
    //given
    String errorMessage = "broker [%s]".formatted(VALID_BROKER_ID);
    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID)).thenThrow(new ResourceNotFoundException(errorMessage));

    //when
    ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> brokerService.getBrokerApiKeys(VALID_BROKER_ID));

    //verify
    Assertions.assertEquals(errorMessage, exception.getMessage());
  }

  @Test
  void givenNotFoundBrokerIdWhenEncryptAndSaveApiKeyThenException(){
    //given
    BrokerApiKey brokerApiKey = new BrokerApiKey();
    String errorMessage = "broker [%s]".formatted(VALID_BROKER_ID);
    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID)).thenThrow(new ResourceNotFoundException(errorMessage));

    //when
    ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> brokerService.encryptAndSaveApiKey(VALID_BROKER_ID, brokerApiKey));

    //verify
    Assertions.assertEquals(errorMessage, exception.getMessage());
  }

  @ParameterizedTest
  @EnumSource(BrokerApiKeyType.class)
  void whenEncryptAndSaveApiKeyThenOk(BrokerApiKeyType keyType){
    //given
    Broker broker = new Broker();
    String apiKey = "apiKey";
    byte[] encryptedKey = new byte[0];

    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID))
      .thenReturn(Optional.of(broker));
    Mockito.when(brokerEncryptionServiceMock.encryptKey(apiKey))
      .thenReturn(encryptedKey);

    Mockito.when(brokerRepositoryMock.save(Mockito.argThat(i -> {
      Assertions.assertSame(i, broker);
      byte[] storedKey = switch (keyType){
        case SYNC_PAYMENTS_REPORTING -> broker.getSyncPaymentsReportingKey();
        case SYNC -> broker.getSyncKey();
        case ACA -> broker.getAcaKey();
        case GPD -> broker.getGpdKey();
        case GENERATE_NOTICE -> broker.getGenerateNoticeKey();
      };
      Assertions.assertSame(encryptedKey, storedKey);

      if(!BrokerApiKeyType.SYNC.equals(keyType)){
        Assertions.assertNull(broker.getSyncKey());
      }
      if(!BrokerApiKeyType.ACA.equals(keyType)){
        Assertions.assertNull(broker.getAcaKey());
      }
        if(!BrokerApiKeyType.GPD.equals(keyType)){
          Assertions.assertNull(broker.getGpdKey());
        }
        if(!BrokerApiKeyType.GENERATE_NOTICE.equals(keyType)){
          Assertions.assertNull(broker.getGenerateNoticeKey());
        }
      return true;
    })))
      .thenReturn(broker);

    //when
    brokerService.encryptAndSaveApiKey(VALID_BROKER_ID, new BrokerApiKey(keyType, apiKey));
  }

  @Test
  void givenValidBrokerIdWhenGetBrokerApiKeyThenOk(){
    //given
    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID)).thenReturn(Optional.of(VALID_BROKER));
    Mockito.when(brokerEncryptionServiceMock.getBrokerDecryptedApiKey(VALID_BROKER, BrokerApiKeyType.GENERATE_NOTICE)).thenReturn("noticeKey");

    //when
    String response = brokerService.getBrokerApiKey(VALID_BROKER_ID, BrokerApiKeyType.GENERATE_NOTICE);

    //verify
    Assertions.assertEquals("noticeKey", response);
  }

  @Test
  void givenNotFoundBrokerIdWhenGetBrokerApiKeyThenException(){
    //given
    String errorMessage = "broker [%s]".formatted(VALID_BROKER_ID);
    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID)).thenThrow(new ResourceNotFoundException(errorMessage));

    //when
    ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> brokerService.getBrokerApiKey(VALID_BROKER_ID, BrokerApiKeyType.GENERATE_NOTICE));

    //verify
    Assertions.assertEquals(errorMessage, exception.getMessage());
  }

  @Test
  void givenValidBrokerRequestDTOWhenCreateBrokerThenOk(){
    // Given
    BrokerRequestDTO brokerRequestDTO = BrokerRequestDTO.builder()
      .organizationId(23L)
      .brokerFiscalCode("99999000099")
      .brokerName("Broker Test")
      .pagoPaInteractionModel("ASYNC_GPD")
      .broadcastStationId("99999000015_04")
      .flagDelegate(true)
      .flagPaymentsReporting(true)
      .externalId("testcreate")
      .defaultStationId("12345000000_01")
      .build();

     Broker broker = Broker.builder()
      .brokerId(1L)
      .organizationId(23L)
      .brokerFiscalCode("99999000099")
      .brokerName("Broker Test")
      .defaultStationId("12345000000_01")
      .build();

    Station station = new Station();
    station.setBrokerId(1L);
    station.setStationId("12345000000_01");

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

  }
}
