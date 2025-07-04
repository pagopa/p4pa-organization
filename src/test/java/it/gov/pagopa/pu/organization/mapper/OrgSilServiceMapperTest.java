package it.gov.pagopa.pu.organization.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.enums.JwtAlgorithm;
import it.gov.pagopa.pu.organization.enums.OrgSilServiceType;
import it.gov.pagopa.pu.organization.model.*;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceMapperTest {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private OrganizationEncryptionService encryptionService;

  @InjectMocks
  private OrgSilServiceMapper mapper;

  private OrgSilService testEntity;
  private OrgSilServiceDTO testDTO;
  private SilServiceLegacyBasicAuthConfig basicAuthConfig;
  private SilServiceLegacyBasicAuthConfigDTO basicAuthConfigDTO;
  private SilServiceLegacyJwtAuthConfig jwtAuthConfig;
  private SilServiceLegacyJwtAuthConfigDTO jwtAuthConfigDTO;

  @BeforeEach
  void setUp() {
    // Setup test data
    testEntity = new OrgSilService();
    testEntity.setOrgSilServiceId(1L);
    testEntity.setOrganizationId(100L);
    testEntity.setServiceType(OrgSilServiceType.ACTUALIZATION);
    testEntity.setServiceUrl("https://api.example.com");
    testEntity.setApplicationName("TestApp");
    testEntity.setFlagLegacy(true);

    testDTO = new OrgSilServiceDTO();
    testDTO.setOrgSilServiceId(1L);
    testDTO.setOrganizationId(100L);
    testDTO.setServiceType(OrgSilServiceType.ACTUALIZATION);
    testDTO.setServiceUrl("https://api.example.com");
    testDTO.setApplicationName("TestApp");
    testDTO.setFlagLegacy(true);

    // Setup BasicAuth config
    basicAuthConfig = new SilServiceLegacyBasicAuthConfig();
    basicAuthConfig.setAuthUrl("https://auth.example.com");
    basicAuthConfig.setUser("encryptedUser".getBytes(StandardCharsets.UTF_8));
    basicAuthConfig.setPsw("encryptedPassword".getBytes(StandardCharsets.UTF_8));

    basicAuthConfigDTO = new SilServiceLegacyBasicAuthConfigDTO();
    basicAuthConfigDTO.setAuthUrl("https://auth.example.com");
    basicAuthConfigDTO.setUser("plainUser");
    basicAuthConfigDTO.setPsw("plainPassword");

    // Setup JWT config
    jwtAuthConfig = new SilServiceLegacyJwtAuthConfig();
    jwtAuthConfig.setKid("kid123");
    jwtAuthConfig.setSubject("subject");
    jwtAuthConfig.setIssuer("issuer");
    jwtAuthConfig.setAlgorithm(JwtAlgorithm.HS256);
    jwtAuthConfig.setSigningKey("encryptedSigningKey".getBytes(StandardCharsets.UTF_8));

    jwtAuthConfigDTO = new SilServiceLegacyJwtAuthConfigDTO();
    jwtAuthConfigDTO.setKid("kid123");
    jwtAuthConfigDTO.setSubject("subject");
    jwtAuthConfigDTO.setIssuer("issuer");
    jwtAuthConfigDTO.setAlgorithm(JwtAlgorithm.HS256);
    jwtAuthConfigDTO.setSigningKey("plainSigningKey");
  }

  @Test
  void givenNullEntityWhenFromEntityThenReturnsNull() {
    // When
    OrgSilServiceDTO result = mapper.fromEntity(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenValidEntityWithNoAuthConfigWhenFromEntityThenReturnsCompleteDTO() {
    // Given
    testEntity.setAuthConfig(null);

    // When
    OrgSilServiceDTO result = mapper.fromEntity(testEntity);

    // Then
    assertNotNull(result);
    assertEquals(testEntity.getOrgSilServiceId(), result.getOrgSilServiceId());
    assertEquals(testEntity.getOrganizationId(), result.getOrganizationId());
    assertEquals(testEntity.getServiceType(), result.getServiceType());
    assertEquals(testEntity.getServiceUrl(), result.getServiceUrl());
    assertEquals(testEntity.getApplicationName(), result.getApplicationName());
    assertEquals(testEntity.isFlagLegacy(), result.getFlagLegacy());
    assertNull(result.getAuthConfig());
    TestUtils.checkNotNullFields(result, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
  }

  @Test
  void givenEntityWithBasicAuthConfigWhenFromEntityThenReturnsDTOWithDecryptedValues() {
    // Given
    testEntity.setAuthConfig(basicAuthConfig);
    when(encryptionService.decryptKey("encryptedUser".getBytes(StandardCharsets.UTF_8))).thenReturn("plainUser");
    when(encryptionService.decryptKey("encryptedPassword".getBytes(StandardCharsets.UTF_8))).thenReturn("plainPassword");

    // When
    OrgSilServiceDTO result = mapper.fromEntity(testEntity);

    // Then
    assertNotNull(result);
    assertNotNull(result.getAuthConfig());
    assertTrue(result.getAuthConfig() instanceof SilServiceLegacyBasicAuthConfigDTO);

    SilServiceLegacyBasicAuthConfigDTO authConfig = (SilServiceLegacyBasicAuthConfigDTO) result.getAuthConfig();
    assertEquals("https://auth.example.com", authConfig.getAuthUrl());
    assertEquals("plainUser", authConfig.getUser());
    assertEquals("plainPassword", authConfig.getPsw());

    TestUtils.checkNotNullFields(result);
    TestUtils.checkNotNullFields(authConfig);

    verify(encryptionService).decryptKey("encryptedUser".getBytes(StandardCharsets.UTF_8));
    verify(encryptionService).decryptKey("encryptedPassword".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void givenEntityWithJwtAuthConfigWhenFromEntityThenReturnsDTOWithDecryptedValues() {
    // Given
    testEntity.setAuthConfig(jwtAuthConfig);
    when(encryptionService.decryptKey("encryptedSigningKey".getBytes(StandardCharsets.UTF_8))).thenReturn("plainSigningKey");

    // When
    OrgSilServiceDTO result = mapper.fromEntity(testEntity);

    // Then
    assertNotNull(result);
    assertNotNull(result.getAuthConfig());
    assertTrue(result.getAuthConfig() instanceof SilServiceLegacyJwtAuthConfigDTO);

    SilServiceLegacyJwtAuthConfigDTO authConfig = (SilServiceLegacyJwtAuthConfigDTO) result.getAuthConfig();
    assertEquals("kid123", authConfig.getKid());
    assertEquals("subject", authConfig.getSubject());
    assertEquals("issuer", authConfig.getIssuer());
    assertEquals(JwtAlgorithm.HS256, authConfig.getAlgorithm());
    assertEquals("plainSigningKey", authConfig.getSigningKey());

    TestUtils.checkNotNullFields(result);
    TestUtils.checkNotNullFields(authConfig);

    verify(encryptionService).decryptKey("encryptedSigningKey".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void givenNullDTOWhenFromDTOThenReturnsNull() {
    // When
    OrgSilService result = mapper.fromDTO(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenValidDTOWithNoAuthConfigWhenFromDTOThenReturnsCompleteEntity() {
    // Given
    testDTO.setAuthConfig(null);

    // When
    OrgSilService result = mapper.fromDTO(testDTO);

    // Then
    assertNotNull(result);
    assertEquals(testDTO.getOrgSilServiceId(), result.getOrgSilServiceId());
    assertEquals(testDTO.getOrganizationId(), result.getOrganizationId());
    assertEquals(testDTO.getServiceType(), result.getServiceType());
    assertEquals(testDTO.getServiceUrl(), result.getServiceUrl());
    assertEquals(testDTO.getApplicationName(), result.getApplicationName());
    assertEquals(testDTO.getFlagLegacy(), result.isFlagLegacy());
    assertNull(result.getAuthConfig());
    TestUtils.checkNotNullFields(result, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
  }

  @Test
  void givenDTOWithBasicAuthConfigWhenFromDTOThenReturnsEntityWithEncryptedValues() {
    // Given
    testDTO.setAuthConfig(basicAuthConfigDTO);
    when(encryptionService.encrypt("plainUser")).thenReturn("encryptedUser".getBytes(StandardCharsets.UTF_8));
    when(encryptionService.encrypt("plainPassword")).thenReturn("encryptedPassword".getBytes(StandardCharsets.UTF_8));

    // When
    OrgSilService result = mapper.fromDTO(testDTO);

    // Then
    assertNotNull(result);
    assertNotNull(result.getAuthConfig());
    assertTrue(result.getAuthConfig() instanceof SilServiceLegacyBasicAuthConfig);

    SilServiceLegacyBasicAuthConfig authConfig = (SilServiceLegacyBasicAuthConfig) result.getAuthConfig();
    assertEquals("https://auth.example.com", authConfig.getAuthUrl());
    assertEquals("encryptedUser", new String(authConfig.getUser(), StandardCharsets.UTF_8));
    assertEquals("encryptedPassword", new String(authConfig.getPsw(), StandardCharsets.UTF_8));

    TestUtils.checkNotNullFields(result, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
    TestUtils.checkNotNullFields(authConfig);

    verify(encryptionService).encrypt("plainUser");
    verify(encryptionService).encrypt("plainPassword");
  }

  @Test
  void givenDTOWithJwtAuthConfigWhenFromDTOThenReturnsEntityWithEncryptedValues() {
    // Given
    testDTO.setAuthConfig(jwtAuthConfigDTO);
    when(encryptionService.encrypt("plainSigningKey")).thenReturn("encryptedSigningKey".getBytes(StandardCharsets.UTF_8));

    // When
    OrgSilService result = mapper.fromDTO(testDTO);

    // Then
    assertNotNull(result);
    assertNotNull(result.getAuthConfig());
    assertTrue(result.getAuthConfig() instanceof SilServiceLegacyJwtAuthConfig);

    SilServiceLegacyJwtAuthConfig authConfig = (SilServiceLegacyJwtAuthConfig) result.getAuthConfig();
    assertEquals("kid123", authConfig.getKid());
    assertEquals("subject", authConfig.getSubject());
    assertEquals("issuer", authConfig.getIssuer());
    assertEquals(JwtAlgorithm.HS256, authConfig.getAlgorithm());
    assertEquals("encryptedSigningKey", new String(authConfig.getSigningKey(), StandardCharsets.UTF_8));

    TestUtils.checkNotNullFields(result, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
    TestUtils.checkNotNullFields(authConfig);

    verify(encryptionService).encrypt("plainSigningKey");
  }

  @Test
  void givenDTOWithUnsupportedAuthConfigWhenFromDTOThenReturnsEntityWithNullAuthConfig() {
    // Given
    SilServiceAuthConfigDTO unsupportedAuthConfig = new SilServiceAuthConfigDTO() {
      // Anonymous implementation for testing unsupported type
    };
    testDTO.setAuthConfig(unsupportedAuthConfig);

    // When
    OrgSilService result = mapper.fromDTO(testDTO);

    // Then
    assertNotNull(result);
    assertNull(result.getAuthConfig());
    TestUtils.checkNotNullFields(result, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
    verifyNoInteractions(encryptionService);
  }

  @Test
  void givenEntityWithUnsupportedAuthConfigWhenFromEntityThenReturnsDTOWithNullAuthConfig() {
    // Given
    SilServiceAuthConfig unsupportedAuthConfig = new SilServiceAuthConfig() {
      // Anonymous implementation for testing unsupported type
    };
    testEntity.setAuthConfig(unsupportedAuthConfig);

    // When
    OrgSilServiceDTO result = mapper.fromEntity(testEntity);

    // Then
    assertNotNull(result);
    assertNull(result.getAuthConfig());
    TestUtils.checkNotNullFields(result, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
    verifyNoInteractions(encryptionService);
  }

  @Test
  void givenEntityWithBasicAuthConfigWhenRoundTripConversionThenMaintainsDataIntegrity() {
    // Given
    testEntity.setAuthConfig(basicAuthConfig);
    when(encryptionService.decryptKey("encryptedUser".getBytes(StandardCharsets.UTF_8))).thenReturn("plainUser");
    when(encryptionService.decryptKey("encryptedPassword".getBytes(StandardCharsets.UTF_8))).thenReturn("plainPassword");
    when(encryptionService.encrypt("plainUser")).thenReturn("encryptedUser".getBytes(StandardCharsets.UTF_8));
    when(encryptionService.encrypt("plainPassword")).thenReturn("encryptedPassword".getBytes(StandardCharsets.UTF_8));

    // When
    OrgSilServiceDTO dto = mapper.fromEntity(testEntity);
    OrgSilService convertedEntity = mapper.fromDTO(dto);

    // Then
    assertNotNull(convertedEntity);
    assertEquals(testEntity.getOrgSilServiceId(), convertedEntity.getOrgSilServiceId());
    assertEquals(testEntity.getOrganizationId(), convertedEntity.getOrganizationId());
    assertEquals(testEntity.getServiceType(), convertedEntity.getServiceType());
    assertEquals(testEntity.getServiceUrl(), convertedEntity.getServiceUrl());
    assertEquals(testEntity.getApplicationName(), convertedEntity.getApplicationName());
    assertEquals(testEntity.isFlagLegacy(), convertedEntity.isFlagLegacy());

    assertTrue(convertedEntity.getAuthConfig() instanceof SilServiceLegacyBasicAuthConfig);
    SilServiceLegacyBasicAuthConfig convertedAuthConfig = (SilServiceLegacyBasicAuthConfig) convertedEntity.getAuthConfig();
    assertEquals(basicAuthConfig.getAuthUrl(), convertedAuthConfig.getAuthUrl());
    assertEquals(new String(basicAuthConfig.getUser(), StandardCharsets.UTF_8), new String(convertedAuthConfig.getUser(), StandardCharsets.UTF_8));
    assertEquals(new String(basicAuthConfig.getPsw(), StandardCharsets.UTF_8), new String(convertedAuthConfig.getPsw(), StandardCharsets.UTF_8));

    TestUtils.checkNotNullFields(convertedEntity, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
    TestUtils.checkNotNullFields(convertedAuthConfig);
  }

  @Test
  void givenEntityWithJwtAuthConfigWhenRoundTripConversionThenMaintainsDataIntegrity() {
    // Given
    testEntity.setAuthConfig(jwtAuthConfig);
    when(encryptionService.decryptKey("encryptedSigningKey".getBytes(StandardCharsets.UTF_8))).thenReturn("plainSigningKey");
    when(encryptionService.encrypt("plainSigningKey")).thenReturn("encryptedSigningKey".getBytes(StandardCharsets.UTF_8));

    // When
    OrgSilServiceDTO dto = mapper.fromEntity(testEntity);
    OrgSilService convertedEntity = mapper.fromDTO(dto);

    // Then
    assertNotNull(convertedEntity);
    assertEquals(testEntity.getOrgSilServiceId(), convertedEntity.getOrgSilServiceId());
    assertEquals(testEntity.getOrganizationId(), convertedEntity.getOrganizationId());
    assertEquals(testEntity.getServiceType(), convertedEntity.getServiceType());
    assertEquals(testEntity.getServiceUrl(), convertedEntity.getServiceUrl());
    assertEquals(testEntity.getApplicationName(), convertedEntity.getApplicationName());
    assertEquals(testEntity.isFlagLegacy(), convertedEntity.isFlagLegacy());

    assertTrue(convertedEntity.getAuthConfig() instanceof SilServiceLegacyJwtAuthConfig);
    SilServiceLegacyJwtAuthConfig convertedAuthConfig = (SilServiceLegacyJwtAuthConfig) convertedEntity.getAuthConfig();
    assertEquals(jwtAuthConfig.getKid(), convertedAuthConfig.getKid());
    assertEquals(jwtAuthConfig.getSubject(), convertedAuthConfig.getSubject());
    assertEquals(jwtAuthConfig.getIssuer(), convertedAuthConfig.getIssuer());
    assertEquals(jwtAuthConfig.getAlgorithm(), convertedAuthConfig.getAlgorithm());
    assertEquals(new String(jwtAuthConfig.getSigningKey(), StandardCharsets.UTF_8), new String(convertedAuthConfig.getSigningKey(), StandardCharsets.UTF_8));

    TestUtils.checkNotNullFields(convertedEntity, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
    TestUtils.checkNotNullFields(convertedAuthConfig);
  }

  @Test
  void givenEntityWithNullAuthConfigWhenFromEntityThenReturnsNullAuthConfig() {
    // Given
    testEntity.setAuthConfig(null);

    // When
    OrgSilServiceDTO result = mapper.fromEntity(testEntity);

    // Then
    assertNotNull(result);
    assertNull(result.getAuthConfig());
    TestUtils.checkNotNullFields(result, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
  }

  @Test
  void givenDTOWithNullAuthConfigWhenFromDTOThenReturnsNullAuthConfig() {
    // Given
    testDTO.setAuthConfig(null);

    // When
    OrgSilService result = mapper.fromDTO(testDTO);

    // Then
    assertNotNull(result);
    assertNull(result.getAuthConfig());
    TestUtils.checkNotNullFields(result, "authConfig", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
  }

  @Test
  void givenDTOWithBasicAuthConfigWhenFromDTOThenEncryptionServiceCalledCorrectly() {
    // Given
    testDTO.setAuthConfig(basicAuthConfigDTO);
    when(encryptionService.encrypt(anyString())).thenReturn("encrypted".getBytes(StandardCharsets.UTF_8));

    // When
    mapper.fromDTO(testDTO);

    // Then
    verify(encryptionService, times(2)).encrypt(anyString());
    verify(encryptionService).encrypt("plainUser");
    verify(encryptionService).encrypt("plainPassword");
  }

  @Test
  void givenEntityWithBasicAuthConfigWhenFromEntityThenDecryptionServiceCalledCorrectly() {
    // Given
    testEntity.setAuthConfig(basicAuthConfig);
    when(encryptionService.decryptKey(any())).thenReturn("decrypted");

    // When
    mapper.fromEntity(testEntity);

    // Then
    verify(encryptionService, times(2)).decryptKey(any());
    verify(encryptionService).decryptKey("encryptedUser".getBytes(StandardCharsets.UTF_8));
    verify(encryptionService).decryptKey("encryptedPassword".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void givenNullBasicAuthConfigDTOWhenFromLegacyBasicAuthConfigDTOThenReturnsNull() {
    // Given
    testDTO.setAuthConfig(null);

    // When
    OrgSilService result = mapper.fromDTO(testDTO);

    // Then
    assertNotNull(result);
    assertNull(result.getAuthConfig());
  }

  @Test
  void givenNullJwtAuthConfigDTOWhenFromLegacyJwtAuthConfigDTOThenReturnsNull() {
    // Given
    testDTO.setAuthConfig(null);

    // When
    OrgSilService result = mapper.fromDTO(testDTO);

    // Then
    assertNotNull(result);
    assertNull(result.getAuthConfig());
  }

  @Test
  void givenNullBasicAuthConfigWhenToLegacyBasicAuthConfigDTOThenReturnsNull() {
    // Given
    testEntity.setAuthConfig(null);

    // When
    OrgSilServiceDTO result = mapper.fromEntity(testEntity);

    // Then
    assertNotNull(result);
    assertNull(result.getAuthConfig());
  }

  @Test
  void givenNullJwtAuthConfigWhenToLegacyJwtAuthConfigDTOThenReturnsNull() {
    // Given
    testEntity.setAuthConfig(null);

    // When
    OrgSilServiceDTO result = mapper.fromEntity(testEntity);

    // Then
    assertNotNull(result);
    assertNull(result.getAuthConfig());
  }
}
