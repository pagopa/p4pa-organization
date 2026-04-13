package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.organization.connector.workflow.service.WorkflowDebtPositionService;
import it.gov.pagopa.pu.organization.dto.BaseOrganization;
import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.mapper.OrganizationMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import it.gov.pagopa.pu.organization.service.broker.BrokerEncryptionService;
import it.gov.pagopa.pu.workflowhub.dto.generated.MassiveDebtPositionIbanUpdateRequestDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.gov.pagopa.pu.organization.util.Utilities.*;

@Service
public class OrganizationService {

  private final OrganizationEncryptionService organizationEncryptionService;
  private final BrokerEncryptionService brokerEncryptionService;
  private final OrganizationMapper organizationMapper;
  private final OrganizationRepository organizationRepository;
  private final BrokerRepository brokerRepository;
  private final DebtPositionTypeOrgClient debtPositionTypeOrgClient;
  private final WorkflowDebtPositionService workflowDebtPositionService;

  private final boolean isOrgPIvaCheckEnabled;

  private static final String ORGANIZATION_NOT_FOUND_MSG = "[ORGANIZATION_NOT_FOUND] Organization with id %s not found";

  public OrganizationService(
    OrganizationEncryptionService organizationEncryptionService,
    BrokerEncryptionService brokerEncryptionService,
    OrganizationMapper organizationMapper,
    OrganizationRepository organizationRepository,
    BrokerRepository brokerRepository,
    DebtPositionTypeOrgClient debtPositionTypeOrgClient,
    WorkflowDebtPositionService workflowDebtPositionService,
    @Value("${features.organization.piva-check}") boolean isOrgPIvaCheckEnabled) {
    this.organizationEncryptionService = organizationEncryptionService;
    this.brokerEncryptionService = brokerEncryptionService;
    this.organizationMapper = organizationMapper;
    this.organizationRepository = organizationRepository;
    this.brokerRepository = brokerRepository;
    this.debtPositionTypeOrgClient = debtPositionTypeOrgClient;
    this.isOrgPIvaCheckEnabled = isOrgPIvaCheckEnabled;
    this.workflowDebtPositionService = workflowDebtPositionService;
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
      throw new OrganizationNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId));
    }
  }

  @Transactional
  public Organization createOrganization(OrganizationCreateDTO organizationCreateDTO, String accessToken) {
    validateOrganizationCreateDTO(organizationCreateDTO);

    Organization organization = organizationRepository.save(organizationMapper.toModel(organizationCreateDTO));

    debtPositionTypeOrgClient.createTechnicalDebtPositionTypeOrg(organization.getOrganizationId(), accessToken);

    return organization;
  }

  public String getApiKey(Long organizationId, OrganizationApiKeyType keyType) {
    Organization organization = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));

    return switch (keyType) {
      case IO -> organization.isFlagNotifyIo() ? organizationEncryptionService.decryptKey(organization.getIoApiKey()) : null;
      case SEND -> organizationEncryptionService.decryptKey(organization.getSendApiKey());
      case GENERATE_NOTICE -> {
        if (organization.getGenerateNoticeApiKey() != null) {
          yield organizationEncryptionService.decryptKey(organization.getGenerateNoticeApiKey());
        } else {
          Broker broker = brokerRepository.findByBrokeredOrganizationId(String.valueOf(organizationId))
            .orElseThrow(() -> new ResourceNotFoundException("[BROKER_NOT_FOUND] Broker for org with id %s not found".formatted(organizationId)));
          yield brokerEncryptionService.decryptKey(broker.getGenerateNoticeKey(), BrokerApiKeyType.GENERATE_NOTICE, broker.getBrokerId());
        }
      }
    };
  }

  private void validateOrganizationCreateDTO(OrganizationCreateDTO organizationCreateDTO) {
    validateSegregationCode(organizationCreateDTO);
    validateOrgFiscalCode(organizationCreateDTO);
    validateIban(organizationCreateDTO);
    validatePostalIban(organizationCreateDTO);
  }

  private void validateOrgFiscalCode(OrganizationCreateDTO organizationCreateDTO) {
    if (StringUtils.isBlank(organizationCreateDTO.getOrgFiscalCode()) ||
      !isValidPIVA(organizationCreateDTO.getOrgFiscalCode(), isOrgPIvaCheckEnabled)) {
      throw new InvalidValueException("[INVALID_VAT_CODE] Fiscal code is not valid");
    }
  }

  private void validateIban(OrganizationCreateDTO dto) {
    if (StringUtils.isNotBlank(dto.getIban())) {
      if (!isValidIban(dto.getIban())) {
        throw new InvalidValueException("[INVALID_IBAN] Iban is not valid");
      }
    }
  }

  private void validatePostalIban(OrganizationCreateDTO dto) {
    String postalIban = dto.getPostalIban();

    // Postal IBAN is optional, but if provided, it must not be blank
    if (postalIban != null) {
      if (StringUtils.isBlank(postalIban)) {
        throw new InvalidValueException("[MISSING_POSTAL_IBAN] Postal IBAN is optional, but if provided, it must not be blank");
      }

      if (!isValidIban(postalIban)) {
        throw new InvalidValueException("[INVALID_POSTAL_IBAN] Postal iban is not valid");
      }
    }
  }

  public OrganizationDetailDTO getOrganization(Long organizationId) {
    Organization org = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));

    return organizationMapper.mapToDTO(org);
  }

  @Transactional
  public void updateOrganization(OrganizationDetailDTO organization, String accessToken) {
    Long organizationId = organization.getOrganizationId();
    Organization existingOrganization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));
    validateOrganizationDTO(organization, existingOrganization);
    triggerMassiveIbanUpdateIfNeeded(existingOrganization, organization, accessToken);
    organizationRepository.save(organizationMapper.toModel(organization));
  }

  private void triggerMassiveIbanUpdateIfNeeded(Organization existingOrganization, OrganizationDetailDTO organization, String accessToken) {
    String oldIban = existingOrganization.getIban();
    String newIban = organization.getIban();
    String oldPostalIban = existingOrganization.getPostalIban();
    String newPostalIban = organization.getPostalIban();

    if (oldIban == null || newIban == null) {
      return;
    }

    if (!Objects.equals(oldIban, newIban) || !Objects.equals(oldPostalIban, newPostalIban)) {
      MassiveDebtPositionIbanUpdateRequestDTO requestDTO = MassiveDebtPositionIbanUpdateRequestDTO.builder()
        .oldIban(oldIban)
        .newIban(newIban)
        .oldPostalIban(oldPostalIban)
        .newPostalIban(newPostalIban)
        .build();

      workflowDebtPositionService.massiveDpIbanUpdate(existingOrganization.getOrganizationId(), requestDTO, accessToken);
    }
  }

  private void validateOrganizationDTO(OrganizationDetailDTO organization, Organization existingOrganization) {
    validateOrganizationCreateDTO(organization);
    checkReadOnlyFields(existingOrganization,organization);
    validateStatusUpdate(organization);
  }

  private static void validateStatusUpdate(BaseOrganization organization) {
    if(OrganizationStatus.ACTIVE.equals(organization.getStatus())){
      List<String> emptyOrNullFields = new ArrayList<>();
      checkBlankOrNullField("orgLogo", organization.getOrgLogo(),emptyOrNullFields);
      checkBlankOrNullField("iban", organization.getIban(),emptyOrNullFields);
      checkBlankOrNullField("segregationCode", organization.getSegregationCode(),emptyOrNullFields);
      if(!CollectionUtils.isEmpty(emptyOrNullFields)){
        throw new ValidationException("[MISSING_ORGANIZATION_FIELDS] The following Organization fields are required in order to change the organization’s status to ACTIVE. "+emptyOrNullFields);
      }
    }
  }

  private void checkReadOnlyFields(Organization existingOrganization, OrganizationDetailDTO organization) {
    List<String> modifiedFields = new ArrayList<>();
    checkImmutableField("brokerId", existingOrganization.getBrokerId(), organization.getBrokerId(), modifiedFields);
    checkImmutableField("externalOrganizationId", existingOrganization.getExternalOrganizationId(), organization.getExternalOrganizationId(), modifiedFields);
    checkImmutableField("ipaCode", existingOrganization.getIpaCode(), organization.getIpaCode(), modifiedFields);
    checkImmutableField("orgFiscalCode", existingOrganization.getOrgFiscalCode(), organization.getOrgFiscalCode(), modifiedFields);
    checkImmutableField("orgName", existingOrganization.getOrgName(), organization.getOrgName(), modifiedFields);
    checkImmutableField("orgTypeCode", existingOrganization.getOrgTypeCode(), organization.getOrgTypeCode(), modifiedFields);
    if(!CollectionUtils.isEmpty(modifiedFields)){
      throw new ValidationException("[IMMUTABLE_FIELD] The following Organization fields are readOnly. "+modifiedFields);
    }
  }

  public void updateOrganizationStatus(Long organizationId, OrganizationStatus newStatus) {
    Organization organization = organizationRepository.findById(organizationId)
      .orElseThrow(()->new ResourceNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));
    organization.setStatus(newStatus);
    validateStatusUpdate(organization);
    organizationRepository.save(organization);
  }

  private void validateSegregationCode(OrganizationCreateDTO organizationCreateDTO) {
    if (StringUtils.isNotBlank(organizationCreateDTO.getSegregationCode()) &&
          !isValidSegregationCode(organizationCreateDTO.getSegregationCode())) {
        throw new InvalidValueException("[INVALID_SEGREGATION_CODE] Segregation code is not valid");
    }
  }
}
