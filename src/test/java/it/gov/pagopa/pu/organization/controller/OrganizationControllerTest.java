package it.gov.pagopa.pu.organization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.pu.organization.dto.OrganizationUpdateDTO;
import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.service.organization.OrganizationService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrganizationController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrganizationControllerTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private OrganizationService organizationServiceMock;

  @Test
  void whenCreateOrganizationThenOk() throws Exception {
    TestUtils.setFakeAccessTokenInContext();

    OrganizationCreateDTO organizationCreateDTO = OrganizationCreateDTO.builder()
      .externalOrganizationId("externalOrganizationId")
      .ipaCode("ipaCode")
      .orgFiscalCode("orgFiscalCode")
      .orgName("orgName")
      .orgTypeCode("orgTypeCode")
      .orgEmail("orgEmail")
      .postalIban("postalIban")
      .iban("iban")
      .password("plainPassword")
      .segregationCode("segregationCode")
      .cbillInterBankCode("cbillInterBankCode")
      .orgLogo("orgLogo")
      .status(OrganizationStatus.DRAFT)
      .additionalLanguage("additionalLanguage")
      .startDate(LocalDate.now())
      .brokerId(1L)
      .ioApiKey("ioApiKey")
      .sendApiKey("sendApiKey")
      .generateNoticeApiKey("generateNoticeApiKey")
      .flagNotifyIo(false)
      .flagNotifyOutcomePush(false)
      .flagPaymentNotification(false)
      .pdndEnabled(true)
      .build();

    mockMvc.perform(
        post("/organization")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(objectMapper.writeValueAsString(organizationCreateDTO)))
      .andExpect(status().isOk())
      .andReturn();

    verify(organizationServiceMock).createOrganization(organizationCreateDTO, TestUtils.getFakeAccessToken());
  }

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

  @Test
  void givenNoKeyWhenGetApiKeyThenOk() throws Exception {
    Mockito.when(organizationServiceMock.getApiKey(1L, OrganizationApiKeyType.IO)).thenReturn(null);

    mockMvc.perform(
        get("/organization/1/apiKey/IO")
          .contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isNoContent());
  }

  @Test
  void whenGetOrganizationThenOk() throws Exception {
    OrganizationDetailDTO dto = new OrganizationDetailDTO();
    dto.setOrganizationId(1L);
    dto.setOrgName("My Org");
    dto.setIpaCode("IPA123");

    Mockito.when(organizationServiceMock.getOrganization(1L)).thenReturn(dto);

    MvcResult result = mockMvc.perform(
        get("/organization/1")
          .contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isOk())
      .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    OrganizationDetailDTO responseDto = objectMapper.readValue(responseBody, OrganizationDetailDTO.class);

    assertEquals(dto.getOrganizationId(), responseDto.getOrganizationId());
    assertEquals(dto.getOrgName(), responseDto.getOrgName());
    assertEquals(dto.getIpaCode(), responseDto.getIpaCode());

    verify(organizationServiceMock).getOrganization(1L);
  }


  @Test
  void whenUpdateOrganizationThenOk() throws Exception {
    TestUtils.setFakeAccessTokenInContext();

    OrganizationUpdateDTO organizationUpdateDTO = podamFactory.manufacturePojo(OrganizationUpdateDTO.class);

    mockMvc.perform(
                    put("/organization")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(organizationUpdateDTO)))
            .andExpect(status().isOk())
            .andReturn();

    verify(organizationServiceMock).updateOrganization(organizationUpdateDTO);
  }
}
