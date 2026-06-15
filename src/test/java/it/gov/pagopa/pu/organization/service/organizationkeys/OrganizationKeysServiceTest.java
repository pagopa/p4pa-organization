package it.gov.pagopa.pu.organization.service.organizationkeys;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.model.OrganizationKeys;
import it.gov.pagopa.pu.organization.repository.OrganizationKeysRepository;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationKeysServiceTest {

  @Mock
  private OrganizationEncryptionService organizationEncryptionServiceMock;
  @Mock
  private OrganizationKeysRepository organizationKeysRepositoryMock;

  private OrganizationKeysService service;

  private static final Long ORG_ID = 1L;
  private static final String SUB_UNIT = "SUB_01";
  private static final OrganizationApiKeyType KEY_TYPE = OrganizationApiKeyType.IO;
  private static final String EXPECTED_DECRYPTED_KEY = "decryptedApiKey";
  private static final byte[] MOCK_CIPHER = "encryptedData".getBytes();

  @BeforeEach
  void setUp() {
    service = new OrganizationKeysService(organizationEncryptionServiceMock,
      organizationKeysRepositoryMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      organizationEncryptionServiceMock,
      organizationKeysRepositoryMock
    );
  }

  @Test
  void givenOrganizationApiKeysWhenEncryptAndSaveThenVerify(){
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    String plainText = "PLAINTEXT";
    byte[] encryptedKey = new byte[64];
    OrganizationApiKeys organizationApiKeys = new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.IO, plainText);

    OrganizationKeys expectedOrganizationKeys = new OrganizationKeys();
    expectedOrganizationKeys.setKeyCipher(encryptedKey);
    expectedOrganizationKeys.setSubUnitCode(subUnitCode);
    expectedOrganizationKeys.setOrganizationId(organizationId);
    expectedOrganizationKeys.setKeyType(OrganizationApiKeyType.fromValue(organizationApiKeys.getKeyType().getValue()));

    when(organizationEncryptionServiceMock.encrypt(plainText))
      .thenReturn(encryptedKey);

    service.encryptAndSave(organizationId, organizationApiKeys, subUnitCode);


    verify(organizationKeysRepositoryMock).save(Mockito.argThat(argument -> {
      TestUtils.checkNotNullFields(argument, "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
      TestUtils.reflectionEqualsByName(expectedOrganizationKeys, argument);
      return true;
    }));
  }

  @Test
  void givenSpecificKeyExistsThenReturnDecryptedKey() {
    String specificId = OrganizationKeys.buildSemanticId(ORG_ID, SUB_UNIT, KEY_TYPE);

    OrganizationKeys mockKey = new OrganizationKeys();
    mockKey.setKeyCipher(MOCK_CIPHER);

    when(organizationKeysRepositoryMock.findById(specificId)).thenReturn(Optional.of(mockKey));
    when(organizationEncryptionServiceMock.decryptKey(MOCK_CIPHER)).thenReturn(EXPECTED_DECRYPTED_KEY);

    String result = service.getApiKey(ORG_ID, KEY_TYPE, SUB_UNIT);

    assertEquals(EXPECTED_DECRYPTED_KEY, result);
    verify(organizationKeysRepositoryMock, times(1)).findById(specificId);
  }

  @Test
  void givenSpecificKeyNotFoundButFallbackExistsThenReturnFallbackDecryptedKey() {
    String specificId = OrganizationKeys.buildSemanticId(ORG_ID, SUB_UNIT, KEY_TYPE);
    String fallbackId = OrganizationKeys.buildSemanticId(ORG_ID, null, KEY_TYPE);

    OrganizationKeys fallbackKey = new OrganizationKeys();
    fallbackKey.setKeyCipher(MOCK_CIPHER);

    when(organizationKeysRepositoryMock.findById(specificId)).thenReturn(Optional.empty());
    when(organizationKeysRepositoryMock.findById(fallbackId)).thenReturn(Optional.of(fallbackKey));
    when(organizationEncryptionServiceMock.decryptKey(MOCK_CIPHER)).thenReturn(EXPECTED_DECRYPTED_KEY);

    String result = service.getApiKey(ORG_ID, KEY_TYPE, SUB_UNIT);

    assertEquals(EXPECTED_DECRYPTED_KEY, result);
    verify(organizationKeysRepositoryMock).findById(specificId);
    verify(organizationKeysRepositoryMock).findById(fallbackId);
  }

  @Test
  void givenNoKeysExistThenReturnNull() {
    String specificId = OrganizationKeys.buildSemanticId(ORG_ID, SUB_UNIT, KEY_TYPE);
    String fallbackId = OrganizationKeys.buildSemanticId(ORG_ID, null, KEY_TYPE);

    when(organizationKeysRepositoryMock.findById(specificId)).thenReturn(Optional.empty());
    when(organizationKeysRepositoryMock.findById(fallbackId)).thenReturn(Optional.empty());
    when(organizationEncryptionServiceMock.decryptKey(null)).thenReturn(null);

    String result = service.getApiKey(ORG_ID, KEY_TYPE, SUB_UNIT);

    assertNull(result);
  }

  @Test
  void givenSubUnitCodeIsNullAndKeyNotFoundThenDoNotTriggerFallback() {
    String specificId = OrganizationKeys.buildSemanticId(ORG_ID, null, KEY_TYPE);

    when(organizationKeysRepositoryMock.findById(specificId)).thenReturn(Optional.empty());
    when(organizationEncryptionServiceMock.decryptKey(null)).thenReturn(null);

    String result = service.getApiKey(ORG_ID, KEY_TYPE, null);

    assertNull(result);
    verify(organizationKeysRepositoryMock, times(1)).findById(anyString());
  }


}
