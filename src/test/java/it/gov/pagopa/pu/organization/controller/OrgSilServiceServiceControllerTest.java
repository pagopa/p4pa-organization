package it.gov.pagopa.pu.organization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.enums.OrgSilServiceType;
import it.gov.pagopa.pu.organization.dto.orgsilservice.SilServiceLegacyBasicAuthConfigDTO;
import it.gov.pagopa.pu.organization.service.organization.OrgSilServiceService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrganizationSilServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrgSilServiceServiceControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private OrgSilServiceService orgSilServiceServiceMock;

  @Test
  void givenExistingOrgSilServiceIdThenReturnsDTO() throws Exception {
    // Given
    OrgSilServiceDTO orgSilServiceDTO = new OrgSilServiceDTO();
    orgSilServiceDTO.setOrgSilServiceId(1L);
    orgSilServiceDTO.setApplicationName("Test Service");

    when(orgSilServiceServiceMock.getById(1L))
      .thenReturn(orgSilServiceDTO);

    // When
    mockMvc.perform(
        get("/organization-sil-service/1")
          .contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isOk())
      .andReturn();

    // Then
    verify(orgSilServiceServiceMock).getById(1L);
  }

  @Test
  void givenNonExistingOrgSilServiceIdThenReturn404() throws Exception {
    // Given
    when(orgSilServiceServiceMock.getById(1L))
      .thenThrow(new ResourceNotFoundException("OrgSilService not found with ID: 1"));

    // When
    mockMvc.perform(
        get("/organization-sil-service/1")
          .contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isNotFound())
      .andReturn();

    // Then
    verify(orgSilServiceServiceMock).getById(1L);
  }

  @Test
  void givenValidDataWhenCreateOrUpdateThenReturnsDTO() throws Exception {
    // Given
    OrgSilServiceDTO orgSilServiceDTO = new OrgSilServiceDTO();
    orgSilServiceDTO.setOrgSilServiceId(1L);
    orgSilServiceDTO.setApplicationName("Test Service");
    orgSilServiceDTO.setOrganizationId(1L);
    orgSilServiceDTO.setServiceType(OrgSilServiceType.ACTUALIZATION);
    orgSilServiceDTO.setFlagLegacy(true);
    orgSilServiceDTO.setAuthConfig(new SilServiceLegacyBasicAuthConfigDTO());
    orgSilServiceDTO.setServiceUrl("http://localhost:8080/organization-sil-service/1");

    when(orgSilServiceServiceMock.createOrUpdate(any(OrgSilServiceDTO.class)))
      .thenReturn(orgSilServiceDTO);

    // When
    mockMvc.perform(
        post("/organization-sil-service")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(objectMapper.writeValueAsString(orgSilServiceDTO)))
      .andExpect(status().isOk())
      .andReturn();

    // Then
    ArgumentCaptor<OrgSilServiceDTO> captor = ArgumentCaptor.forClass(OrgSilServiceDTO.class);
    verify(orgSilServiceServiceMock).createOrUpdate(captor.capture());
    assertEquals(orgSilServiceDTO.getOrgSilServiceId(), captor.getValue().getOrgSilServiceId());
    assertEquals(orgSilServiceDTO.getApplicationName(), captor.getValue().getApplicationName());
  }
}
