package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.util.AESUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdndClientEncryptionServiceTest {

  private static final String VALID_PDND_CLIENT_ENCRYPT_PASSWORD = "VALID_PASSWORD";

  private PdndClientEncryptionService service;

  @BeforeEach
  void setUp() {
    service = new PdndClientEncryptionService(VALID_PDND_CLIENT_ENCRYPT_PASSWORD);
  }

  @Test
  void givenEncryptThenSuccess() {
    // Given
    String plainText = "PLAINTEXT";

    // When
    byte[] encryptedKey = service.encrypt(plainText);

    // Then
    assertNotNull(encryptedKey);
  }

  @Test
  void givenDecryptThenSuccess() {
    byte[] encryptedKey = new byte[]{1, 2, 3};
    String apiKeyExpected = "apiKey";

    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(VALID_PDND_CLIENT_ENCRYPT_PASSWORD, encryptedKey))
        .thenReturn(apiKeyExpected);

      String result = service.decryptKey(encryptedKey);
      assertEquals(apiKeyExpected, result);
    }
  }

  @Test
  void givenDecryptEmptyKeyThenSuccess() {
    byte[] encryptedKey = new byte[]{};

    String result = service.decryptKey(encryptedKey);
    assertNull(result);
  }

  @Test
  void givenDecryptNullKeyThenSuccess() {
    String result = service.decryptKey(null);
    assertNull(result);
  }
}
