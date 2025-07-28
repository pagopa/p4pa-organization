package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

  private final OrganizationEncryptionService organizationEncryptionService;
  private final OrganizationRepository organizationRepository;
  private final BrokerRepository brokerRepository;

  public OrganizationService(OrganizationEncryptionService organizationEncryptionService, OrganizationRepository organizationRepository, BrokerRepository brokerRepository) {
    this.organizationEncryptionService = organizationEncryptionService;
    this.organizationRepository = organizationRepository;
    this.brokerRepository = brokerRepository;
  }

  @Transactional
  public void encryptAndSaveApiKey(Long organizationId, OrganizationApiKeys organizationApiKeys) {
    byte[] encryptedApiKey = organizationEncryptionService.encrypt(organizationApiKeys.getApiKey());

    int updatedRows = switch (organizationApiKeys.getKeyType()) {
      case IO -> organizationRepository.updateIoApiKey(organizationId, encryptedApiKey);
      case SEND -> organizationRepository.updateSendApiKey(organizationId, encryptedApiKey);
      case GENERATE_NOTICE -> organizationRepository.updateGenerateNoticeApiKey(organizationId, encryptedApiKey);
    };

    if (updatedRows == 0) {
      throw new OrganizationNotFoundException("Organization with ID %s was not found".formatted(organizationId));
    }
  }

  public String getApiKey(Long organizationId, OrganizationApiKeyType keyType) {
    Organization organization = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new ResourceNotFoundException("Organization [%s]".formatted(organizationId)));

    String apiKeyResult = null;
    switch (keyType) {
      case IO -> apiKeyResult = organization.isFlagNotifyIo() ? organizationEncryptionService.decryptKey(organization.getIoApiKey()) : null;
      case SEND -> apiKeyResult = organizationEncryptionService.decryptKey(organization.getSendApiKey());
      case GENERATE_NOTICE -> {
        if (organization.getGenerateNoticeApiKey() != null) {
          apiKeyResult =  organizationEncryptionService.decryptKey(organization.getGenerateNoticeApiKey());
        } else {
          Broker broker = brokerRepository.findByBrokeredOrganizationId(String.valueOf(organizationId))
            .orElseThrow(() -> new ResourceNotFoundException("Broker not found for orgId [%s]".formatted(organizationId)));
          apiKeyResult =  organizationEncryptionService.decryptKey(broker.getGenerateNoticeKey());
        }
      }
    }

    return apiKeyResult;
  }
}
