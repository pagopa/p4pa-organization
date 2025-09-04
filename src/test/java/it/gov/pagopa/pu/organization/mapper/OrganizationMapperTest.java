package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrganizationUpdateDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
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
  void givenNullOrganizationDTOWhenMapToModelThenReturnNull() {
    assertNull(organizationMapper.toModel( null));
  }

  @Test
  void givenValidOrganizationDTOWhenMapToModelThenReturnValidOrganization() {
    OrganizationUpdateDTO dto = podamFactory.manufacturePojo(OrganizationUpdateDTO.class);

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
