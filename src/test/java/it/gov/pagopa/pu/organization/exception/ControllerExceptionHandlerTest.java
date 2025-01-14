package it.gov.pagopa.pu.organization.exception;

import it.gov.pagopa.pu.organization.controller.BrokerController;
import it.gov.pagopa.pu.organization.service.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
@Import({BrokerController.class})
@AutoConfigureMockMvc(addFilters = false)
class ControllerExceptionHandlerTest {

  @MockitoBean
  private BrokerService brokerServiceMock;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void test() throws Exception{
    //given
    Long invalidBrokerId = 1L;
    String errorMessage = "broker [%s]".formatted(invalidBrokerId);
    Mockito.when(brokerServiceMock.getBrokerApiKeys(invalidBrokerId)).thenThrow(new ResourceNotFoundException(errorMessage));
    //when
    mockMvc.perform(
        MockMvcRequestBuilders.get("/brokers/apiKey/{brokerId}", invalidBrokerId) )
    //verify
      .andExpect(MockMvcResultMatchers.status().isNotFound())
      .andExpect(MockMvcResultMatchers.jsonPath("$.message")
        .value("resource not found: %s".formatted(errorMessage)))
      .andReturn();

    Mockito.verify(brokerServiceMock, Mockito.times(1)).getBrokerApiKeys(invalidBrokerId);
  }

}
