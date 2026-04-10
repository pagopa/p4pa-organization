package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.organization.connector.workflow.service.WorkflowDebtPositionService;
import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

import static it.gov.pagopa.pu.organization.util.faker.OrganizationFaker.buildOrganization;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  @Mock
  private WorkflowDebtPositionService workflowDebtPositionServiceMock;

  private OrganizationService service;

  @BeforeEach
  void setUp() {
    service = new OrganizationService(organizationEncryptionServiceMock,
      brokerEncryptionServiceMock, organizationMapperMock,
      organizationRepositoryMock, brokerRepositoryMock,
      debtPositionTypeOrgClientMock, workflowDebtPositionServiceMock, true);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
      organizationEncryptionServiceMock,
      organizationRepositoryMock,
      brokerRepositoryMock,
      brokerEncryptionServiceMock,
      organizationMapperMock,
      debtPositionTypeOrgClientMock);
  }

  @ParameterizedTest
  @CsvSource(value = {
    "12345678903,IT60X0542811101000000123456,IT60X0542811101000000123456,12,true",
    "12345678903,iban,IT60X0542811101000000123456,12,false",
    "12345678903,IT60X0542811101000000123456,iban,12,false",
    "null,IT60X0542811101000000123456,IT60X0542811101000000123456,12,false",
    "12345678903,IT60X0542811101000000123456,IT60X0542811101000000123456,abc,false"
  }, nullValues = {"null"})
  void testCreateOrganizationParameterized(String orgFiscalCode, String iban, String postalIban, String segregationCode, boolean expectedSuccess) {
    String accessToken = TestUtils.getFakeAccessToken();
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode(orgFiscalCode);
    dto.setIban(iban);
    dto.setPostalIban(postalIban);
    dto.setSegregationCode(segregationCode);
    dto.setSegregationCode(segregationCode);

    if (expectedSuccess) {
      Organization organization = OrganizationFaker.buildOrganization();
      when(organizationMapperMock.toModel(dto)).thenReturn(organization);
      when(organizationRepositoryMock.save(organization)).thenReturn(organization);
      Organization result = service.createOrganization(dto, accessToken);
      Assertions.assertSame(organization, result);
      verify(debtPositionTypeOrgClientMock).createTechnicalDebtPositionTypeOrg(organization.getOrganizationId(), accessToken);
    } else {
      Assertions.assertThrows(InvalidValueException.class, () -> service.createOrganization(dto, accessToken));
    }
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

    assertEquals("[ORGANIZATION_NOT_FOUND] Organization with id 1 not found", exception.getMessage());
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

    assertEquals("[ORGANIZATION_NOT_FOUND] Organization with id 1 not found", result.getMessage());
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

    assertEquals("[BROKER_NOT_FOUND] Broker for org with id 1 not found", result.getMessage());
  }

  @Test
  void givenExistingOrganizationWhenGetOrganizationThenReturnDTO() {
    Long organizationId = 1L;
    Organization org = new Organization();
    org.setOrganizationId(organizationId);

    OrganizationDetailDTO expectedDto = new OrganizationDetailDTO();
    expectedDto.setOrganizationId(organizationId);

    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(org));
    when(organizationMapperMock.mapToDTO(org)).thenReturn(expectedDto);

    OrganizationDetailDTO result = service.getOrganization(organizationId);

    assertNotNull(result);
    assertEquals(expectedDto.getOrganizationId(), result.getOrganizationId());
    verify(organizationRepositoryMock).findById(organizationId);
    verify(organizationMapperMock).mapToDTO(org);
  }

  @Test
  void givenNonExistingOrganizationWhenGetOrganizationThenThrowException() {
    Long organizationId = 99L;

    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getOrganization(organizationId));

    verify(organizationRepositoryMock).findById(organizationId);
  }

  @Test
  void givenValidOrganizationDTOWhenUpdateOrganizationThenOk(){
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organizationDetailDTO.setOrgFiscalCode("12345678903");
    organizationDetailDTO.setIban("IT60X0542811101000000123456");
    organizationDetailDTO.setPostalIban("IT60X0542811101000000123456");
    organizationDetailDTO.setSegregationCode("02");
    Organization organization = OrganizationFaker.buildOrganization();
    organization.setBrokerId(organizationDetailDTO.getBrokerId());
    organization.setExternalOrganizationId(organizationDetailDTO.getExternalOrganizationId());
    organization.setIpaCode(organizationDetailDTO.getIpaCode());
    organization.setOrgFiscalCode(organizationDetailDTO.getOrgFiscalCode());
    organization.setOrgName(organizationDetailDTO.getOrgName());
    organization.setOrgTypeCode(organizationDetailDTO.getOrgTypeCode());
    organization.setSegregationCode(organizationDetailDTO.getSegregationCode());
    when(organizationRepositoryMock.findById(organizationDetailDTO.getOrganizationId())).thenReturn(Optional.of(organization));
    when(organizationMapperMock.toModel(organizationDetailDTO)).thenReturn(organization);
    when(organizationRepositoryMock.save(organization)).thenReturn(organization);

    service.updateOrganization(organizationDetailDTO);
  }

  @Test
  void givenStatusActiveAndNoMandatoryFieldWhenUpdateOrganizationThenValidationException(){
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organizationDetailDTO.setOrgFiscalCode("12345678903");
    organizationDetailDTO.setIban("IT60X0542811101000000123456");
    organizationDetailDTO.setPostalIban("IT60X0542811101000000123456");
    organizationDetailDTO.setSegregationCode(null);
    organizationDetailDTO.setStatus(OrganizationStatus.ACTIVE);
    Organization organization = OrganizationFaker.buildOrganization();
    organization.setBrokerId(organizationDetailDTO.getBrokerId());
    organization.setExternalOrganizationId(organizationDetailDTO.getExternalOrganizationId());
    organization.setIpaCode(organizationDetailDTO.getIpaCode());
    organization.setOrgFiscalCode(organizationDetailDTO.getOrgFiscalCode());
    organization.setOrgName(organizationDetailDTO.getOrgName());
    organization.setOrgTypeCode(organizationDetailDTO.getOrgTypeCode());
    when(organizationRepositoryMock.findById(organizationDetailDTO.getOrganizationId())).thenReturn(Optional.of(organization));

    assertThrows(ValidationException.class,()->service.updateOrganization(organizationDetailDTO));
  }

  @Test
  void givenUpdatedImmutableFieldWhenUpdateOrganizationThenValidationException(){
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organizationDetailDTO.setOrgFiscalCode("12345678903");
    organizationDetailDTO.setIban("IT60X0542811101000000123456");
    organizationDetailDTO.setPostalIban("IT60X0542811101000000123456");
    organizationDetailDTO.setSegregationCode("01");
    Organization organization = OrganizationFaker.buildOrganization();
    organization.setBrokerId(organizationDetailDTO.getBrokerId());
    organization.setExternalOrganizationId(organizationDetailDTO.getExternalOrganizationId());
    organization.setIpaCode(organizationDetailDTO.getIpaCode());
    organization.setOrgFiscalCode(organizationDetailDTO.getOrgFiscalCode());
    organization.setOrgName(organizationDetailDTO.getOrgName());
    organization.setSegregationCode(organizationDetailDTO.getSegregationCode());
    organization.setOrgTypeCode(organizationDetailDTO.getOrgTypeCode()+"old");
    when(organizationRepositoryMock.findById(organizationDetailDTO.getOrganizationId())).thenReturn(Optional.of(organization));

    assertThrows(ValidationException.class,()->service.updateOrganization(organizationDetailDTO));
  }

  @Test
  void givenNonExistingOrganizationWhenUpdateOrganizationThenResourceNotFoundException(){
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    when(organizationRepositoryMock.findById(organizationDetailDTO.getOrganizationId())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,()->service.updateOrganization(organizationDetailDTO));
  }

  @Test
  void givenNonExistingOrganizationWhenUpdateOrganizationStatusThenResourceNotFoundException(){
    Long organizationId = 1L;
    OrganizationStatus newStatus = OrganizationStatus.ACTIVE;

    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,()->service.updateOrganizationStatus(organizationId, newStatus));
  }

  @Test
  void givenStatusActiveAndNoMandatoryFieldWhenUpdateOrganizationStatusThenValidationException(){
    Long organizationId = 1L;
    OrganizationStatus newStatus = OrganizationStatus.ACTIVE;

    Organization organization = new Organization();
    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));

    assertThrows(ValidationException.class,()->service.updateOrganizationStatus(organizationId, newStatus));
  }

  @Test
  void givenValidRequestWhenUpdateOrganizationStatusThenOk(){
    Long organizationId = 1L;
    OrganizationStatus newStatus = OrganizationStatus.ACTIVE;

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    organization.setStatus(OrganizationStatus.DRAFT);
    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));

    service.updateOrganizationStatus(organizationId, newStatus);

    Mockito.verify(organizationRepositoryMock).save(Mockito.same(organization));
    Assertions.assertEquals(newStatus, organization.getStatus());
  }
}
