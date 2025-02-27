package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

  private final OrganizationEncryptionService organizationEncryptionService;
  private final OrganizationRepository organizationRepository;

  public OrganizationService(OrganizationEncryptionService organizationEncryptionService, OrganizationRepository organizationRepository) {
    this.organizationEncryptionService = organizationEncryptionService;
    this.organizationRepository = organizationRepository;
  }

  @Transactional
  public void encryptAndSaveApiKey(Long organizationId, OrganizationApiKeys organizationApiKeys) {
    byte[] encryptedApiKey = organizationEncryptionService.encrypt(organizationApiKeys.getApiKey());

    int updatedRows;
    switch (organizationApiKeys.getKeyType()) {
      case IO -> updatedRows = organizationRepository.updateIoApiKey(organizationId, encryptedApiKey);
      case SEND -> updatedRows = organizationRepository.updateSendApiKey(organizationId, encryptedApiKey);
      default -> throw new IllegalArgumentException("Unsupported API key type: " + organizationApiKeys.getKeyType());
    }

    if (updatedRows == 0) {
      throw new OrganizationNotFoundException("Organization with ID %s was not found".formatted(organizationId));
    }
  }

}
