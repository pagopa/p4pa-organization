package it.gov.pagopa.pu.organization.service.organization;

import static it.gov.pagopa.pu.organization.util.Utilities.isValidIban;
import static it.gov.pagopa.pu.organization.util.Utilities.isValidPIVA;

import it.gov.pagopa.pu.organization.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.organization.dto.OrganizationDTO;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.mapper.OrganizationMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import it.gov.pagopa.pu.organization.service.broker.BrokerEncryptionService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

  private final OrganizationEncryptionService organizationEncryptionService;
  private final BrokerEncryptionService brokerEncryptionService;
  private final OrganizationMapper organizationMapper;
  private final OrganizationRepository organizationRepository;
  private final BrokerRepository brokerRepository;
  private final DebtPositionTypeOrgClient debtPositionTypeOrgClient;

  private final boolean isOrgPIvaCheckEnabled;

  public OrganizationService(
    OrganizationEncryptionService organizationEncryptionService,
    BrokerEncryptionService brokerEncryptionService,
    OrganizationMapper organizationMapper,
    OrganizationRepository organizationRepository,
    BrokerRepository brokerRepository,
    DebtPositionTypeOrgClient debtPositionTypeOrgClient,
    @Value("${features.organization.piva-check}") boolean isOrgPIvaCheckEnabled) {
    this.organizationEncryptionService = organizationEncryptionService;
    this.brokerEncryptionService = brokerEncryptionService;
    this.organizationMapper = organizationMapper;
    this.organizationRepository = organizationRepository;
    this.brokerRepository = brokerRepository;
    this.debtPositionTypeOrgClient = debtPositionTypeOrgClient;
    this.isOrgPIvaCheckEnabled = isOrgPIvaCheckEnabled;
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

  @Transactional
  public void createOrganization(OrganizationCreateDTO organizationCreateDTO, String accessToken) {
    validateOrganizationCreateDTO(organizationCreateDTO);

    Organization organization = organizationRepository.save(organizationMapper.toModel(organizationCreateDTO));

    debtPositionTypeOrgClient.createTechnicalDebtPositionTypeOrg(organization.getOrganizationId(), accessToken);
  }

  public String getApiKey(Long organizationId, OrganizationApiKeyType keyType) {
    Organization organization = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new ResourceNotFoundException("Organization [%s]".formatted(organizationId)));

    return switch (keyType) {
      case IO -> organization.isFlagNotifyIo() ? organizationEncryptionService.decryptKey(organization.getIoApiKey()) : null;
      case SEND -> organizationEncryptionService.decryptKey(organization.getSendApiKey());
      case GENERATE_NOTICE -> {
        if (organization.getGenerateNoticeApiKey() != null) {
          yield organizationEncryptionService.decryptKey(organization.getGenerateNoticeApiKey());
        } else {
          Broker broker = brokerRepository.findByBrokeredOrganizationId(String.valueOf(organizationId))
            .orElseThrow(() -> new ResourceNotFoundException("Broker not found for orgId [%s]".formatted(organizationId)));
          yield brokerEncryptionService.decryptKey(broker.getGenerateNoticeKey(), BrokerApiKeyType.GENERATE_NOTICE, broker.getBrokerId());
        }
      }
    };
  }

  private void validateOrganizationCreateDTO(OrganizationCreateDTO organizationCreateDTO) {
    validateOrgFiscalCode(organizationCreateDTO);
    validateIban(organizationCreateDTO);
  }

  private void validateOrgFiscalCode(OrganizationCreateDTO organizationCreateDTO) {
    if (StringUtils.isBlank(organizationCreateDTO.getOrgFiscalCode()) ||
      !isValidPIVA(organizationCreateDTO.getOrgFiscalCode(), isOrgPIvaCheckEnabled)) {
      throw new InvalidValueException("Fiscal code is not valid");
    }
  }

  private void validateIban(OrganizationCreateDTO dto) {
    if (StringUtils.isNotBlank(dto.getIban())) {
      if (!isValidIban(dto.getIban())) {
        throw new InvalidValueException("Iban is not valid");
      }
      if (StringUtils.isNotBlank(dto.getPostalIban()) && !isValidIban(dto.getPostalIban())) {
        throw new InvalidValueException("Postal iban is not valid");
      }
    }
  }

  public OrganizationDTO getOrganization(Long organizationId) {
    Organization org = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new ResourceNotFoundException("Organization [%s] not found".formatted(organizationId)));

    return organizationMapper.mapToDTO(org);
  }
}
