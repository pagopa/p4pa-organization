package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.OrganizationStationDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationAdditionalLanguage;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import it.gov.pagopa.pu.organization.service.organization.OrganizationService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationMapperTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  @InjectMocks
  private OrganizationMapper organizationMapper;

  @Mock
  private OrganizationEncryptionService encryptionServiceMock;
  @Mock
  private OrganizationService organizationServiceMock;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      encryptionServiceMock,
      organizationServiceMock
    );
  }

  @Test
  void givenNullOrganizationCreateDTOWhenMapToModelThenReturnNull() {
    assertNull(organizationMapper.toModel((OrganizationCreateDTO) null));
  }

  @Test
  void givenValidOrganizationCreateDTOWhenMapToModelThenReturnValidOrganization() {
    OrganizationCreateDTO dto = OrganizationCreateDTO.builder()
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
      .additionalLanguage(OrganizationAdditionalLanguage.EN)
      .startDate(LocalDate.now())
      .brokerId(1L)
      .ioApiKey("ioApiKey")
      .sendApiKey("sendApiKey")
      .generateNoticeApiKey("generateNoticeApiKey")
      .flagNotifyIo(false)
      .flagNotifyOutcomePush(false)
      .flagPaymentNotification(false)
      .flagTreasury(true)
      .pdndEnabled(true)
      .address("address")
      .zipCode("zipCode")
      .city("city")
      .build();

    byte[] expectedEncryptedPassword = "encryptedPassword".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encrypt(dto.getPassword())).thenReturn(expectedEncryptedPassword);

    byte[] expectedEncryptedIoApiKey = "encryptedIoApiKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encrypt(dto.getIoApiKey())).thenReturn(expectedEncryptedIoApiKey);

    byte[] expectedEncryptedSendApiKey = "encryptedSendApiKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encrypt(dto.getSendApiKey())).thenReturn(expectedEncryptedSendApiKey);

    byte[] expectedEncryptedGenerateNoticeApiKey = "encryptedGenerateNoticeApiKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encrypt(dto.getGenerateNoticeApiKey())).thenReturn(expectedEncryptedGenerateNoticeApiKey);

    Organization result = organizationMapper.toModel(dto);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result, "organizationId", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId", "defaultOrganizationStationId");

    assertThat(result)
      .usingRecursiveComparison()
      .ignoringFields(
        "organizationId",
        "creationDate",
        "updateDate",
        "updateOperatorExternalId",
        "updateTraceId",
        "password",
        "ioApiKey",
        "sendApiKey",
        "generateNoticeApiKey",
        "flagClassification",
        "flagPaymentsReporting"
      )
      .isEqualTo(dto);

    assertThat(result.getPassword()).isEqualTo(expectedEncryptedPassword);
    assertThat(result.getIoApiKey()).isEqualTo(expectedEncryptedIoApiKey);
    assertThat(result.getSendApiKey()).isEqualTo(expectedEncryptedSendApiKey);
    assertThat(result.getGenerateNoticeApiKey()).isEqualTo(expectedEncryptedGenerateNoticeApiKey);
  }

  @Test
  void givenNullOrganizationWhenMapToDTOThenReturnNull() {
    assertNull(organizationMapper.mapToDTO(null));
  }

  @Test
  void givenValidOrganizationWhenMapToDTOThenReturnValidDTO() {
    Organization org = new Organization();
    org.setOrganizationId(1L);
    org.setExternalOrganizationId("externalOrganizationId");
    org.setIpaCode("ipaCode");
    org.setOrgFiscalCode("orgFiscalCode");
    org.setOrgName("orgName");
    org.setOrgTypeCode("orgTypeCode");
    org.setOrgEmail("orgEmail");
    org.setPostalIban("postalIban");
    org.setIban("iban");
    org.setCbillInterBankCode("cbillInterBankCode");
    org.setOrgLogo("orgLogo");
    org.setStatus(OrganizationStatus.DRAFT);
    org.setAdditionalLanguage(OrganizationAdditionalLanguage.EN);
    org.setStartDate(LocalDate.now());
    org.setFlagNotifyIo(true);
    org.setFlagNotifyOutcomePush(false);
    org.setFlagPaymentNotification(true);
    org.setPdndEnabled(true);
    org.setFlagTreasury(false);
    org.setBrokerId(10L);
    org.setAddress("address");
    org.setZipCode("zipCode");
    org.setCity("city");

    byte[] encryptedPassword = "encryptedPassword".getBytes(StandardCharsets.UTF_8);
    byte[] encryptedIoApiKey = "encryptedIoApiKey".getBytes(StandardCharsets.UTF_8);
    byte[] encryptedSendApiKey = "encryptedSendApiKey".getBytes(StandardCharsets.UTF_8);
    byte[] encryptedGenerateNoticeApiKey = "encryptedGenerateNoticeApiKey".getBytes(StandardCharsets.UTF_8);

    org.setPassword(encryptedPassword);
    org.setIoApiKey(encryptedIoApiKey);
    org.setSendApiKey(encryptedSendApiKey);
    org.setGenerateNoticeApiKey(encryptedGenerateNoticeApiKey);

    when(encryptionServiceMock.decryptKey(encryptedPassword)).thenReturn("plainPassword");
    when(encryptionServiceMock.decryptKey(encryptedIoApiKey)).thenReturn("plainIoApiKey");
    when(encryptionServiceMock.decryptKey(encryptedSendApiKey)).thenReturn("plainSendApiKey");
    when(encryptionServiceMock.decryptKey(encryptedGenerateNoticeApiKey)).thenReturn("plainGenerateNoticeApiKey");

    OrganizationStationDTO organizationStationDTO = new OrganizationStationDTO();
    organizationStationDTO.setSegregationCode("segregationCode");
    when(organizationServiceMock.getOrganizationStation(1L, null)).thenReturn(organizationStationDTO);

    OrganizationDetailDTO dto = organizationMapper.mapToDTO(org);

    assertNotNull(dto);
    assertThat(dto)
      .usingRecursiveComparison()
      .ignoringFields(
        "password",
        "ioApiKey",
        "sendApiKey",
        "generateNoticeApiKey",
        "segregationCode"
      )
      .isEqualTo(org);

    assertThat(dto.getPassword()).isEqualTo("plainPassword");
    assertThat(dto.getIoApiKey()).isEqualTo("plainIoApiKey");
    assertThat(dto.getSendApiKey()).isEqualTo("plainSendApiKey");
    assertThat(dto.getGenerateNoticeApiKey()).isEqualTo("plainGenerateNoticeApiKey");
    assertThat(dto.getSegregationCode()).isEqualTo("segregationCode");
  }

  @Test
  void givenNullOrganizationDetailDTOWhenMapToModelThenReturnNull() {
    assertNull(organizationMapper.toModel( null));
  }

  @Test
  void givenValidOrganizationDetailDTOWhenMapToModelThenReturnValidOrganization() {
    OrganizationDetailDTO dto = podamFactory.manufacturePojo(OrganizationDetailDTO.class);

    byte[] expectedEncryptedPassword = "encryptedPassword".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encrypt(dto.getPassword())).thenReturn(expectedEncryptedPassword);

    byte[] expectedEncryptedIoApiKey = "encryptedIoApiKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encrypt(dto.getIoApiKey())).thenReturn(expectedEncryptedIoApiKey);

    byte[] expectedEncryptedSendApiKey = "encryptedSendApiKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encrypt(dto.getSendApiKey())).thenReturn(expectedEncryptedSendApiKey);

    byte[] expectedEncryptedGenerateNoticeApiKey = "encryptedGenerateNoticeApiKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encrypt(dto.getGenerateNoticeApiKey())).thenReturn(expectedEncryptedGenerateNoticeApiKey);

    Organization result = organizationMapper.toModel(dto);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId", "defaultOrganizationStationId");
    TestUtils.reflectionEqualsByName(dto,result,"password","ioApiKey","sendApiKey","generateNoticeApiKey");
    assertEquals(expectedEncryptedPassword, result.getPassword());
    assertEquals(expectedEncryptedIoApiKey, result.getIoApiKey());
    assertEquals(expectedEncryptedSendApiKey, result.getSendApiKey());
    assertEquals(expectedEncryptedGenerateNoticeApiKey, result.getGenerateNoticeApiKey());
  }
}
