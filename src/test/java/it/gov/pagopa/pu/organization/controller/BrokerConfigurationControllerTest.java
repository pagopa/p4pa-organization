package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigRequestDTO;
import it.gov.pagopa.pu.organization.service.brokerconfiguration.BrokerConfigurationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BrokerConfigurationControllerTest {

  @Mock
  private BrokerConfigurationService brokerConfigurationServiceMock;

  @InjectMocks
  private BrokerConfigurationController brokerConfigurationController;

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(brokerConfigurationServiceMock);
  }

  @Test
  void whenSaveBrokerEmailServerConfigThenOk(){
    Long brokerId = 1L;
    EmailServerConfigRequestDTO emailServerConfigRequestDTO = new EmailServerConfigRequestDTO();

    Mockito.doNothing().when(brokerConfigurationServiceMock).saveBrokerEmailServerConfig(brokerId, emailServerConfigRequestDTO);

    ResponseEntity<Void> response = brokerConfigurationController.saveBrokerEmailServerConfig(brokerId, emailServerConfigRequestDTO);

    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
  }

  @Test
  void givenValidBrokerWhenEncryptAndSaveBrokerApiKeyThenOk(){
    Long brokerId = 1L;
    EmailServerConfigDTO expectedResult = new EmailServerConfigDTO();

    Mockito.when(brokerConfigurationServiceMock.getBrokerEmailServerConfig(brokerId))
      .thenReturn(expectedResult);

    ResponseEntity<EmailServerConfigDTO> response = brokerConfigurationController.getBrokerEmailServerConfig(brokerId);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(expectedResult,response.getBody());
  }
}
