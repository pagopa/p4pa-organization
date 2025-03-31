package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.service.broker.BrokerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BrokerControllerTest {

  @Mock
  private BrokerService brokerServiceMock;

  @InjectMocks
  private BrokerController brokerController;

  private static final Long VALID_BROKER_ID = 1L;

  private static final BrokerApiKeys VALID_BROKER_API_KEYS = BrokerApiKeys.builder()
    .syncKey("sync")
    .acaKey("aca")
    .gpdKey("gpd")
    .build();

  @Test
  void givenValidBrokerWhenGetBrokerApiKeysThenOk(){
    //given
    Mockito.when(brokerServiceMock.getBrokerApiKeys(VALID_BROKER_ID)).thenReturn(VALID_BROKER_API_KEYS);
    //when
    ResponseEntity<BrokerApiKeys> response = brokerController.getBrokerApiKeys(VALID_BROKER_ID);
    //verify
    Assertions.assertNotNull(response);
    Assertions.assertEquals(VALID_BROKER_API_KEYS, response.getBody());
    Mockito.verify(brokerServiceMock, Mockito.times(1)).getBrokerApiKeys(VALID_BROKER_ID);
  }
}
