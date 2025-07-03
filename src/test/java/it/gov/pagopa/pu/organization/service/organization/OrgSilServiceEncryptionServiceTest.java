package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.model.SilServiceAuthConfig;
import it.gov.pagopa.pu.organization.model.SilServiceLegacyBasicAuthConfig;
import it.gov.pagopa.pu.organization.model.SilServiceLegacyJwtAuthConfig;
import it.gov.pagopa.pu.organization.util.AESUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceEncryptionServiceTest {

  private static final String ENCRYPT_PASSWORD = "testPassword";

  private OrgSilServiceEncryptionService encryptionService;

  @BeforeEach
  void setUp() {
    encryptionService = new OrgSilServiceEncryptionService(ENCRYPT_PASSWORD);
  }

  @Test
  void givenNullAuthConfigWhenEncryptAuthConfigThenReturnsNull() {
    // When
    SilServiceAuthConfig result = encryptionService.encryptAuthConfig(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenBasicAuthConfigWhenEncryptAuthConfigThenReturnsEncryptedBasicAuthConfig() {
    // Given
    SilServiceLegacyBasicAuthConfig basicAuthConfig = new SilServiceLegacyBasicAuthConfig();
    basicAuthConfig.setUser("user".getBytes(StandardCharsets.UTF_8));
    basicAuthConfig.setPsw("password".getBytes(StandardCharsets.UTF_8));

    try (MockedStatic<AESUtils> aesUtilsMock = mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.encrypt(ENCRYPT_PASSWORD, "user"))
        .thenReturn("encryptedUser".getBytes());
      aesUtilsMock.when(() -> AESUtils.encrypt(ENCRYPT_PASSWORD, "password"))
        .thenReturn("encryptedPassword".getBytes());

      // When
      SilServiceAuthConfig result = encryptionService.encryptAuthConfig(basicAuthConfig);

      // Then
      assertInstanceOf(SilServiceLegacyBasicAuthConfig.class, result);
      SilServiceLegacyBasicAuthConfig encryptedConfig = (SilServiceLegacyBasicAuthConfig) result;
      assertArrayEquals("encryptedUser".getBytes(), encryptedConfig.getUser());
      assertArrayEquals("encryptedPassword".getBytes(), encryptedConfig.getPsw());
    }
  }

  @Test
  void givenJwtAuthConfigWhenEncryptAuthConfigThenReturnsEncryptedJwtAuthConfig() {
    // Given
    SilServiceLegacyJwtAuthConfig jwtAuthConfig = new SilServiceLegacyJwtAuthConfig();
    jwtAuthConfig.setSigningKey("signingKey".getBytes(StandardCharsets.UTF_8));

    try (MockedStatic<AESUtils> aesUtilsMock = mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.encrypt(ENCRYPT_PASSWORD, "signingKey"))
        .thenReturn("encryptedSigningKey".getBytes());

      // When
      SilServiceAuthConfig result = encryptionService.encryptAuthConfig(jwtAuthConfig);

      // Then
      assertInstanceOf(SilServiceLegacyJwtAuthConfig.class, result);
      SilServiceLegacyJwtAuthConfig encryptedConfig = (SilServiceLegacyJwtAuthConfig) result;
      assertArrayEquals("encryptedSigningKey".getBytes(), encryptedConfig.getSigningKey());
    }
  }

  @Test
  void givenNullAuthConfigWhenDecryptAuthConfigThenReturnsNull() {
    // When
    SilServiceAuthConfig result = encryptionService.decryptAuthConfig(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenBasicAuthConfigWhenDecryptAuthConfigThenReturnsDecryptedBasicAuthConfig() {
    // Given
    SilServiceLegacyBasicAuthConfig basicAuthConfig = new SilServiceLegacyBasicAuthConfig();
    basicAuthConfig.setUser("encryptedUser".getBytes());
    basicAuthConfig.setPsw("encryptedPassword".getBytes());

    try (MockedStatic<AESUtils> aesUtilsMock = mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(ENCRYPT_PASSWORD, "encryptedUser".getBytes()))
        .thenReturn("user");
      aesUtilsMock.when(() -> AESUtils.decrypt(ENCRYPT_PASSWORD, "encryptedPassword".getBytes()))
        .thenReturn("password");

      // When
      SilServiceAuthConfig result = encryptionService.decryptAuthConfig(basicAuthConfig);

      // Then
      assertInstanceOf(SilServiceLegacyBasicAuthConfig.class, result);
      SilServiceLegacyBasicAuthConfig decryptedConfig = (SilServiceLegacyBasicAuthConfig) result;
      assertArrayEquals("user".getBytes(StandardCharsets.UTF_8), decryptedConfig.getUser());
      assertArrayEquals("password".getBytes(StandardCharsets.UTF_8), decryptedConfig.getPsw());
    }
  }

  @Test
  void givenJwtAuthConfigWhenDecryptAuthConfigThenReturnsDecryptedJwtAuthConfig() {
    // Given
    SilServiceLegacyJwtAuthConfig jwtAuthConfig = new SilServiceLegacyJwtAuthConfig();
    jwtAuthConfig.setSigningKey("encryptedSigningKey".getBytes());

    try (MockedStatic<AESUtils> aesUtilsMock = mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(ENCRYPT_PASSWORD, "encryptedSigningKey".getBytes()))
        .thenReturn("signingKey");

      // When
      SilServiceAuthConfig result = encryptionService.decryptAuthConfig(jwtAuthConfig);

      // Then
      assertInstanceOf(SilServiceLegacyJwtAuthConfig.class, result);
      SilServiceLegacyJwtAuthConfig decryptedConfig = (SilServiceLegacyJwtAuthConfig) result;
      assertArrayEquals("signingKey".getBytes(StandardCharsets.UTF_8), decryptedConfig.getSigningKey());
    }
  }

  @Test
  void givenNullJwtConfigWhenEncryptLegacyJwtAuthConfigThenReturnsNull() {
    // When
    SilServiceLegacyJwtAuthConfig result = encryptionService.encryptLegacyJwtAuthConfig(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenValidJwtConfigWhenEncryptLegacyJwtAuthConfigThenReturnsEncryptedConfig() {
    // Given
    SilServiceLegacyJwtAuthConfig jwtAuthConfig = new SilServiceLegacyJwtAuthConfig();
    jwtAuthConfig.setSigningKey("signingKey".getBytes(StandardCharsets.UTF_8));

    try (MockedStatic<AESUtils> aesUtilsMock = mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.encrypt(ENCRYPT_PASSWORD, "signingKey"))
        .thenReturn("encryptedSigningKey".getBytes());

      // When
      SilServiceLegacyJwtAuthConfig result = encryptionService.encryptLegacyJwtAuthConfig(jwtAuthConfig);

      // Then
      assertSame(jwtAuthConfig, result);
      assertArrayEquals("encryptedSigningKey".getBytes(), result.getSigningKey());
    }
  }

  @Test
  void givenNullJwtConfigWhenDecryptLegacyJwtAuthConfigThenReturnsNull() {
    // When
    SilServiceLegacyJwtAuthConfig result = encryptionService.decryptLegacyJwtAuthConfig(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenValidJwtConfigWhenDecryptLegacyJwtAuthConfigThenReturnsDecryptedConfig() {
    // Given
    SilServiceLegacyJwtAuthConfig jwtAuthConfig = new SilServiceLegacyJwtAuthConfig();
    jwtAuthConfig.setSigningKey("encryptedSigningKey".getBytes());

    try (MockedStatic<AESUtils> aesUtilsMock = mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(ENCRYPT_PASSWORD, "encryptedSigningKey".getBytes()))
        .thenReturn("signingKey");

      // When
      SilServiceLegacyJwtAuthConfig result = encryptionService.decryptLegacyJwtAuthConfig(jwtAuthConfig);

      // Then
      assertSame(jwtAuthConfig, result);
      assertArrayEquals("signingKey".getBytes(StandardCharsets.UTF_8), result.getSigningKey());
    }
  }

  @Test
  void givenNullBasicConfigWhenEncryptLegacyBasicAuthConfigThenReturnsNull() {
    // When
    SilServiceLegacyBasicAuthConfig result = encryptionService.encryptLegacyBasicAuthConfig(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenValidBasicConfigWhenEncryptLegacyBasicAuthConfigThenReturnsEncryptedConfig() {
    // Given
    SilServiceLegacyBasicAuthConfig basicAuthConfig = new SilServiceLegacyBasicAuthConfig();
    basicAuthConfig.setUser("user".getBytes(StandardCharsets.UTF_8));
    basicAuthConfig.setPsw("password".getBytes(StandardCharsets.UTF_8));

    try (MockedStatic<AESUtils> aesUtilsMock = mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.encrypt(ENCRYPT_PASSWORD, "user"))
        .thenReturn("encryptedUser".getBytes());
      aesUtilsMock.when(() -> AESUtils.encrypt(ENCRYPT_PASSWORD, "password"))
        .thenReturn("encryptedPassword".getBytes());

      // When
      SilServiceLegacyBasicAuthConfig result = encryptionService.encryptLegacyBasicAuthConfig(basicAuthConfig);

      // Then
      assertSame(basicAuthConfig, result);
      assertArrayEquals("encryptedUser".getBytes(), result.getUser());
      assertArrayEquals("encryptedPassword".getBytes(), result.getPsw());
    }
  }

  @Test
  void givenNullBasicConfigWhenDecryptLegacyBasicAuthConfigThenReturnsNull() {
    // When
    SilServiceLegacyBasicAuthConfig result = encryptionService.decryptLegacyBasicAuthConfig(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenValidBasicConfigWhenDecryptLegacyBasicAuthConfigThenReturnsDecryptedConfig() {
    // Given
    SilServiceLegacyBasicAuthConfig basicAuthConfig = new SilServiceLegacyBasicAuthConfig();
    basicAuthConfig.setUser("encryptedUser".getBytes());
    basicAuthConfig.setPsw("encryptedPassword".getBytes());

    try (MockedStatic<AESUtils> aesUtilsMock = mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(ENCRYPT_PASSWORD, "encryptedUser".getBytes()))
        .thenReturn("user");
      aesUtilsMock.when(() -> AESUtils.decrypt(ENCRYPT_PASSWORD, "encryptedPassword".getBytes()))
        .thenReturn("password");

      // When
      SilServiceLegacyBasicAuthConfig result = encryptionService.decryptLegacyBasicAuthConfig(basicAuthConfig);

      // Then
      assertSame(basicAuthConfig, result);
      assertArrayEquals("user".getBytes(StandardCharsets.UTF_8), result.getUser());
      assertArrayEquals("password".getBytes(StandardCharsets.UTF_8), result.getPsw());
    }
  }

  @Test
  void givenCompleteBasicConfigWhenRoundTripEncryptionThenDataIsPreserved() {
    // Given
    SilServiceLegacyBasicAuthConfig originalConfig = new SilServiceLegacyBasicAuthConfig();
    originalConfig.setUser("testUser".getBytes(StandardCharsets.UTF_8));
    originalConfig.setPsw("testPassword".getBytes(StandardCharsets.UTF_8));

    try (MockedStatic<AESUtils> aesUtilsMock = mockStatic(AESUtils.class)) {
      // Mock encryption
      aesUtilsMock.when(() -> AESUtils.encrypt(ENCRYPT_PASSWORD, "testUser"))
        .thenReturn("encryptedUser".getBytes());
      aesUtilsMock.when(() -> AESUtils.encrypt(ENCRYPT_PASSWORD, "testPassword"))
        .thenReturn("encryptedPassword".getBytes());

      // Mock decryption
      aesUtilsMock.when(() -> AESUtils.decrypt(ENCRYPT_PASSWORD, "encryptedUser".getBytes()))
        .thenReturn("testUser");
      aesUtilsMock.when(() -> AESUtils.decrypt(ENCRYPT_PASSWORD, "encryptedPassword".getBytes()))
        .thenReturn("testPassword");

      // When
      SilServiceLegacyBasicAuthConfig encrypted = encryptionService.encryptLegacyBasicAuthConfig(originalConfig);
      SilServiceLegacyBasicAuthConfig decrypted = encryptionService.decryptLegacyBasicAuthConfig(encrypted);

      // Then
      assertArrayEquals("testUser".getBytes(StandardCharsets.UTF_8), decrypted.getUser());
      assertArrayEquals("testPassword".getBytes(StandardCharsets.UTF_8), decrypted.getPsw());
    }
  }
}
