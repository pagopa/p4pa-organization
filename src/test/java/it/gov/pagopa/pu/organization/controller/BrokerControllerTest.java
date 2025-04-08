package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKey;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.service.broker.BrokerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BrokerControllerTest {

  @Mock
  private BrokerService brokerServiceMock;

  @InjectMocks
  private BrokerController brokerController;

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(brokerServiceMock);
  }

  private static final Long VALID_BROKER_ID = 1L;

  private static final BrokerApiKeys VALID_BROKER_API_KEYS = BrokerApiKeys.builder()
    .syncKey("sync")
    .acaKey("aca")
    .gpdKey("gpd")
    .generateNoticeKey("notice")
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
  }

  @Test
  void givenValidBrokerWhenEncryptAndSaveBrokerApiKeyThenOk(){
    //given
    BrokerApiKey brokerApiKey = new BrokerApiKey();

    Mockito.doNothing().when(brokerServiceMock)
      .encryptAndSaveApiKey(Mockito.same(VALID_BROKER_ID), Mockito.same(brokerApiKey));
    //when
    ResponseEntity<Void> response = brokerController.encryptAndSaveBrokerApiKey(VALID_BROKER_ID, brokerApiKey);
    //verify
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
  }

  @Test
  void givenValidBrokerWhenGetApiKeyThenOk() {
    //given
    Mockito.when(brokerServiceMock.getBrokerApiKey(VALID_BROKER_ID, BrokerApiKeyType.GENERATE_NOTICE)).thenReturn("validKey");
    //when
    ResponseEntity<String> response = brokerController.getBrokerApiKey(VALID_BROKER_ID, BrokerApiKeyType.GENERATE_NOTICE);
    //verify
    Assertions.assertNotNull(response);
    Assertions.assertEquals("validKey", response.getBody());
  }
}
