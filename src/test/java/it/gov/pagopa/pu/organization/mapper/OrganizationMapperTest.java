package it.gov.pagopa.pu.organization.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import it.gov.pagopa.pu.organization.dto.OrganizationDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationMapperTest {

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
  void givenNullDtoWhenMapToModelThenReturnNull() {
    assertNull(organizationMapper.toModel(null));
  }

  @Test
  void givenValidDtoWhenMapToModelThenReturnValidOrganization() {
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
    assertEquals(dto.getExternalOrganizationId(), result.getExternalOrganizationId());
    assertEquals(dto.getIpaCode(), result.getIpaCode());
    assertEquals(dto.getOrgFiscalCode(), result.getOrgFiscalCode());
    assertEquals(dto.getOrgName(), result.getOrgName());
    assertEquals(dto.getOrgTypeCode(), result.getOrgTypeCode());
    assertEquals(dto.getOrgEmail(), result.getOrgEmail());
    assertEquals(dto.getPostalIban(), result.getPostalIban());
    assertEquals(dto.getIban(), result.getIban());
    assertEquals(expectedEncryptedPassword, result.getPassword());
    assertEquals(dto.getSegregationCode(), result.getSegregationCode());
    assertEquals(dto.getCbillInterBankCode(), result.getCbillInterBankCode());
    assertEquals(dto.getOrgLogo(), result.getOrgLogo());
    assertEquals(dto.getStatus(), result.getStatus());
    assertEquals(dto.getAdditionalLanguage(), result.getAdditionalLanguage());
    assertEquals(dto.getStartDate(), result.getStartDate());
    assertEquals(dto.getBrokerId(), result.getBrokerId());
    assertEquals(expectedEncryptedIoApiKey, result.getIoApiKey());
    assertEquals(expectedEncryptedSendApiKey, result.getSendApiKey());
    assertEquals(expectedEncryptedGenerateNoticeApiKey, result.getGenerateNoticeApiKey());
    assertEquals(dto.getFlagNotifyIo(), result.isFlagNotifyIo());
    assertEquals(dto.getFlagNotifyOutcomePush(), result.isFlagNotifyOutcomePush());
    assertEquals(dto.getFlagPaymentNotification(), result.isFlagPaymentNotification());
    assertEquals(dto.getPdndEnabled(), result.isPdndEnabled());
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
    org.setAdditionalLanguage("additionalLanguage");
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

    OrganizationDTO dto = organizationMapper.mapToDTO(org);

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
    assertEquals(org.isFlagNotifyOutcomePush(), dto.isFlagNotifyOutcomePush());
    assertEquals(org.isFlagPaymentNotification(), dto.isFlagPaymentNotification());
    assertEquals(org.isPdndEnabled(), dto.isPdndEnabled());
    assertEquals(org.isFlagTreasury(), dto.isFlagTreasury());
    assertEquals(org.getBrokerId(), dto.getBrokerId());
  }

  @Test
  void givenOrganizationWithNullPasswordAndNullApiKeysWhenMapToDTOThenReturnDTOWithNulls() {
    Organization org = new Organization();
    org.setOrganizationId(200L);
    org.setOrgName("Null Org");
    org.setIpaCode("IPA_NULL");
    org.setPassword(null);
    org.setIoApiKey(null);
    org.setSendApiKey(null);
    org.setGenerateNoticeApiKey(null);

    org.setStatus(OrganizationStatus.DRAFT);

    OrganizationDTO dto = organizationMapper.mapToDTO(org);

    assertNotNull(dto);
    assertEquals(org.getOrganizationId(), dto.getOrganizationId());
    assertEquals(org.getOrgName(), dto.getOrgName());
    assertEquals(org.getIpaCode(), dto.getIpaCode());
    assertEquals(org.getStatus(), dto.getStatus());

    assertNull(dto.getPassword());
    assertNull(dto.getIoApiKey());
    assertNull(dto.getSendApiKey());
    assertNull(dto.getGenerateNoticeApiKey());

    verifyNoInteractions(encryptionServiceMock);
  }
}
