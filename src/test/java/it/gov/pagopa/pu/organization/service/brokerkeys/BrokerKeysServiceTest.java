package it.gov.pagopa.pu.organization.service.brokerkeys;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.model.BrokerKeys;
import it.gov.pagopa.pu.organization.repository.BrokerKeysRepository;
import it.gov.pagopa.pu.organization.service.broker.BrokerEncryptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BrokerKeysServiceTest {

  @Mock
  private BrokerEncryptionService brokerEncryptionServiceMock;

  @Mock
  private BrokerKeysRepository brokerKeysRepositoryMock;

  private BrokerKeysService brokerKeysService;

  private static final Long BROKER_ID = 1L;

  @BeforeEach
  void setUp() {
    brokerKeysService = new BrokerKeysService(brokerEncryptionServiceMock, brokerKeysRepositoryMock);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(brokerEncryptionServiceMock, brokerKeysRepositoryMock);
  }

  @Test
  void whenGetBrokerDecryptedApiKeysWithAllKeysPresentThenOk() {
    //given
    BrokerKeys syncPaymentsReportingKey = buildBrokerKey(BrokerApiKeyType.SYNC_PAYMENTS_REPORTING, "cipherSyncPaymentsReporting");
    BrokerKeys syncKey = buildBrokerKey(BrokerApiKeyType.SYNC, "cipherSync");
    BrokerKeys acaKey = buildBrokerKey(BrokerApiKeyType.ACA, "cipherAca");
    BrokerKeys gpdKey = buildBrokerKey(BrokerApiKeyType.GPD, "cipherGpd");
    BrokerKeys generateNoticeKey = buildBrokerKey(BrokerApiKeyType.GENERATE_NOTICE, "cipherGenerateNotice");

    Mockito.when(brokerKeysRepositoryMock.findByBrokerId(BROKER_ID))
      .thenReturn(List.of(syncPaymentsReportingKey, syncKey, acaKey, gpdKey, generateNoticeKey));

    Mockito.when(brokerEncryptionServiceMock.decryptKey(syncPaymentsReportingKey.getKeyCipher(), BrokerApiKeyType.SYNC_PAYMENTS_REPORTING, BROKER_ID))
      .thenReturn("plainSyncPaymentsReporting");
    Mockito.when(brokerEncryptionServiceMock.decryptKey(syncKey.getKeyCipher(), BrokerApiKeyType.SYNC, BROKER_ID))
      .thenReturn("plainSync");
    Mockito.when(brokerEncryptionServiceMock.decryptKey(acaKey.getKeyCipher(), BrokerApiKeyType.ACA, BROKER_ID))
      .thenReturn("plainAca");
    Mockito.when(brokerEncryptionServiceMock.decryptKey(gpdKey.getKeyCipher(), BrokerApiKeyType.GPD, BROKER_ID))
      .thenReturn("plainGpd");
    Mockito.when(brokerEncryptionServiceMock.decryptKey(generateNoticeKey.getKeyCipher(), BrokerApiKeyType.GENERATE_NOTICE, BROKER_ID))
      .thenReturn("plainGenerateNotice");

    //when
    BrokerApiKeys result = brokerKeysService.getBrokerDecryptedApiKeys(BROKER_ID);

    //verify
    Assertions.assertEquals("plainSyncPaymentsReporting", result.getSyncPaymentsReportingKey());
    Assertions.assertEquals("plainSync", result.getSyncKey());
    Assertions.assertEquals("plainAca", result.getAcaKey());
    Assertions.assertEquals("plainGpd", result.getGpdKey());
    Assertions.assertEquals("plainGenerateNotice", result.getGenerateNoticeKey());
  }

  @Test
  void whenGetBrokerDecryptedApiKeysWithNoKeysFoundThenAllFieldsNull() {
    //given
    Mockito.when(brokerKeysRepositoryMock.findByBrokerId(BROKER_ID))
      .thenReturn(List.of());

    Mockito.when(brokerEncryptionServiceMock.decryptKey(null, BrokerApiKeyType.SYNC_PAYMENTS_REPORTING, BROKER_ID))
      .thenReturn(null);
    Mockito.when(brokerEncryptionServiceMock.decryptKey(null, BrokerApiKeyType.SYNC, BROKER_ID))
      .thenReturn(null);
    Mockito.when(brokerEncryptionServiceMock.decryptKey(null, BrokerApiKeyType.ACA, BROKER_ID))
      .thenReturn(null);
    Mockito.when(brokerEncryptionServiceMock.decryptKey(null, BrokerApiKeyType.GPD, BROKER_ID))
      .thenReturn(null);
    Mockito.when(brokerEncryptionServiceMock.decryptKey(null, BrokerApiKeyType.GENERATE_NOTICE, BROKER_ID))
      .thenReturn(null);

    //when
    BrokerApiKeys result = brokerKeysService.getBrokerDecryptedApiKeys(BROKER_ID);

    //verify
    Assertions.assertNull(result.getSyncPaymentsReportingKey());
    Assertions.assertNull(result.getSyncKey());
    Assertions.assertNull(result.getAcaKey());
    Assertions.assertNull(result.getGpdKey());
    Assertions.assertNull(result.getGenerateNoticeKey());
  }

  @Test
  void whenGetBrokerDecryptedApiKeysWithPartialKeysThenOnlyFoundOnesDecrypted() {
    //given
    BrokerKeys syncKey = buildBrokerKey(BrokerApiKeyType.SYNC, "cipherSync");

    Mockito.when(brokerKeysRepositoryMock.findByBrokerId(BROKER_ID))
      .thenReturn(List.of(syncKey));

    Mockito.when(brokerEncryptionServiceMock.decryptKey(syncKey.getKeyCipher(), BrokerApiKeyType.SYNC, BROKER_ID))
      .thenReturn("plainSync");
    Mockito.when(brokerEncryptionServiceMock.decryptKey(null, BrokerApiKeyType.SYNC_PAYMENTS_REPORTING, BROKER_ID))
      .thenReturn(null);
    Mockito.when(brokerEncryptionServiceMock.decryptKey(null, BrokerApiKeyType.ACA, BROKER_ID))
      .thenReturn(null);
    Mockito.when(brokerEncryptionServiceMock.decryptKey(null, BrokerApiKeyType.GPD, BROKER_ID))
      .thenReturn(null);
    Mockito.when(brokerEncryptionServiceMock.decryptKey(null, BrokerApiKeyType.GENERATE_NOTICE, BROKER_ID))
      .thenReturn(null);

    //when
    BrokerApiKeys result = brokerKeysService.getBrokerDecryptedApiKeys(BROKER_ID);

    //verify
    Assertions.assertEquals("plainSync", result.getSyncKey());
    Assertions.assertNull(result.getSyncPaymentsReportingKey());
    Assertions.assertNull(result.getAcaKey());
    Assertions.assertNull(result.getGpdKey());
    Assertions.assertNull(result.getGenerateNoticeKey());
  }

  @ParameterizedTest
  @EnumSource(BrokerApiKeyType.class)
  void whenGetBrokerDecryptedApiKeyThenOk(BrokerApiKeyType keyType) {
    //given
    BrokerKeys brokerKey = buildBrokerKey(keyType, "cipherValue");
    String expectedDecryptedKey = "plainValue";

    Mockito.when(brokerKeysRepositoryMock.findById(BrokerKeys.buildSemanticId(BROKER_ID, keyType)))
      .thenReturn(Optional.of(brokerKey));
    Mockito.when(brokerEncryptionServiceMock.decryptKey(brokerKey.getKeyCipher(), keyType, BROKER_ID))
      .thenReturn(expectedDecryptedKey);

    //when
    String result = brokerKeysService.getBrokerDecryptedApiKey(BROKER_ID, keyType);

    //verify
    Assertions.assertEquals(expectedDecryptedKey, result);
  }

  @ParameterizedTest
  @EnumSource(BrokerApiKeyType.class)
  void whenGetBrokerDecryptedApiKeyNotFoundThenThrowBrokerNotFoundException(BrokerApiKeyType keyType) {
    //given
    Mockito.when(brokerKeysRepositoryMock.findById(BrokerKeys.buildSemanticId(BROKER_ID, keyType)))
      .thenReturn(Optional.empty());

    //when
    //verify
    Assertions.assertThrows(BrokerNotFoundException.class,
      () -> brokerKeysService.getBrokerDecryptedApiKey(BROKER_ID, keyType));

    Mockito.verifyNoInteractions(brokerEncryptionServiceMock);
  }

  private BrokerKeys buildBrokerKey(BrokerApiKeyType keyType, String cipherValue) {
    BrokerKeys brokerKey = new BrokerKeys();
    brokerKey.setKeyType(keyType);
    brokerKey.setKeyCipher(cipherValue.getBytes(StandardCharsets.UTF_8));
    return brokerKey;
  }
}
