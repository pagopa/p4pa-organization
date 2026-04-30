package it.gov.pagopa.pu.organization.service.broker;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.util.AESUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class BrokerEncryptionServiceTest {

  private static final String VALID_BROKER_ENCRYPT_PASSWORD = "VALID_PASSWORD";
  private static final byte[] VALID_ENCRYPTED_SYNC_PASSWORD = new byte[]{1, 2, 3};
  private static final byte[] VALID_ENCRYPTED_ACA_PASSWORD = new byte[]{4, 5, 6};
  private static final byte[] VALID_ENCRYPTED_GPD_PASSWORD = new byte[]{7, 8, 9};
  private static final byte[] VALID_ENCRYPTED_GENERATE_NOTICE_PASSWORD = new byte[]{10, 11, 12};
  private static final byte[] VALID_ENCRYPTED_SYNC_PAYMENTS_REPORTING_PASSWORD = new byte[]{13, 14, 15};
  private static final Long VALID_BROKER_ID = 1L;
  private static final Broker VALID_BROKER = Broker.builder()
    .brokerId(VALID_BROKER_ID)
    .syncPaymentsReportingKey(VALID_ENCRYPTED_SYNC_PAYMENTS_REPORTING_PASSWORD)
    .syncKey(VALID_ENCRYPTED_SYNC_PASSWORD)
    .acaKey(VALID_ENCRYPTED_ACA_PASSWORD)
    .gpdKey(VALID_ENCRYPTED_GPD_PASSWORD)
    .generateNoticeKey(VALID_ENCRYPTED_GENERATE_NOTICE_PASSWORD)
    .build();

  private BrokerEncryptionService brokerEncryptionService;

  @BeforeEach
  void setUp() {
    brokerEncryptionService = new BrokerEncryptionService(VALID_BROKER_ENCRYPT_PASSWORD);
  }

  @Test
  void givenValidBrokerWhenGetBrokerDecryptedApiKeysThenOk() {
    //given
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      Stream.of(VALID_ENCRYPTED_SYNC_PASSWORD, VALID_ENCRYPTED_ACA_PASSWORD, VALID_ENCRYPTED_GPD_PASSWORD, VALID_ENCRYPTED_SYNC_PAYMENTS_REPORTING_PASSWORD).forEach(p ->
        aesUtilsMock.when(() -> AESUtils.decrypt(VALID_BROKER_ENCRYPT_PASSWORD, p))
          .thenReturn(List.of(p).toString())
      );

      //when
      BrokerApiKeys response = brokerEncryptionService.getBrokerDecryptedApiKeys(VALID_BROKER);

      //verify
      Assertions.assertEquals(List.of(VALID_ENCRYPTED_SYNC_PASSWORD).toString(), response.getSyncKey());
      Assertions.assertEquals(List.of(VALID_ENCRYPTED_ACA_PASSWORD).toString(), response.getAcaKey());
      Assertions.assertEquals(List.of(VALID_ENCRYPTED_GPD_PASSWORD).toString(), response.getGpdKey());
      Assertions.assertEquals(List.of(VALID_ENCRYPTED_SYNC_PAYMENTS_REPORTING_PASSWORD).toString(), response.getSyncPaymentsReportingKey());
      aesUtilsMock.verify(() -> AESUtils.decrypt(Mockito.eq(VALID_BROKER_ENCRYPT_PASSWORD), Mockito.any(byte[].class)), Mockito.times(4));
    }
  }

  @Test
  void givenValidBrokerIdWithoutKeyWhenGetBrokerDecryptedApiKeysThenNullKey() {
    //given
    Broker broker = Broker.builder().build();

    //when
    BrokerApiKeys response = brokerEncryptionService.getBrokerDecryptedApiKeys(broker);

    //verify
    Assertions.assertNull(response.getSyncKey());
    Assertions.assertNull(response.getAcaKey());
    Assertions.assertNull(response.getGpdKey());
    Assertions.assertNull(response.getSyncPaymentsReportingKey());
  }

  @Test
  void givenNullApiKeyWhenEncryptKeyThenNull() {
    Assertions.assertNull(brokerEncryptionService.encryptKey(null));
  }

  @Test
  void whenEncryptKeyThenOk() {

    //given
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      String apiKey = "plainKey";
      byte[] expectedEncryptedKey = "cipheredKey".getBytes(StandardCharsets.UTF_8);
      aesUtilsMock.when(() -> AESUtils.encrypt(VALID_BROKER_ENCRYPT_PASSWORD, apiKey))
        .thenReturn(expectedEncryptedKey);

      //when
      byte[] result = brokerEncryptionService.encryptKey(apiKey);

      //verify
      Assertions.assertSame(expectedEncryptedKey, result);
    }
  }

  @Test
  void givenGetApiKeyACAThenSuccess() {
    //given
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(VALID_BROKER_ENCRYPT_PASSWORD, VALID_ENCRYPTED_ACA_PASSWORD)).thenReturn("validKey");

      //when
      String result = brokerEncryptionService.getBrokerDecryptedApiKey(VALID_BROKER, BrokerApiKeyType.ACA);

      //verify
      Assertions.assertEquals("validKey", result);
      aesUtilsMock.verify(() -> AESUtils.decrypt(Mockito.eq(VALID_BROKER_ENCRYPT_PASSWORD), Mockito.any(byte[].class)), Mockito.times(1));
    }
  }

  @Test
  void givenGetApiKeySYNCPaymentsReportingThenSuccess() {
    //given
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(VALID_BROKER_ENCRYPT_PASSWORD, VALID_ENCRYPTED_SYNC_PAYMENTS_REPORTING_PASSWORD)).thenReturn("validKey");

      //when
      String result = brokerEncryptionService.getBrokerDecryptedApiKey(VALID_BROKER, BrokerApiKeyType.SYNC_PAYMENTS_REPORTING);

      //verify
      Assertions.assertEquals("validKey", result);
      aesUtilsMock.verify(() -> AESUtils.decrypt(Mockito.eq(VALID_BROKER_ENCRYPT_PASSWORD), Mockito.any(byte[].class)), Mockito.times(1));
    }
  }

  @Test
  void givenGetApiKeySYNCThenSuccess() {
    //given
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(VALID_BROKER_ENCRYPT_PASSWORD, VALID_ENCRYPTED_SYNC_PASSWORD)).thenReturn("validKey");

      //when
      String result = brokerEncryptionService.getBrokerDecryptedApiKey(VALID_BROKER, BrokerApiKeyType.SYNC);

      //verify
      Assertions.assertEquals("validKey", result);
      aesUtilsMock.verify(() -> AESUtils.decrypt(Mockito.eq(VALID_BROKER_ENCRYPT_PASSWORD), Mockito.any(byte[].class)), Mockito.times(1));
    }
  }

  @Test
  void givenGetApiKeyGPDThenSuccess() {
    //given
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(VALID_BROKER_ENCRYPT_PASSWORD, VALID_ENCRYPTED_GPD_PASSWORD)).thenReturn("validKey");

      //when
      String result = brokerEncryptionService.getBrokerDecryptedApiKey(VALID_BROKER, BrokerApiKeyType.GPD);

      //verify
      Assertions.assertEquals("validKey", result);
      aesUtilsMock.verify(() -> AESUtils.decrypt(Mockito.eq(VALID_BROKER_ENCRYPT_PASSWORD), Mockito.any(byte[].class)), Mockito.times(1));
    }
  }

  @Test
  void givenGetApiKeyNOTICEThenSuccess() {
    //given
    try (MockedStatic<AESUtils> aesUtilsMock = Mockito.mockStatic(AESUtils.class)) {
      aesUtilsMock.when(() -> AESUtils.decrypt(VALID_BROKER_ENCRYPT_PASSWORD, VALID_ENCRYPTED_GENERATE_NOTICE_PASSWORD)).thenReturn("validKey");

      //when
      String result = brokerEncryptionService.getBrokerDecryptedApiKey(VALID_BROKER, BrokerApiKeyType.GENERATE_NOTICE);

      //verify
      Assertions.assertEquals("validKey", result);
      aesUtilsMock.verify(() -> AESUtils.decrypt(Mockito.eq(VALID_BROKER_ENCRYPT_PASSWORD), Mockito.any(byte[].class)), Mockito.times(1));
    }
  }
}
