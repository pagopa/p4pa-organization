package it.gov.pagopa.pu.organization.exception;

import it.gov.pagopa.pu.organization.exception.common.CommonExceptionHandlerTest;
import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doThrow;

class OrganizationExceptionHandlerTest extends CommonExceptionHandlerTest {

  @Test
  void handleBrokerNotFoundException() throws Exception {
    doThrow(new BrokerNotFoundException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isNotFound())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("ORGANIZATION_NOT_FOUND"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("BROKER_NOT_FOUND"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));

  }

}
