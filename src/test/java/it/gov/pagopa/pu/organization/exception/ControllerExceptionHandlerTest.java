package it.gov.pagopa.pu.organization.exception;

import it.gov.pagopa.pu.organization.controller.BrokerController;
import it.gov.pagopa.pu.organization.service.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
@Import({BrokerController.class})
public class ControllerExceptionHandlerTest {

  @MockBean
  private BrokerService brokerServiceMock;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void test() throws Exception{
    //given
    Long INVALID_BROKER_ID = 1L;
    String ERR_MESSAGE = "broker [%s]".formatted(INVALID_BROKER_ID);
    Mockito.when(brokerServiceMock.getBrokerApiKeys(INVALID_BROKER_ID)).thenThrow(new ResourceNotFoundException(ERR_MESSAGE));
    //when
    mockMvc.perform(
        MockMvcRequestBuilders.get("/brokers/apiKey/{brokerId}", INVALID_BROKER_ID) )
    //verify
      .andExpect(MockMvcResultMatchers.status().isNotFound())
      .andExpect(MockMvcResultMatchers.jsonPath("$.message")
        .value("resource not found: %s".formatted(ERR_MESSAGE)))
      .andReturn();

    Mockito.verify(brokerServiceMock, Mockito.times(1)).getBrokerApiKeys(INVALID_BROKER_ID);
  }

}
