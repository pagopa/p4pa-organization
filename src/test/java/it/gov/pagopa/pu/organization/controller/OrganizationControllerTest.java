package it.gov.pagopa.pu.organization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.service.organization.OrganizationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrganizationController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrganizationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private OrganizationService organizationServiceMock;

  @Test
  void whenEncryptAndSaveApiKeyThenOk() throws Exception {

    OrganizationApiKeys organizationApiKeys = new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.IO, "apikey");

    mockMvc.perform(
        put("/organization/1/apiKey")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(objectMapper.writeValueAsString(organizationApiKeys)))
      .andExpect(status().isOk())
      .andReturn();

    verify(organizationServiceMock).encryptAndSaveApiKey(1L, organizationApiKeys);
  }

  @Test
  void whenGetApiKeyThenOk() throws Exception {
    String apiKey = "apikey";
    Mockito.when(organizationServiceMock.getApiKey(1L, OrganizationApiKeyType.IO)).thenReturn(apiKey);

    MvcResult result = mockMvc.perform(
        get("/organization/1/apiKey/IO")
          .contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(apiKey, result.getResponse().getContentAsString());
  }
}
