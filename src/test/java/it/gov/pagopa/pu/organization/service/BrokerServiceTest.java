package it.gov.pagopa.pu.organization.service;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.util.AESUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class BrokerServiceTest {

  private static final String VALID_BROKER_ENCRYPT_PASSWORD = "VALID_PASSWORD";
  private static final byte[] VALID_ENCRYPTED_SYNC_PASSWORD = new byte[]{1, 2, 3};
  private static final byte[] VALID_ENCRYPTED_ACA_PASSWORD = new byte[]{4, 5, 6};
  private static final byte[] VALID_ENCRYPTED_GPD_PASSWORD = new byte[]{7, 8, 9};
  private static final Long VALID_BROKER_ID = 1L;
  private static final Broker VALID_BROKER = Broker.builder()
    .brokerId(VALID_BROKER_ID)
    .syncKey(VALID_ENCRYPTED_SYNC_PASSWORD)
    .acaKey(VALID_ENCRYPTED_ACA_PASSWORD)
    .gpdKey(VALID_ENCRYPTED_GPD_PASSWORD)
    .build();

  @Mock
  private BrokerRepository brokerRepositoryMock;

  private BrokerService brokerService;

  @BeforeEach
  void setUp() {
    brokerService = new BrokerService(VALID_BROKER_ENCRYPT_PASSWORD, brokerRepositoryMock);
  }

  @Test
  void givenValidBrokerIdWhenGetBrokerApiKeysThenOk(){
    //given
    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID)).thenReturn(Optional.of(VALID_BROKER));
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      Stream.of(VALID_ENCRYPTED_SYNC_PASSWORD, VALID_ENCRYPTED_ACA_PASSWORD, VALID_ENCRYPTED_GPD_PASSWORD).forEach( p ->
        aesUtilsMock.when(() -> AESUtils.decrypt(VALID_BROKER_ENCRYPT_PASSWORD, p))
          .thenReturn(List.of(p).toString())
        );

      //when
      BrokerApiKeys response = brokerService.getBrokerApiKeys(VALID_BROKER_ID);

      //verify
      Assertions.assertEquals(List.of(VALID_ENCRYPTED_SYNC_PASSWORD).toString(),response.getSyncKey());
      Assertions.assertEquals(List.of(VALID_ENCRYPTED_ACA_PASSWORD).toString(),response.getAcaKey());
      Assertions.assertEquals(List.of(VALID_ENCRYPTED_GPD_PASSWORD).toString(),response.getGpdKey());
      aesUtilsMock.verify(() -> AESUtils.decrypt(Mockito.eq(VALID_BROKER_ENCRYPT_PASSWORD), Mockito.any(byte[].class)), Mockito.times(3));
    }
  }

  @Test
  void givenValidBrokerIdWithoutKeyWhenGetBrokerApiKeysThenNUllKey(){
    //given
    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID)).thenReturn(Optional.of(Broker.builder().build()));

    //when
    BrokerApiKeys response = brokerService.getBrokerApiKeys(VALID_BROKER_ID);

    //verify
    Assertions.assertNull(response.getSyncKey());
    Assertions.assertNull(response.getAcaKey());
    Assertions.assertNull(response.getGpdKey());
  }

  @Test
  void givenNotFoundBrokerIdWhenGetBrokerApiKeysThenException(){
    //given
    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID)).thenThrow(new HttpServerErrorException(HttpStatus.NOT_FOUND));

    //when
    HttpServerErrorException exception = Assertions.assertThrows(HttpServerErrorException.class, () -> brokerService.getBrokerApiKeys(VALID_BROKER_ID));

    //verify
    Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }
}
