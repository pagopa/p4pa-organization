package it.gov.pagopa.pu.organization.service.organizationkeys;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.model.OrganizationKeys;
import it.gov.pagopa.pu.organization.repository.OrganizationKeysRepository;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import org.springframework.stereotype.Service;

@Service
public class OrganizationKeysService {
  private final OrganizationEncryptionService organizationEncryptionService;
  private final OrganizationKeysRepository organizationKeysRepository;

  public OrganizationKeysService(OrganizationEncryptionService organizationEncryptionService, OrganizationKeysRepository organizationKeysRepository) {
    this.organizationEncryptionService = organizationEncryptionService;
    this.organizationKeysRepository = organizationKeysRepository;
  }

  public void encryptAndSave(Long organizationId, OrganizationApiKeys organizationApiKeys, String subUnitCode) {
    byte[] encryptedApiKey = organizationEncryptionService.encrypt(organizationApiKeys.getApiKey());
    OrganizationKeys organizationKeys = new OrganizationKeys();
    organizationKeys.setSubUnitCode(subUnitCode);
    organizationKeys.setOrganizationId(organizationId);
    organizationKeys.setKeyType(OrganizationApiKeyType.fromValue(organizationApiKeys.getKeyType().getValue()));
    organizationKeys.setKeyCipher(encryptedApiKey);
    organizationKeysRepository.save(organizationKeys);
  }
}
