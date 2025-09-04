package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.organization.dto.OrganizationUpdateDTO;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.mapper.OrganizationMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import it.gov.pagopa.pu.organization.service.broker.BrokerEncryptionService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import it.gov.pagopa.pu.organization.util.faker.OrganizationFaker;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

import static it.gov.pagopa.pu.organization.util.faker.OrganizationFaker.buildOrganization;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  @Mock
  private OrganizationEncryptionService organizationEncryptionServiceMock;
  @Mock
  private OrganizationRepository organizationRepositoryMock;
  @Mock
  private BrokerRepository brokerRepositoryMock;
  @Mock
  private BrokerEncryptionService brokerEncryptionServiceMock;
  @Mock
  private OrganizationMapper organizationMapperMock;
  @Mock
  private DebtPositionTypeOrgClient debtPositionTypeOrgClientMock;

  private OrganizationService service;

  @BeforeEach
  void setUp() {
    service = new OrganizationService(organizationEncryptionServiceMock,
      brokerEncryptionServiceMock, organizationMapperMock,
      organizationRepositoryMock, brokerRepositoryMock,
      debtPositionTypeOrgClientMock, true);
  }

  @Test
  void givenCreateOrganizationThenSuccess() {
    String accessToken = TestUtils.getFakeAccessToken();

    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT60X0542811101000000123456");

    Organization organization = OrganizationFaker.buildOrganization();
    when(organizationMapperMock.toModel(dto)).thenReturn(organization);
    when(organizationRepositoryMock.save(organization)).thenReturn(organization);

    service.createOrganization(dto, accessToken);

    verify(debtPositionTypeOrgClientMock).createTechnicalDebtPositionTypeOrg(organization.getOrganizationId(), accessToken);
    verifyNoMoreInteractions(organizationRepositoryMock);
  }

  @Test
  void givenInvalidIbanWhenCreateOrganizationThenInvalidValueException() {
    String accessToken = TestUtils.getFakeAccessToken();

    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("iban");

    Executable exec = () -> service.createOrganization(dto, accessToken);

    assertThrows(InvalidValueException.class, exec);
  }

  @Test
  void givenInvalidPostalIbanWhenCreateOrganizationThenInvalidValueException() {
    String accessToken = TestUtils.getFakeAccessToken();

    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("iban");

    Executable exec = () -> service.createOrganization(dto, accessToken);

    assertThrows(InvalidValueException.class, exec);
  }

  @Test
  void givenInvalidOrgFiscalCodeWhenCreateOrganizationThenInvalidValueException() {
    String accessToken = TestUtils.getFakeAccessToken();

    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT60X0542811101000000123456");

    Executable exec = () -> service.createOrganization(dto, accessToken);

    assertThrows(InvalidValueException.class, exec);
  }

  @Test
  void givenEncryptAndSaveIOApiKeyThenSuccess() {
    // Given
    String plainText = "PLAINTEXT";
    byte[] encryptedKey = new byte[64];
    OrganizationApiKeys organizationApiKeys = new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.IO, plainText);

    Mockito.when(organizationEncryptionServiceMock.encrypt(plainText))
      .thenReturn(encryptedKey);

    Mockito.when(organizationRepositoryMock.updateIoApiKey(1L, encryptedKey))
      .thenReturn(1);

    // When
    service.encryptAndSaveApiKey(1L, organizationApiKeys);

    // Then
    verify(organizationRepositoryMock).updateIoApiKey(1L, encryptedKey);
  }

  @Test
  void givenEncryptAndSaveSendApiKeyThenSuccess() {
    // Given
    String plainText = "PLAINTEXT";
    byte[] encryptedKey = new byte[64];
    OrganizationApiKeys organizationApiKeys = new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.SEND, plainText);

    Mockito.when(organizationEncryptionServiceMock.encrypt(plainText))
      .thenReturn(encryptedKey);

    Mockito.when(organizationRepositoryMock.updateSendApiKey(1L, encryptedKey))
      .thenReturn(1);

    // When
    service.encryptAndSaveApiKey(1L, organizationApiKeys);

    // Then
    verify(organizationRepositoryMock).updateSendApiKey(1L, encryptedKey);
  }

  @Test
  void givenEncryptAndSaveApiKeyWhenOrganizationNotFoundThenThrowOrganizationNotFoundException() {
    // Given
    String plainText = "PLAINTEXT";
    byte[] encryptedKey = new byte[64];
    OrganizationApiKeys organizationApiKeys = new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.SEND, plainText);

    Mockito.when(organizationEncryptionServiceMock.encrypt(plainText))
      .thenReturn(encryptedKey);

    Mockito.when(organizationRepositoryMock.updateSendApiKey(1L, encryptedKey))
      .thenReturn(0);

    // When & Then
    OrganizationNotFoundException exception = assertThrows(OrganizationNotFoundException.class, () ->
      service.encryptAndSaveApiKey(1L, organizationApiKeys));

    assertEquals("Organization with ID 1 was not found", exception.getMessage());
  }

  @Test
  void givenGetApiKeyIOThenSuccess() {
    Long organizationId = 1L;
    Organization organization = buildOrganization();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.IO;

    String expectedApiKey = "apiKey";

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    Mockito.when(organizationEncryptionServiceMock.decryptKey(organization.getIoApiKey()))
      .thenReturn(expectedApiKey);

    String result = service.getApiKey(organizationId, keyType);

    assertEquals(expectedApiKey, result);
  }

  @Test
  void givenGetApiKeyIOWithOrgNotEnabledThenSuccess() {
    Long organizationId = 1L;
    Organization organization = buildOrganization();
    organization.setFlagNotifyIo(false);
    OrganizationApiKeyType keyType = OrganizationApiKeyType.IO;

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));

    String result = service.getApiKey(organizationId, keyType);

    assertNull(result);
  }

  @Test
  void givenGetApiKeySENDThenSuccess() {
    Long organizationId = 1L;
    Organization organization = buildOrganization();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.SEND;

    String expectedApiKey = "apiKey";

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    Mockito.when(organizationEncryptionServiceMock.decryptKey(organization.getSendApiKey()))
      .thenReturn(expectedApiKey);

    String result = service.getApiKey(organizationId, keyType);

    assertEquals(expectedApiKey, result);
  }

  @Test
  void givenGetApiKeyWithOrgNotFoundThenThrowException() {
    Long organizationId = 1L;
    OrganizationApiKeyType keyType = OrganizationApiKeyType.SEND;

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.empty());

    ResourceNotFoundException result = assertThrows(ResourceNotFoundException.class,
      () -> service.getApiKey(organizationId, keyType));

    assertEquals("Organization [1]", result.getMessage());
  }

  @Test
  void givenEncryptAndSaveGenerateNoticeApiKeyThenSuccess(){
    // Given
    String plainText = "PLAINTEXT";
    byte[] encryptedKey = new byte[64];
    OrganizationApiKeys organizationApiKeys = new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.GENERATE_NOTICE, plainText);

    Mockito.when(organizationEncryptionServiceMock.encrypt(plainText))
      .thenReturn(encryptedKey);

    Mockito.when(organizationRepositoryMock.updateGenerateNoticeApiKey(1L, encryptedKey))
      .thenReturn(1);

    // When
    service.encryptAndSaveApiKey(1L, organizationApiKeys);

    // Then
    verify(organizationRepositoryMock).updateGenerateNoticeApiKey(1L, encryptedKey);
  }

  @Test
  void givenGetApiKeyGenerateNoticeThenSuccess() {
    Long organizationId = 1L;
    Organization organization = buildOrganization();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.GENERATE_NOTICE;

    String expectedApiKey = "apiKey";

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    Mockito.when(organizationEncryptionServiceMock.decryptKey(organization.getGenerateNoticeApiKey()))
      .thenReturn(expectedApiKey);

    String result = service.getApiKey(organizationId, keyType);

    assertEquals(expectedApiKey, result);
  }

  @Test
  void givenGetApiKeyGenerateNoticeNoOrganizationThenSuccess() {
    Long organizationId = 1L;
    Organization organization = buildOrganization();
    organization.setGenerateNoticeApiKey(null);
    Broker broker = new Broker();
    broker.setGenerateNoticeKey("apiKey".getBytes());
    OrganizationApiKeyType keyType = OrganizationApiKeyType.GENERATE_NOTICE;

    String expectedApiKey = "apiKey";

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    Mockito.when(brokerRepositoryMock.findByBrokeredOrganizationId(String.valueOf(organizationId)))
      .thenReturn(Optional.of(broker));
    Mockito.when(brokerEncryptionServiceMock.decryptKey(broker.getGenerateNoticeKey(), BrokerApiKeyType.GENERATE_NOTICE, broker.getBrokerId()))
      .thenReturn(expectedApiKey);

    String result = service.getApiKey(organizationId, keyType);

    assertEquals(expectedApiKey, result);
  }

  @Test
  void givenGetApiKeyGenerateNoticeBrokerNotFoundThenThrowException() {
    Long organizationId = 1L;
    Organization organization = buildOrganization();
    organization.setGenerateNoticeApiKey(null);
    OrganizationApiKeyType keyType = OrganizationApiKeyType.GENERATE_NOTICE;

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    Mockito.when(brokerRepositoryMock.findByBrokeredOrganizationId(String.valueOf(organizationId)))
      .thenReturn(Optional.empty());

    ResourceNotFoundException result = assertThrows(ResourceNotFoundException.class,
      () -> service.getApiKey(organizationId, keyType));

    assertEquals("Broker not found for orgId [1]", result.getMessage());
  }

  @Test
  void givenValidOrganizationDTOWhenUpdateOrganizationThenOk(){
    OrganizationUpdateDTO organizationUpdateDTO = podamFactory.manufacturePojo(OrganizationUpdateDTO.class);
    organizationUpdateDTO.setOrgFiscalCode("12345678903");
    organizationUpdateDTO.setIban("IT60X0542811101000000123456");
    organizationUpdateDTO.setPostalIban("IT60X0542811101000000123456");
    Organization organization = OrganizationFaker.buildOrganization();
    organization.setBrokerId(organizationUpdateDTO.getBrokerId());
    organization.setExternalOrganizationId(organizationUpdateDTO.getExternalOrganizationId());
    organization.setIpaCode(organizationUpdateDTO.getIpaCode());
    organization.setOrgFiscalCode(organizationUpdateDTO.getOrgFiscalCode());
    organization.setOrgName(organizationUpdateDTO.getOrgName());
    organization.setOrgTypeCode(organizationUpdateDTO.getOrgTypeCode());
    when(organizationRepositoryMock.findById(organizationUpdateDTO.getOrganizationId())).thenReturn(Optional.of(organization));
    when(organizationMapperMock.toModel(organizationUpdateDTO)).thenReturn(organization);
    when(organizationRepositoryMock.save(organization)).thenReturn(organization);

    service.updateOrganization(organizationUpdateDTO);

    verifyNoMoreInteractions(organizationRepositoryMock);
  }

  @Test
  void givenStatusActiveAndNoMandatoryFieldWhenUpdateOrganizationThenValidationException(){
    OrganizationUpdateDTO organizationUpdateDTO = podamFactory.manufacturePojo(OrganizationUpdateDTO.class);
    organizationUpdateDTO.setOrgFiscalCode("12345678903");
    organizationUpdateDTO.setIban("IT60X0542811101000000123456");
    organizationUpdateDTO.setPostalIban("IT60X0542811101000000123456");
    organizationUpdateDTO.setSegregationCode(null);
    organizationUpdateDTO.setStatus(OrganizationStatus.ACTIVE);
    Organization organization = OrganizationFaker.buildOrganization();
    organization.setBrokerId(organizationUpdateDTO.getBrokerId());
    organization.setExternalOrganizationId(organizationUpdateDTO.getExternalOrganizationId());
    organization.setIpaCode(organizationUpdateDTO.getIpaCode());
    organization.setOrgFiscalCode(organizationUpdateDTO.getOrgFiscalCode());
    organization.setOrgName(organizationUpdateDTO.getOrgName());
    organization.setOrgTypeCode(organizationUpdateDTO.getOrgTypeCode()+"old");
    organization.setStatus(OrganizationStatus.DRAFT);
    when(organizationRepositoryMock.findById(organizationUpdateDTO.getOrganizationId())).thenReturn(Optional.of(organization));

    assertThrows(ValidationException.class,()->service.updateOrganization(organizationUpdateDTO));

    verifyNoInteractions(organizationMapperMock);
  }

  @Test
  void givenUpdatedImmutableFieldWhenUpdateOrganizationThenValidationException(){
    OrganizationUpdateDTO organizationUpdateDTO = podamFactory.manufacturePojo(OrganizationUpdateDTO.class);
    organizationUpdateDTO.setOrgFiscalCode("12345678903");
    organizationUpdateDTO.setIban("IT60X0542811101000000123456");
    organizationUpdateDTO.setPostalIban("IT60X0542811101000000123456");
    Organization organization = OrganizationFaker.buildOrganization();
    organization.setBrokerId(organizationUpdateDTO.getBrokerId());
    organization.setExternalOrganizationId(organizationUpdateDTO.getExternalOrganizationId());
    organization.setIpaCode(organizationUpdateDTO.getIpaCode());
    organization.setOrgFiscalCode(organizationUpdateDTO.getOrgFiscalCode());
    organization.setOrgName(organizationUpdateDTO.getOrgName());
    organization.setOrgTypeCode(organizationUpdateDTO.getOrgTypeCode()+"old");
    when(organizationRepositoryMock.findById(organizationUpdateDTO.getOrganizationId())).thenReturn(Optional.of(organization));

    assertThrows(ValidationException.class,()->service.updateOrganization(organizationUpdateDTO));

    verifyNoInteractions(organizationMapperMock);
  }

  @Test
  void givenNonExistingOrganizationWhenUpdateOrganizationThenResourceNotFoundException(){
    OrganizationUpdateDTO organizationUpdateDTO = podamFactory.manufacturePojo(OrganizationUpdateDTO.class);
    when(organizationRepositoryMock.findById(organizationUpdateDTO.getOrganizationId())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,()->service.updateOrganization(organizationUpdateDTO));

    verifyNoInteractions(organizationMapperMock);
  }
}
