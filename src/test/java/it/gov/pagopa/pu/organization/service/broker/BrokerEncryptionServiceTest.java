package it.gov.pagopa.pu.organization.service.broker;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.util.AESUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
class BrokerEncryptionServiceTest {

  private static final String VALID_BROKER_PASSWORD = "VALID_PASSWORD";

  private BrokerEncryptionService brokerEncryptionService;

  @BeforeEach
  void setUp() {
    brokerEncryptionService = new BrokerEncryptionService(VALID_BROKER_PASSWORD);
  }

  @Test
  void whenEncryptKeyThenOk() {
    //given
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      String apiKey = "plainKey";
      byte[] expectedEncryptedKey = "cipheredKey".getBytes(StandardCharsets.UTF_8);
      aesUtilsMock.when(() -> AESUtils.encrypt(VALID_BROKER_PASSWORD, apiKey))
        .thenReturn(expectedEncryptedKey);

      //when
      byte[] result = brokerEncryptionService.encryptKey(apiKey);

      //verify
      Assertions.assertSame(expectedEncryptedKey, result);
    }
  }

  @Test
  void whenEncryptKeyWithNullApiKeyThenReturnNull() {
    //when
    byte[] result = brokerEncryptionService.encryptKey(null);

    //verify
    Assertions.assertNull(result);
  }

  @Test
  void whenEncryptKeyWithEmptyApiKeyThenReturnNull() {
    //when
    byte[] result = brokerEncryptionService.encryptKey("");

    //verify
    Assertions.assertNull(result);
  }

  @Test
  void whenDecryptKeyThenOk() {
    //given
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      byte[] encryptedKey = "cipheredKey".getBytes(StandardCharsets.UTF_8);
      String expectedDecryptedKey = "plainKey";
      aesUtilsMock.when(() -> AESUtils.decrypt(VALID_BROKER_PASSWORD, encryptedKey))
        .thenReturn(expectedDecryptedKey);

      //when
      String result = brokerEncryptionService.decryptKey(encryptedKey, BrokerApiKeyType.SYNC, 1L);

      //verify
      Assertions.assertSame(expectedDecryptedKey, result);
    }
  }

  @Test
  void whenDecryptKeyWithNullEncryptedKeyThenReturnNull() {
    //when
    String result = brokerEncryptionService.decryptKey(null, BrokerApiKeyType.SYNC, 1L);

    //verify
    Assertions.assertNull(result);
  }

  @Test
  void whenDecryptKeyWithEmptyEncryptedKeyThenReturnNull() {
    //when
    String result = brokerEncryptionService.decryptKey(new byte[0], BrokerApiKeyType.SYNC, 1L);

    //verify
    Assertions.assertNull(result);
  }
}
