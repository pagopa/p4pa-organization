package it.gov.pagopa.pu.organization.service;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
  private static final Long VALID_BROKER_ID = 1L;
  private static final Broker VALID_BROKER = Broker.builder()
    .brokerId(VALID_BROKER_ID)
    .syncKey(VALID_ENCRYPTED_SYNC_PASSWORD)
    .acaKey(VALID_ENCRYPTED_ACA_PASSWORD)
    .gpdKey(VALID_ENCRYPTED_GPD_PASSWORD)
    .build();
  private static final BrokerApiKeys VALID_BROKER_API_KEYS = BrokerApiKeys.builder()
    .syncKey(List.of(VALID_ENCRYPTED_SYNC_PASSWORD).toString())
    .acaKey(List.of(VALID_ENCRYPTED_ACA_PASSWORD).toString())
    .gpdKey(List.of(VALID_ENCRYPTED_GPD_PASSWORD).toString())
    .build();

  @Mock
  private BrokerRepository brokerRepositoryMock;

  @Mock
  private BrokerEncryptionService brokerEncryptionService;

  private BrokerService brokerService;

  @BeforeEach
  void setUp() {
    brokerService = new BrokerService(brokerRepositoryMock, brokerEncryptionService);
  }

  @Test
  void givenValidBrokerIdWhenGetBrokerApiKeysThenOk(){
    //given
    Mockito.when(brokerRepositoryMock.findById(VALID_BROKER_ID)).thenReturn(Optional.of(VALID_BROKER));
    Mockito.when(brokerEncryptionService.getBrokerDecryptedApiKeys(VALID_BROKER)).thenReturn(VALID_BROKER_API_KEYS);

    //when
    BrokerApiKeys response = brokerService.getBrokerApiKeys(VALID_BROKER_ID);

    //verify
    Assertions.assertEquals(List.of(VALID_ENCRYPTED_SYNC_PASSWORD).toString(),response.getSyncKey());
    Assertions.assertEquals(List.of(VALID_ENCRYPTED_ACA_PASSWORD).toString(),response.getAcaKey());
    Assertions.assertEquals(List.of(VALID_ENCRYPTED_GPD_PASSWORD).toString(),response.getGpdKey());
    Mockito.verify(brokerRepositoryMock, Mockito.times(1)).findById(VALID_BROKER_ID);
    Mockito.verify(brokerEncryptionService, Mockito.times(1)).getBrokerDecryptedApiKeys(VALID_BROKER);
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
}
