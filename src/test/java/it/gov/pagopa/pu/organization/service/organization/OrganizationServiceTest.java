package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

  @Mock
  private OrganizationEncryptionService organizationEncryptionServiceMock;
  @Mock
  private OrganizationRepository organizationRepositoryMock;

  private OrganizationService service;

  @BeforeEach
  void setUp(){
    service = new OrganizationService(organizationEncryptionServiceMock, organizationRepositoryMock);
  }

  @Test
  void givenEncryptAndSaveApiKeyThenSuccess(){
    // Given
    String plainText = "PLAINTEXT";
    byte[] encryptedKey = new byte[64];
    OrganizationApiKeys organizationApiKeys = new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.IO, plainText);


    Mockito.when(organizationEncryptionServiceMock.encrypt(plainText))
        .thenReturn(encryptedKey);

    Mockito.when(organizationRepositoryMock.existsById(1L)).thenReturn(true);

    // When
    service.encryptAndSaveApiKey(1L, organizationApiKeys);

    // Then
    verify(organizationRepositoryMock).updateApiKeyByType(1L, "IO", encryptedKey);
  }

  @Test
  void givenEncryptAndSaveApiKeyWhenOrganizationNotFoundThenThrowOrganizationNotFoundException(){
    // Given
    String plainText = "PLAINTEXT";
    OrganizationApiKeys organizationApiKeys = new OrganizationApiKeys(OrganizationApiKeys.KeyTypeEnum.IO, plainText);

    Mockito.when(organizationRepositoryMock.existsById(1L)).thenReturn(false);

    // When & Then
    OrganizationNotFoundException exception = assertThrows(OrganizationNotFoundException.class, () ->
      service.encryptAndSaveApiKey(1L, organizationApiKeys));

    assertEquals("Organization with id 1 was not found", exception.getMessage());
  }
}
