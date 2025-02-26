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
  public void encryptAndSaveApiKey(Long organizationId, OrganizationApiKeys organizationApiKeys){
//    if (!organizationRepository.existsById(organizationId)) {
//      throw new OrganizationNotFoundException(
//        "Organization with id %s was not found".formatted(organizationId)
//      );
//    }

    byte[] encryptedApiKey = organizationEncryptionService.encrypt(organizationApiKeys.getApiKey());

    organizationRepository.updateApiKeyByType(organizationId, organizationApiKeys.getKeyType().name(), encryptedApiKey);
  }
}
