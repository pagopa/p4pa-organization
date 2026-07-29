package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.organization.connector.workflow.service.WorkflowDebtPositionService;
import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.OrganizationStationDTO;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.mapper.OrganizationMapper;
import it.gov.pagopa.pu.organization.mapper.OrganizationStationMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import it.gov.pagopa.pu.organization.service.brokerkeys.BrokerKeysService;
import it.gov.pagopa.pu.organization.service.organizationkeys.OrganizationKeysService;
import it.gov.pagopa.pu.organization.service.organizationstation.DefaultOrganizationStationService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import it.gov.pagopa.pu.organization.util.faker.OrganizationFaker;
import it.gov.pagopa.pu.workflowhub.dto.generated.MassiveDebtPositionIbanUpdateRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

import static it.gov.pagopa.pu.organization.util.faker.OrganizationFaker.buildOrganization;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {
  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private OrganizationRepository organizationRepositoryMock;
  @Mock
  private BrokerRepository brokerRepositoryMock;
  @Mock
  private BrokerKeysService brokerKeysServiceMock;
  @Mock
  private OrganizationMapper organizationMapperMock;
  @Mock
  private DebtPositionTypeOrgClient debtPositionTypeOrgClientMock;
  @Mock
  private WorkflowDebtPositionService workflowDebtPositionServiceMock;
  @Mock
  private OrganizationStationMapper organizationStationMapperMock;
  @Mock
  private DefaultOrganizationStationService defaultOrganizationStationServiceMock;
  @Mock
  private OrganizationValidatorService organizationValidatorServiceMock;
  @Mock
  private OrganizationKeysService organizationKeysServiceMock;

  @InjectMocks
  private OrganizationService service;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      organizationRepositoryMock,
      brokerRepositoryMock,
      brokerKeysServiceMock,
      organizationMapperMock,
      debtPositionTypeOrgClientMock,
      organizationStationMapperMock,
      defaultOrganizationStationServiceMock,
      organizationValidatorServiceMock,
      organizationKeysServiceMock
    );
  }

  @Test
  void givenValidDTOWithSegregationCodeWhenCreateOrganizationThenSuccess() {
    String accessToken = TestUtils.getFakeAccessToken();
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setSegregationCode("12");
    dto.setIoApiKey("ioApiKey");
    dto.setSendApiKey("sendApiKey");
    dto.setGenerateNoticeApiKey("generateNoticeApiKey");
    dto.setBrokerId(1L);

    Organization organization = OrganizationFaker.buildOrganization();
    OrganizationStation station = new OrganizationStation();
    station.setOrganizationStationId(1L);

    when(organizationMapperMock.toModel(dto)).thenReturn(organization);
    when(organizationRepositoryMock.save(organization)).thenReturn(organization);
    when(defaultOrganizationStationServiceMock.createOrUpdateDefaultOrganizationStation(organization.getOrganizationId(), 1L, "12")).thenReturn(station);

    Organization result = service.createOrganization(dto, accessToken);

    Assertions.assertSame(organization, result);
    verify(organizationValidatorServiceMock).validateOrganizationCreateDTO(dto);
    verify(organizationRepositoryMock, Mockito.times(2)).save(organization);
    verify(debtPositionTypeOrgClientMock).createTechnicalDebtPositionTypeOrg(organization.getOrganizationId(), accessToken);

    Mockito.verify(organizationKeysServiceMock).encryptAndSave(1L, new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.SEND, dto.getSendApiKey()), null);
    Mockito.verify(organizationKeysServiceMock).encryptAndSave(1L, new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.IO, dto.getIoApiKey()), null);
    Mockito.verify(organizationKeysServiceMock).encryptAndSave(1L, new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.GENERATE_NOTICE, dto.getGenerateNoticeApiKey()), null);
  }

  @Test
  void givenValidDTOWithSegregationCodeAndKeyNullWhenCreateOrganizationThenSuccess() {
    String accessToken = TestUtils.getFakeAccessToken();
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setSegregationCode("12");
    dto.setBrokerId(1L);

    Organization organization = OrganizationFaker.buildOrganization();
    OrganizationStation station = new OrganizationStation();
    station.setOrganizationStationId(1L);

    when(organizationMapperMock.toModel(dto)).thenReturn(organization);
    when(organizationRepositoryMock.save(organization)).thenReturn(organization);
    when(defaultOrganizationStationServiceMock.createOrUpdateDefaultOrganizationStation(organization.getOrganizationId(), 1L, "12")).thenReturn(station);

    Organization result = service.createOrganization(dto, accessToken);

    Assertions.assertSame(organization, result);
    verify(organizationValidatorServiceMock).validateOrganizationCreateDTO(dto);
    verify(organizationRepositoryMock, Mockito.times(2)).save(organization);
    verify(debtPositionTypeOrgClientMock).createTechnicalDebtPositionTypeOrg(organization.getOrganizationId(), accessToken);
    verifyNoInteractions(organizationKeysServiceMock);
 }

  @Test
  void givenValidDTOWithoutSegregationCodeWhenCreateOrganizationThenSuccess() {
    String accessToken = TestUtils.getFakeAccessToken();
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setSegregationCode(null);

    Organization organization = OrganizationFaker.buildOrganization();

    when(organizationMapperMock.toModel(dto)).thenReturn(organization);
    when(organizationRepositoryMock.save(organization)).thenReturn(organization);

    Organization result = service.createOrganization(dto, accessToken);

    Assertions.assertSame(organization, result);
    verify(organizationValidatorServiceMock).validateOrganizationCreateDTO(dto);
    verify(debtPositionTypeOrgClientMock).createTechnicalDebtPositionTypeOrg(organization.getOrganizationId(), accessToken);
  }

  @Test
  void givenSegregationCodeWithoutBrokerIdWhenCreateOrganizationThenThrowException() {
    String accessToken = TestUtils.getFakeAccessToken();
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setSegregationCode("12");
    dto.setBrokerId(null);

    Organization organization = OrganizationFaker.buildOrganization();
    when(organizationMapperMock.toModel(dto)).thenReturn(organization);
    when(organizationRepositoryMock.save(organization)).thenReturn(organization);

    assertThrows(InvalidValueException.class, () -> service.createOrganization(dto, accessToken));
    verify(organizationValidatorServiceMock).validateOrganizationCreateDTO(dto);
  }

  @Test
  void givenEncryptAndSaveSendApiKeyThenSuccess() {
    // Given
    String plainText = "PLAINTEXT";
    OrganizationApiKeys organizationApiKeys = new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.SEND, plainText);

    // When
    service.encryptAndSaveApiKey(1L, organizationApiKeys, null);

    // Then
    verify(organizationKeysServiceMock).encryptAndSave(1L, organizationApiKeys, null);
  }

  @Test
  void givenGetApiKeyIOThenSuccess() {
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    Organization organization = buildOrganization();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.IO;

    String expectedApiKey = "apiKey";

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    when(organizationKeysServiceMock.getApiKey(organizationId, keyType, subUnitCode))
      .thenReturn(expectedApiKey);
    String result = service.getApiKey(organizationId, keyType, subUnitCode);

    assertEquals(expectedApiKey, result);
  }

  @Test
  void givenGetApiKeyIOWithOrgNotEnabledThenSuccess() {
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    Organization organization = buildOrganization();
    organization.setFlagNotifyIo(false);
    OrganizationApiKeyType keyType = OrganizationApiKeyType.IO;

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));

    String result = service.getApiKey(organizationId, keyType, subUnitCode);

    assertNull(result);
  }

  @Test
  void givenGetApiKeySENDThenSuccess() {
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    Organization organization = buildOrganization();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.SEND;

    String expectedApiKey = "apiKey";

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    when(organizationKeysServiceMock.getApiKey(organizationId, keyType, subUnitCode))
      .thenReturn(expectedApiKey);

    String result = service.getApiKey(organizationId, keyType, subUnitCode);

    assertEquals(expectedApiKey, result);
  }

  @Test
  void givenGetApiKeySENDWhenSubUnitKeyIsNullThenFallbackToOrganizationKeySuccess() {
    // Given
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    Organization organization = buildOrganization();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.SEND;
    String expectedApiKey = "fallbackApiKey";

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));

    Mockito.when(organizationKeysServiceMock.getApiKey(organizationId, keyType, subUnitCode))
      .thenReturn(null);
    Mockito.when(organizationKeysServiceMock.getApiKey(organizationId, keyType, null))
      .thenReturn(expectedApiKey);

    // When
    String result = service.getApiKey(organizationId, keyType, subUnitCode);

    // Then
    assertEquals(expectedApiKey, result);
  }

  @Test
  void givenGetApiKeySENDWhenBothSpecificAndGeneralKeysAreNullThenReturnNull() {
    // Given
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    Organization organization = buildOrganization();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.SEND;

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));

    Mockito.when(organizationKeysServiceMock.getApiKey(organizationId, keyType, subUnitCode))
      .thenReturn(null);
    Mockito.when(organizationKeysServiceMock.getApiKey(organizationId, keyType, null))
      .thenReturn(null);

    // When
    String result = service.getApiKey(organizationId, keyType, subUnitCode);

    // Then
    assertNull(result);
  }


  @Test
  void givenGetApiKeyWithOrgNotFoundThenThrowException() {
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    OrganizationApiKeyType keyType = OrganizationApiKeyType.SEND;

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.empty());

    OrganizationNotFoundException result = assertThrows(OrganizationNotFoundException.class,
      () -> service.getApiKey(organizationId, keyType, subUnitCode));

    assertEquals("ORGANIZATION_NOT_FOUND",result.getCode());
    assertEquals("Organization with id 1 not found", result.getMessage());
  }

  @Test
  void givenGetApiKeyGenerateNoticeThenSuccess() {
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    Organization organization = buildOrganization();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.GENERATE_NOTICE;

    String expectedApiKey = "apiKey";

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    when(organizationKeysServiceMock.getApiKey(organizationId, keyType, subUnitCode))
      .thenReturn(expectedApiKey);

    String result = service.getApiKey(organizationId, keyType, subUnitCode);

    assertEquals(expectedApiKey, result);
  }

  @Test
  void givenGetApiKeyGenerateNoticeNoOrganizationThenSuccess() {
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    Organization organization = buildOrganization();
    Broker broker = new Broker();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.GENERATE_NOTICE;

    String expectedApiKey = "apiKey";

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    Mockito.when(brokerRepositoryMock.findByBrokeredOrganizationId(String.valueOf(organizationId)))
      .thenReturn(Optional.of(broker));
    Mockito.when(brokerKeysServiceMock.getBrokerDecryptedApiKey(broker.getBrokerId(), BrokerApiKeyType.GENERATE_NOTICE))
      .thenReturn(expectedApiKey);
    when(organizationKeysServiceMock.getApiKey(organizationId, keyType, subUnitCode))
      .thenReturn(null);

    String result = service.getApiKey(organizationId, keyType, subUnitCode);

    assertEquals(expectedApiKey, result);
  }

  @Test
  void givenGetApiKeyGenerateNoticeBrokerNotFoundThenThrowException() {
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    Organization organization = buildOrganization();
    OrganizationApiKeyType keyType = OrganizationApiKeyType.GENERATE_NOTICE;

    Mockito.when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));
    Mockito.when(brokerRepositoryMock.findByBrokeredOrganizationId(String.valueOf(organizationId)))
      .thenReturn(Optional.empty());
    when(organizationKeysServiceMock.getApiKey(organizationId, keyType, subUnitCode))
      .thenReturn(null);

    BrokerNotFoundException result = assertThrows(BrokerNotFoundException.class,
      () -> service.getApiKey(organizationId, keyType, subUnitCode));

    assertEquals("BROKER_NOT_FOUND",result.getCode());
    assertEquals("BROKER_NOT_FOUND",result.getCode());
    assertEquals("Broker for org with id 1 not found", result.getMessage());
  }

  @Test
  void givenExistingOrganizationWhenGetOrganizationThenReturnDTO() {
    Long organizationId = 1L;
    Organization org = new Organization();
    org.setOrganizationId(organizationId);

    OrganizationDetailDTO expectedDto = new OrganizationDetailDTO();
    expectedDto.setOrganizationId(organizationId);

    OrganizationStationDTO organizationStationDTO = new OrganizationStationDTO();
    organizationStationDTO.setSegregationCode("segregationCode");

    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(org));
    when(organizationStationMapperMock.mapToDTO(org, null)).thenReturn(organizationStationDTO);
    when(organizationMapperMock.mapToDTO(org, "segregationCode")).thenReturn(expectedDto);

    OrganizationDetailDTO result = service.getOrganization(organizationId);

    assertNotNull(result);
    assertEquals(expectedDto.getOrganizationId(), result.getOrganizationId());
    verify(organizationRepositoryMock, Mockito.times(2)).findById(organizationId);
    verify(organizationStationMapperMock).mapToDTO(org, null);
    verify(organizationMapperMock).mapToDTO(org, "segregationCode");
  }

  @Test
  void givenNonExistingOrganizationWhenGetOrganizationThenThrowException() {
    Long organizationId = 99L;

    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.empty());

    assertThrows(OrganizationNotFoundException.class, () -> service.getOrganization(organizationId));

    verify(organizationRepositoryMock).findById(organizationId);
  }

  @Test
  void givenExistingOrganizationStationWhenGetOrganizationStationThenReturnDTO() {
    Long organizationId = 1L;
    Organization org = new Organization();
    org.setOrganizationId(organizationId);

    OrganizationStationDTO expectedDto = new OrganizationStationDTO();
    expectedDto.setOrganizationId(organizationId);

    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(org));
    when(organizationStationMapperMock.mapToDTO(org, null)).thenReturn(expectedDto);

    OrganizationStationDTO result = service.getOrganizationStation(organizationId, null);

    assertNotNull(result);
    assertEquals(expectedDto.getOrganizationId(), result.getOrganizationId());
    verify(organizationRepositoryMock).findById(organizationId);
    verify(organizationStationMapperMock).mapToDTO(org, null);
  }

  @Test
  void givenNonExistingOrganizationStationWhenGetOrganizationStationThenThrowException() {
    Long organizationId = 99L;

    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.empty());

    assertThrows(OrganizationNotFoundException.class, () -> service.getOrganizationStation(organizationId, null));

    verify(organizationRepositoryMock).findById(organizationId);
  }

  @Test
  void givenSegregationCodeNullAndStatusActiveWhenUpdateOrganizationThenDoNotClearDefaultStation() {
    Long defaultOrganizationStationId = 1L;

    String accessToken = "accessToken";
    OrganizationDetailDTO organization = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organization.setSegregationCode(null);
    organization.setStatus(OrganizationStatus.ACTIVE);
    organization.setDefaultOrganizationStationId(defaultOrganizationStationId);

    Organization existingOrganization = OrganizationFaker.buildOrganization();

    when(organizationRepositoryMock.findById(organization.getOrganizationId())).thenReturn(Optional.of(existingOrganization));
    when(organizationMapperMock.toModel(organization)).thenReturn(existingOrganization);
    when(organizationRepositoryMock.save(existingOrganization)).thenReturn(existingOrganization);
    doNothing().when(organizationValidatorServiceMock).validateOrganizationDTO(organization, existingOrganization);

    service.updateOrganization(organization, accessToken);

    assertEquals(defaultOrganizationStationId, organization.getDefaultOrganizationStationId());
  }

  @Test
  void givenSegregationCodeNullAndStatusDraftWhenUpdateOrganizationThenClearDefaultOrganizationStationId() {
    String accessToken = "accessToken";
    OrganizationDetailDTO organization = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organization.setSegregationCode(null);
    organization.setStatus(OrganizationStatus.DRAFT);
    organization.setDefaultOrganizationStationId(1L);

    Organization existingOrganization = OrganizationFaker.buildOrganization();
    existingOrganization.setStatus(OrganizationStatus.DRAFT);

    when(organizationRepositoryMock.findById(organization.getOrganizationId())).thenReturn(Optional.of(existingOrganization));
    when(organizationMapperMock.toModel(organization)).thenReturn(existingOrganization);
    when(organizationRepositoryMock.save(existingOrganization)).thenReturn(existingOrganization);
    doNothing().when(organizationValidatorServiceMock).validateOrganizationDTO(organization, existingOrganization);

    service.updateOrganization(organization, accessToken);

    assertNull(organization.getDefaultOrganizationStationId());
  }

  @Test
  void givenSegregationCodeNotNullAndDefaultStationNotNullWhenUpdateOrganizationThenUpdateDefaultOrganizationStationSegregationCode() {
    String accessToken = "accessToken";
    OrganizationDetailDTO organization = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organization.setOrganizationId(11L);
    organization.setSegregationCode("12");
    organization.setBrokerId(1L);
    organization.setDefaultOrganizationStationId(1L);

    Organization existingOrganization = OrganizationFaker.buildOrganization();

    when(organizationRepositoryMock.findById(organization.getOrganizationId())).thenReturn(Optional.of(existingOrganization));
    when(organizationMapperMock.toModel(organization)).thenReturn(existingOrganization);
    when(organizationRepositoryMock.save(existingOrganization)).thenReturn(existingOrganization);
    doNothing().when(defaultOrganizationStationServiceMock)
      .updateDefaultOrganizationStationSegregationCode(
        organization.getDefaultOrganizationStationId(),
        organization.getOrganizationId(),
        organization.getSegregationCode()
      );
    doNothing().when(organizationValidatorServiceMock).validateOrganizationDTO(organization, existingOrganization);

    assertDoesNotThrow(() -> service.updateOrganization(organization, accessToken));
  }

  @Test
  void givenSegregationCodeNotNullAndDefaultStationNullWhenUpdateOrganizationThenCreateStation() {
    String accessToken = "accessToken";
    OrganizationDetailDTO organization = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organization.setOrganizationId(1L);
    organization.setSegregationCode("12");
    organization.setBrokerId(1L);
    organization.setDefaultOrganizationStationId(null);

    Organization existingOrganization = OrganizationFaker.buildOrganization();

    OrganizationStation newStation = new OrganizationStation();
    newStation.setOrganizationStationId(1L);

    when(organizationRepositoryMock.findById(organization.getOrganizationId())).thenReturn(Optional.of(existingOrganization));
    when(defaultOrganizationStationServiceMock
      .createOrUpdateDefaultOrganizationStation(
        organization.getOrganizationId(),
        organization.getBrokerId(),
        organization.getSegregationCode()
      )
    ).thenReturn(newStation);
    when(organizationMapperMock.toModel(organization)).thenReturn(existingOrganization);
    when(organizationRepositoryMock.save(existingOrganization)).thenReturn(existingOrganization);
    doNothing().when(organizationValidatorServiceMock).validateOrganizationDTO(organization, existingOrganization);

    service.updateOrganization(organization, accessToken);

    assertEquals(newStation.getOrganizationStationId(), organization.getDefaultOrganizationStationId());
  }

  @Test
  void givenNonExistingOrganizationWhenUpdateOrganizationThenResourceNotFoundException() {
    String accessToken = "accessToken";
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    when(organizationRepositoryMock.findById(organizationDetailDTO.getOrganizationId())).thenReturn(Optional.empty());

    assertThrows(OrganizationNotFoundException.class,() -> service.updateOrganization(organizationDetailDTO, accessToken));
  }

  @Test
  void givenSegregationCodeWithoutBrokerIdWhenUpdateOrganizationThenThrowException() {
    String accessToken = "accessToken";

    OrganizationDetailDTO dto = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    dto.setSegregationCode("12");
    dto.setBrokerId(null);

    Organization existingOrganization = new Organization();
    existingOrganization.setDefaultOrganizationStationId(1L);

    when(organizationRepositoryMock.findById(dto.getOrganizationId())).thenReturn(Optional.of(existingOrganization));

    InvalidValueException exception = assertThrows(InvalidValueException.class,
      () -> service.updateOrganization(dto, accessToken));

    assertEquals("Broker id is required when segregation code is provided", exception.getMessage());
  }

  @Test
  void givenIbanChangedWhenUpdateOrganizationThenTriggerMassiveUpdate() {
    String accessToken = TestUtils.getFakeAccessToken();
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organizationDetailDTO.setOrgFiscalCode("12345678903");
    organizationDetailDTO.setIban("IT0000000000000000000000000");
    organizationDetailDTO.setPostalIban("IT0000000000000000000000000");
    organizationDetailDTO.setSegregationCode(null);

    Organization existingOrganization = OrganizationFaker.buildOrganization();
    existingOrganization.setIban("IT0000000000000000000000001");
    existingOrganization.setPostalIban("IT0000000000000000000000000");
    existingOrganization.setBrokerId(organizationDetailDTO.getBrokerId());
    existingOrganization.setExternalOrganizationId(organizationDetailDTO.getExternalOrganizationId());
    existingOrganization.setIpaCode(organizationDetailDTO.getIpaCode());
    existingOrganization.setOrgFiscalCode(organizationDetailDTO.getOrgFiscalCode());
    existingOrganization.setOrgName(organizationDetailDTO.getOrgName());
    existingOrganization.setOrgTypeCode(organizationDetailDTO.getOrgTypeCode());

    when(organizationRepositoryMock.findById(organizationDetailDTO.getOrganizationId())).thenReturn(Optional.of(existingOrganization));
    when(organizationMapperMock.toModel(organizationDetailDTO)).thenReturn(existingOrganization);
    when(organizationRepositoryMock.save(existingOrganization)).thenReturn(existingOrganization);

    service.updateOrganization(organizationDetailDTO, accessToken);

    verify(organizationValidatorServiceMock).validateOrganizationDTO(organizationDetailDTO, existingOrganization);
    Mockito.verify(workflowDebtPositionServiceMock).massiveDpIbanUpdate(
      Mockito.eq(existingOrganization.getOrganizationId()),
      Mockito.any(MassiveDebtPositionIbanUpdateRequestDTO.class),
      Mockito.eq(accessToken)
    );
  }

  @Test
  void givenPostalIbanChangedWhenUpdateOrganizationThenTriggerMassiveUpdate() {
    String accessToken = TestUtils.getFakeAccessToken();
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organizationDetailDTO.setOrgFiscalCode("12345678903");
    organizationDetailDTO.setIban("IT0000000000000000000000000");
    organizationDetailDTO.setPostalIban("IT0000000000000000000000000");
    organizationDetailDTO.setSegregationCode(null);

    Organization existingOrganization = OrganizationFaker.buildOrganization();
    existingOrganization.setIban("IT0000000000000000000000000");
    existingOrganization.setPostalIban("IT0000000000000000000000001");
    existingOrganization.setBrokerId(organizationDetailDTO.getBrokerId());
    existingOrganization.setExternalOrganizationId(organizationDetailDTO.getExternalOrganizationId());
    existingOrganization.setIpaCode(organizationDetailDTO.getIpaCode());
    existingOrganization.setOrgFiscalCode(organizationDetailDTO.getOrgFiscalCode());
    existingOrganization.setOrgName(organizationDetailDTO.getOrgName());
    existingOrganization.setOrgTypeCode(organizationDetailDTO.getOrgTypeCode());

    when(organizationRepositoryMock.findById(organizationDetailDTO.getOrganizationId())).thenReturn(Optional.of(existingOrganization));
    when(organizationMapperMock.toModel(organizationDetailDTO)).thenReturn(existingOrganization);
    when(organizationRepositoryMock.save(existingOrganization)).thenReturn(existingOrganization);

    service.updateOrganization(organizationDetailDTO, accessToken);

    verify(organizationValidatorServiceMock).validateOrganizationDTO(organizationDetailDTO, existingOrganization);
    Mockito.verify(workflowDebtPositionServiceMock).massiveDpIbanUpdate(
      Mockito.eq(existingOrganization.getOrganizationId()),
      Mockito.any(MassiveDebtPositionIbanUpdateRequestDTO.class),
      Mockito.eq(accessToken)
    );
  }

  @Test
  void givenNullOldIbanWhenUpdateOrganizationThenDoNotTriggerMassiveUpdate() {
    String accessToken = TestUtils.getFakeAccessToken();
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organizationDetailDTO.setOrgFiscalCode("12345678903");
    organizationDetailDTO.setIban("IT0000000000000000000000000");
    organizationDetailDTO.setPostalIban("IT0000000000000000000000000");
    organizationDetailDTO.setSegregationCode(null);

    Organization existingOrganization = OrganizationFaker.buildOrganization();
    existingOrganization.setIban(null);

    existingOrganization.setBrokerId(organizationDetailDTO.getBrokerId());
    existingOrganization.setExternalOrganizationId(organizationDetailDTO.getExternalOrganizationId());
    existingOrganization.setIpaCode(organizationDetailDTO.getIpaCode());
    existingOrganization.setOrgFiscalCode(organizationDetailDTO.getOrgFiscalCode());
    existingOrganization.setOrgName(organizationDetailDTO.getOrgName());
    existingOrganization.setOrgTypeCode(organizationDetailDTO.getOrgTypeCode());

    when(organizationRepositoryMock.findById(organizationDetailDTO.getOrganizationId())).thenReturn(Optional.of(existingOrganization));
    when(organizationMapperMock.toModel(organizationDetailDTO)).thenReturn(existingOrganization);
    when(organizationRepositoryMock.save(existingOrganization)).thenReturn(existingOrganization);

    service.updateOrganization(organizationDetailDTO, accessToken);

    verify(organizationValidatorServiceMock).validateOrganizationDTO(organizationDetailDTO, existingOrganization);
    Mockito.verifyNoInteractions(workflowDebtPositionServiceMock);
  }

  @Test
  void givenValidRequestWhenUpdateOrganizationStatusThenOk(){
    Long organizationId = 1L;
    OrganizationStatus newStatus = OrganizationStatus.ACTIVE;

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    organization.setStatus(OrganizationStatus.DRAFT);
    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));

    service.updateOrganizationStatus(organizationId, newStatus);

    verify(organizationValidatorServiceMock).validateStatusUpdate(organization);
    Mockito.verify(organizationRepositoryMock).save(Mockito.same(organization));
    Assertions.assertEquals(newStatus, organization.getStatus());
  }

  @Test
  void givenNonExistingOrganizationWhenUpdateOrganizationStatusThenResourceNotFoundException(){
    Long organizationId = 1L;
    OrganizationStatus newStatus = OrganizationStatus.ACTIVE;

    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.empty());

    assertThrows(OrganizationNotFoundException.class,() -> service.updateOrganizationStatus(organizationId, newStatus));
  }

  @Test
  void givenValidRequestWhenUpdateOrganizationExternalIdThenOk(){
    Long organizationId = 1L;
    String organizationExternalId = "ORGEXTID";

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    organization.setExternalOrganizationId("OLDEXTID");
    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.of(organization));

    service.updateOrganizationExternalId(organizationId, organizationExternalId);

    verify(organizationRepositoryMock).save(Mockito.same(organization));
    Assertions.assertEquals(organizationExternalId, organization.getExternalOrganizationId());
  }

  @Test
  void givenNonExistingOrganizationWhenUpdateOrganizationExternalIdThenResourceNotFoundException(){
    Long organizationId = 1L;
    String organizationExternalId = "ORGEXTID";

    when(organizationRepositoryMock.findById(organizationId)).thenReturn(Optional.empty());

    assertThrows(OrganizationNotFoundException.class,() -> service.updateOrganizationExternalId(organizationId, organizationExternalId));
  }
}
