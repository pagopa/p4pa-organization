package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationAdditionalLanguage;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationMapperTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  @InjectMocks
  private OrganizationMapper organizationMapper;

  @Mock
  private OrganizationEncryptionService encryptionServiceMock;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      encryptionServiceMock
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
    TestUtils.checkNotNullFields(result, "organizationId", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");

    assertSame(dto.getExternalOrganizationId(), result.getExternalOrganizationId());
    assertSame(dto.getIpaCode(), result.getIpaCode());
    assertSame(dto.getOrgFiscalCode(), result.getOrgFiscalCode());
    assertSame(dto.getOrgName(), result.getOrgName());
    assertSame(dto.getOrgTypeCode(), result.getOrgTypeCode());
    assertSame(dto.getOrgEmail(), result.getOrgEmail());
    assertSame(dto.getPostalIban(), result.getPostalIban());
    assertSame(dto.getIban(), result.getIban());
    assertSame(expectedEncryptedPassword, result.getPassword());
    assertSame(dto.getSegregationCode(), result.getSegregationCode());
    assertSame(dto.getCbillInterBankCode(), result.getCbillInterBankCode());
    assertSame(dto.getOrgLogo(), result.getOrgLogo());
    assertSame(dto.getStatus(), result.getStatus());
    assertSame(dto.getAdditionalLanguage(), result.getAdditionalLanguage());
    assertSame(dto.getStartDate(), result.getStartDate());
    assertSame(dto.getBrokerId(), result.getBrokerId());
    assertSame(expectedEncryptedIoApiKey, result.getIoApiKey());
    assertSame(expectedEncryptedSendApiKey, result.getSendApiKey());
    assertSame(expectedEncryptedGenerateNoticeApiKey, result.getGenerateNoticeApiKey());
    assertSame(dto.getFlagNotifyIo(), result.isFlagNotifyIo());
    assertSame(dto.getFlagNotifyOutcomePush(), result.isFlagNotifyOutcomePush());
    assertSame(dto.getFlagPaymentNotification(), result.isFlagPaymentNotification());
    assertSame(dto.getPdndEnabled(), result.isPdndEnabled());
    assertSame(dto.getFlagTreasury(), result.isFlagTreasury());
  }

  @Test
  void givenNullOrganizationWhenMapToDTOThenReturnNull() {
    assertNull(organizationMapper.mapToDTO(null));
  }

  @Test
  void givenValidOrganizationWhenMapToDTOThenReturnValidDTO() {
    Organization org = new Organization();
    org.setOrganizationId(100L);
    org.setExternalOrganizationId("externalOrganizationId");
    org.setIpaCode("ipaCode");
    org.setOrgFiscalCode("orgFiscalCode");
    org.setOrgName("orgName");
    org.setOrgTypeCode("orgTypeCode");
    org.setOrgEmail("orgEmail");
    org.setPostalIban("postalIban");
    org.setIban("iban");
    org.setSegregationCode("segregationCode");
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

    OrganizationDetailDTO dto = organizationMapper.mapToDTO(org);

    assertNotNull(dto);
    assertEquals(org.getOrganizationId(), dto.getOrganizationId());
    assertEquals(org.getExternalOrganizationId(), dto.getExternalOrganizationId());
    assertEquals(org.getIpaCode(), dto.getIpaCode());
    assertEquals(org.getOrgFiscalCode(), dto.getOrgFiscalCode());
    assertEquals(org.getOrgName(), dto.getOrgName());
    assertEquals(org.getOrgTypeCode(), dto.getOrgTypeCode());
    assertEquals(org.getOrgEmail(), dto.getOrgEmail());
    assertEquals(org.getPostalIban(), dto.getPostalIban());
    assertEquals(org.getIban(), dto.getIban());
    assertEquals("plainPassword", dto.getPassword());
    assertEquals("plainIoApiKey", dto.getIoApiKey());
    assertEquals("plainSendApiKey", dto.getSendApiKey());
    assertEquals("plainGenerateNoticeApiKey", dto.getGenerateNoticeApiKey());
    assertEquals(org.getSegregationCode(), dto.getSegregationCode());
    assertEquals(org.getCbillInterBankCode(), dto.getCbillInterBankCode());
    assertEquals(org.getOrgLogo(), dto.getOrgLogo());
    assertEquals(org.getStatus(), dto.getStatus());
    assertEquals(org.getAdditionalLanguage(), dto.getAdditionalLanguage());
    assertEquals(org.getStartDate(), dto.getStartDate());
    assertFalse(dto.getFlagNotifyOutcomePush());
    assertTrue(dto.getFlagPaymentNotification());
    assertTrue(dto.getPdndEnabled());
    assertFalse(dto.getFlagTreasury());
    assertEquals(org.getBrokerId(), dto.getBrokerId());
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
    TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
    TestUtils.reflectionEqualsByName(dto,result,"password","ioApiKey","sendApiKey","generateNoticeApiKey");
    assertEquals(expectedEncryptedPassword, result.getPassword());
    assertEquals(expectedEncryptedIoApiKey, result.getIoApiKey());
    assertEquals(expectedEncryptedSendApiKey, result.getSendApiKey());
    assertEquals(expectedEncryptedGenerateNoticeApiKey, result.getGenerateNoticeApiKey());
  }
}
