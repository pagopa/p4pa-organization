package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.organization.connector.workflow.service.WorkflowDebtPositionService;
import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.OrganizationStationDTO;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.mapper.OrganizationMapper;
import it.gov.pagopa.pu.organization.mapper.OrganizationStationMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import it.gov.pagopa.pu.organization.service.broker.BrokerEncryptionService;
import it.gov.pagopa.pu.organization.service.organizationkeys.OrganizationKeysService;
import it.gov.pagopa.pu.organization.service.organizationstation.DefaultOrganizationStationService;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import it.gov.pagopa.pu.workflowhub.dto.generated.MassiveDebtPositionIbanUpdateRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OrganizationService {

  private final OrganizationEncryptionService organizationEncryptionService;
  private final BrokerEncryptionService brokerEncryptionService;
  private final OrganizationMapper organizationMapper;
  private final OrganizationRepository organizationRepository;
  private final BrokerRepository brokerRepository;
  private final DebtPositionTypeOrgClient debtPositionTypeOrgClient;
  private final WorkflowDebtPositionService workflowDebtPositionService;
  private final OrganizationStationMapper organizationStationMapper;
  private final DefaultOrganizationStationService defaultOrganizationStationService;
  private final OrganizationValidatorService organizationValidatorService;
  private final OrganizationKeysService organizationKeysService;

  private static final String ORGANIZATION_NOT_FOUND_MSG = "Organization with id %s not found";

  public OrganizationService(
    OrganizationEncryptionService organizationEncryptionService,
    BrokerEncryptionService brokerEncryptionService,
    OrganizationMapper organizationMapper,
    OrganizationRepository organizationRepository,
    BrokerRepository brokerRepository,
    DebtPositionTypeOrgClient debtPositionTypeOrgClient,
    WorkflowDebtPositionService workflowDebtPositionService,
    OrganizationStationMapper organizationStationMapper,
    DefaultOrganizationStationService defaultOrganizationStationService,
    OrganizationValidatorService organizationValidatorService, OrganizationKeysService organizationKeysService
  ) {
    this.organizationEncryptionService = organizationEncryptionService;
    this.brokerEncryptionService = brokerEncryptionService;
    this.organizationMapper = organizationMapper;
    this.organizationRepository = organizationRepository;
    this.brokerRepository = brokerRepository;
    this.debtPositionTypeOrgClient = debtPositionTypeOrgClient;
    this.organizationStationMapper = organizationStationMapper;
    this.workflowDebtPositionService = workflowDebtPositionService;
    this.defaultOrganizationStationService = defaultOrganizationStationService;
    this.organizationValidatorService = organizationValidatorService;
    this.organizationKeysService = organizationKeysService;
  }

  public void encryptAndSaveApiKey(Long organizationId, OrganizationApiKeys organizationApiKeys, String subUnitCode) {
    organizationKeysService.encryptAndSave(organizationId, organizationApiKeys, subUnitCode);
  }

  @Transactional
  public Organization createOrganization(OrganizationCreateDTO organizationCreateDTO, String accessToken) {
    organizationValidatorService.validateOrganizationCreateDTO(organizationCreateDTO);

    Organization organization = organizationRepository.save(organizationMapper.toModel(organizationCreateDTO));

    Long organizationId = organization.getOrganizationId();

    String segregationCode = organizationCreateDTO.getSegregationCode();
    if (segregationCode != null) {
      Long brokerId = organizationCreateDTO.getBrokerId();
      if (brokerId == null) {
        throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_MISSING_BROKER_ID, "Broker id is required when segregation code is provided");
      }

      OrganizationStation saved = defaultOrganizationStationService.createDefaultOrganizationStation(organizationId, brokerId, segregationCode);

      organization.setDefaultOrganizationStationId(saved.getOrganizationStationId());
      organization = organizationRepository.save(organization);
    }

    debtPositionTypeOrgClient.createTechnicalDebtPositionTypeOrg(organization.getOrganizationId(), accessToken);

    return organization;
  }

  public String getApiKey(Long organizationId, OrganizationApiKeyType keyType) {
    Organization organization = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new OrganizationNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));

    return switch (keyType) {
      case IO -> organization.isFlagNotifyIo() ? organizationEncryptionService.decryptKey(organization.getIoApiKey()) : null;
      case SEND -> organizationEncryptionService.decryptKey(organization.getSendApiKey());
      case GENERATE_NOTICE -> {
        if (organization.getGenerateNoticeApiKey() != null) {
          yield organizationEncryptionService.decryptKey(organization.getGenerateNoticeApiKey());
        } else {
          Broker broker = brokerRepository.findByBrokeredOrganizationId(String.valueOf(organizationId))
            .orElseThrow(() -> new BrokerNotFoundException("Broker for org with id %s not found".formatted(organizationId)));
          yield brokerEncryptionService.decryptKey(broker.getGenerateNoticeKey(), BrokerApiKeyType.GENERATE_NOTICE, broker.getBrokerId());
        }
      }
    };
  }

  public OrganizationDetailDTO getOrganization(Long organizationId) {
    Organization org = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new OrganizationNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));

    OrganizationStationDTO organizationStationDTO = getOrganizationStation(org.getOrganizationId(), null);

    return organizationMapper.mapToDTO(org, organizationStationDTO.getSegregationCode());
  }

  public OrganizationStationDTO getOrganizationStation(Long organizationId, String stationId){
    Organization org = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new OrganizationNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));

    return organizationStationMapper.mapToDTO(org, stationId);
  }

  @Transactional
  public void updateOrganization(OrganizationDetailDTO organization, String accessToken) {
    Long organizationId = organization.getOrganizationId();

    Organization existingOrganization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new OrganizationNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));
    Long existingDefaultOrganizationStationId = existingOrganization.getDefaultOrganizationStationId();
    organization.setDefaultOrganizationStationId(existingDefaultOrganizationStationId);

    String segregationCode = organization.getSegregationCode();
    Long brokerId = organization.getBrokerId();

    if (segregationCode != null) {
      if (brokerId == null) {
        throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_MISSING_BROKER_ID, "Broker id is required when segregation code is provided");
      }

      if (existingDefaultOrganizationStationId == null) {
        OrganizationStation saved = defaultOrganizationStationService.createDefaultOrganizationStation(organizationId, brokerId, segregationCode);
        organization.setDefaultOrganizationStationId(saved.getOrganizationStationId());
      } else {
        defaultOrganizationStationService.updateDefaultOrganizationStationSegregationCode(existingDefaultOrganizationStationId, segregationCode);
      }
    } else {
      if (existingDefaultOrganizationStationId != null) {
        defaultOrganizationStationService.updateDefaultOrganizationStationSegregationCode(existingDefaultOrganizationStationId, null);
      }
    }

    organizationValidatorService.validateOrganizationDTO(organization, existingOrganization);
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

  public void updateOrganizationStatus(Long organizationId, OrganizationStatus newStatus) {
    Organization organization = organizationRepository.findById(organizationId)
      .orElseThrow(()->new OrganizationNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));
    organization.setStatus(newStatus);
    organizationValidatorService.validateStatusUpdate(organization);
    organizationRepository.save(organization);
  }
}
