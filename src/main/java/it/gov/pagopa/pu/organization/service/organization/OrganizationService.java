package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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

    int updatedRows = switch (organizationApiKeys.getKeyType()) {
      case IO -> organizationRepository.updateIoApiKey(organizationId, encryptedApiKey);
      case SEND -> organizationRepository.updateSendApiKey(organizationId, encryptedApiKey);
    };

    if (updatedRows == 0) {
      throw new OrganizationNotFoundException("Organization with ID %s was not found".formatted(organizationId));
    }
  }

  public OrganizationApiKeys getApiKey(Long organizationId, OrganizationApiKeyType keyType) {
    Organization organization = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new ResourceNotFoundException("Organization [%s]".formatted(organizationId)));

    String apiKey = switch (keyType) {
      case IO -> organization.isFlagNotifyIo() ? organizationEncryptionService.decryptKey(organization.getIoApiKey()) : null;
      case SEND -> organizationEncryptionService.decryptKey(organization.getSendApiKey());
    };

    return OrganizationApiKeys.builder()
      .keyType(OrganizationApiKeys.KeyTypeEnum.valueOf(keyType.getValue()))
      .apiKey(apiKey)
      .build();
  }
}
