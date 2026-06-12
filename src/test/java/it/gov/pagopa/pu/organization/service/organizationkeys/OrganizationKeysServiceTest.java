package it.gov.pagopa.pu.organization.service.organizationkeys;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.model.OrganizationKeys;
import it.gov.pagopa.pu.organization.repository.OrganizationKeysRepository;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationKeysServiceTest {

  @Mock
  private OrganizationEncryptionService organizationEncryptionServiceMock;
  @Mock
  private OrganizationKeysRepository organizationKeysRepositoryMock;

  private OrganizationKeysService service;

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

    OrganizationKeys organizationKeys = new OrganizationKeys();
    organizationKeys.setKeyCipher(encryptedKey);
    organizationKeys.setSubUnitCode(subUnitCode);
    organizationKeys.setOrganizationId(organizationId);
    organizationKeys.setKeyType(organizationApiKeys.getKeyType());

    Mockito.when(organizationEncryptionServiceMock.encrypt(plainText))
      .thenReturn(encryptedKey);

    service.encryptAndSave(organizationId, organizationApiKeys, subUnitCode);

    Mockito.verify(organizationKeysRepositoryMock).save(organizationKeys);
  }


}
