package it.gov.pagopa.pu.organization.service.organization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OrganizationEncryptionServiceTest {

  private static final String VALID_ORGANIZATION_ENCRYPT_PASSWORD = "VALID_PASSWORD";

  private OrganizationEncryptionService service;

  @BeforeEach
  void setUp() {
    service = new OrganizationEncryptionService(VALID_ORGANIZATION_ENCRYPT_PASSWORD);
  }

  @Test
  void givenEncryptThenSuccess(){
    // Given
    String plainText = "PLAINTEXT";

    // When
    byte[] encryptedKey = service.encrypt(plainText);

    // Then
    assertNotNull(encryptedKey);
  }
}
