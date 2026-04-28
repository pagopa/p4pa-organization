package it.gov.pagopa.pu.organization.service.brokerconfiguration;

import it.gov.pagopa.pu.organization.dto.EmailServerConfig;
import it.gov.pagopa.pu.organization.util.AESUtils;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import uk.co.jemos.podam.api.PodamFactory;

import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
class BrokerConfigurationEncryptionServiceTest {
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final String brokerEncryptPassword = "brokerEncryptPassword";
  @Mock
  private ObjectMapper objectMapperMock;
  private BrokerConfigurationEncryptionService brokerConfigurationEncryptionService;

  @BeforeEach
  void setUp() {
    brokerConfigurationEncryptionService = new BrokerConfigurationEncryptionService(brokerEncryptPassword,objectMapperMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(objectMapperMock);
  }

  @Test
  void givenNullEmailServerConfigWhenEncryptEmailServerConfigThenNull() {
    Assertions.assertNull(brokerConfigurationEncryptionService.encryptEmailServerConfig(null));
  }

  @Test
  void whenEncryptEmailServerConfigThenOk() {
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      EmailServerConfig emailServerConfig = podamFactory.manufacturePojo(EmailServerConfig.class);
      String strEmailServerConfig = "emailServerConfig";
      byte[] expectedEmailServerConfig = strEmailServerConfig.getBytes(StandardCharsets.UTF_8);
      Mockito.when(objectMapperMock.writeValueAsString(emailServerConfig)).thenReturn(strEmailServerConfig);
      aesUtilsMock.when(() -> AESUtils.encrypt(brokerEncryptPassword, strEmailServerConfig))
        .thenReturn(expectedEmailServerConfig);

      byte[] result = brokerConfigurationEncryptionService.encryptEmailServerConfig(emailServerConfig);

      Assertions.assertSame(expectedEmailServerConfig, result);
      aesUtilsMock.verify(() -> AESUtils.encrypt(Mockito.eq(brokerEncryptPassword), Mockito.anyString()), Mockito.times(1));
    }
  }

  @Test
  void whenDecryptEmailServerConfigThenOk() {
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      EmailServerConfig expectedResult = podamFactory.manufacturePojo(EmailServerConfig.class);
      String strEmailServerConfig = "emailServerConfig";
      byte[] encryptedEmailServerConfig = strEmailServerConfig.getBytes(StandardCharsets.UTF_8);
      aesUtilsMock.when(() -> AESUtils.decrypt(brokerEncryptPassword, encryptedEmailServerConfig)).thenReturn(strEmailServerConfig);
      Mockito.when(objectMapperMock.readValue(strEmailServerConfig, EmailServerConfig.class)).thenReturn(expectedResult);

      EmailServerConfig result = brokerConfigurationEncryptionService.decryptEmailServerConfig(encryptedEmailServerConfig, 1L);

      Assertions.assertEquals(expectedResult, result);
      aesUtilsMock.verify(() -> AESUtils.decrypt(Mockito.eq(brokerEncryptPassword), Mockito.any(byte[].class)), Mockito.times(1));
    }
  }

  @Test
  void givenNullEmailServerConfigWhenDecryptEmailServerConfigThenNull() {
    Assertions.assertNull(brokerConfigurationEncryptionService.decryptEmailServerConfig(null,null));
  }
}
