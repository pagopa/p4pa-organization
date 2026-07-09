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
import it.gov.pagopa.pu.organization.service.brokerkeys.BrokerKeysService;
import it.gov.pagopa.pu.organization.service.organizationkeys.OrganizationKeysService;
import it.gov.pagopa.pu.organization.service.organizationstation.DefaultOrganizationStationService;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import it.gov.pagopa.pu.workflowhub.dto.generated.MassiveDebtPositionIbanUpdateRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrganizationService {
  private final BrokerKeysService brokerKeysService;
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

      OrganizationStation saved = defaultOrganizationStationService.createOrUpdateDefaultOrganizationStation(organizationId, brokerId, segregationCode);

      organization.setDefaultOrganizationStationId(saved.getOrganizationStationId());
      organization = organizationRepository.save(organization);

      saveOrganizationKeyIfPresent(organizationId, OrganizationApiKeys.KeyTypeEnum.SEND, organizationCreateDTO.getSendApiKey());
      saveOrganizationKeyIfPresent(organizationId, OrganizationApiKeys.KeyTypeEnum.IO, organizationCreateDTO.getIoApiKey());
      saveOrganizationKeyIfPresent(organizationId, OrganizationApiKeys.KeyTypeEnum.GENERATE_NOTICE, organizationCreateDTO.getGenerateNoticeApiKey());
    }

    debtPositionTypeOrgClient.createTechnicalDebtPositionTypeOrg(organization.getOrganizationId(), accessToken);

    return organization;
  }

  public String getApiKey(Long organizationId, OrganizationApiKeyType keyType, String subUnitCode) {
    Organization organization = organizationRepository.findById(organizationId)
      .orElseThrow(() -> new OrganizationNotFoundException(ORGANIZATION_NOT_FOUND_MSG.formatted(organizationId)));

    return switch (keyType) {
      case IO -> organization.isFlagNotifyIo() ? organizationKeysService.getApiKey(organizationId, keyType, subUnitCode) : null;
      case SEND -> {
        String key = organizationKeysService.getApiKey(organizationId, keyType, subUnitCode);
        if(Objects.isNull(key) && !Objects.isNull(subUnitCode)) {
          key = organizationKeysService.getApiKey(organizationId, keyType, null);
        }
        yield key;
      }
      case GENERATE_NOTICE -> {
        String key = organizationKeysService.getApiKey(organizationId, keyType, subUnitCode);
        if(key!=null) {
          yield key;
        } else {
          Broker broker = brokerRepository.findByBrokeredOrganizationId(String.valueOf(organizationId))
            .orElseThrow(() -> new BrokerNotFoundException("Broker for org with id %s not found".formatted(organizationId)));
          yield brokerKeysService.getBrokerDecryptedApiKey(broker.getBrokerId(), BrokerApiKeyType.GENERATE_NOTICE);
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

    handleOrganizationStationUpdate(organization);

    organizationValidatorService.validateOrganizationDTO(organization, existingOrganization);
    triggerMassiveIbanUpdateIfNeeded(existingOrganization, organization, accessToken);
    organizationRepository.save(organizationMapper.toModel(organization));
  }

  private void handleOrganizationStationUpdate(OrganizationDetailDTO organization) {
    String segregationCode = organization.getSegregationCode();
    if (segregationCode == null) {
      if (OrganizationStatus.DRAFT.equals(organization.getStatus())) {
        organization.setDefaultOrganizationStationId(null);
      }
      return;
    }

    Long brokerId = organization.getBrokerId();
    if (brokerId == null) {
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_MISSING_BROKER_ID, "Broker id is required when segregation code is provided");
    }

    Long defaultOrganizationStationId = organization.getDefaultOrganizationStationId();
    if (defaultOrganizationStationId != null) {
      defaultOrganizationStationService.updateDefaultOrganizationStationSegregationCode(
        defaultOrganizationStationId,
        organization.getOrganizationId(),
        segregationCode
      );
    } else {
      OrganizationStation station = defaultOrganizationStationService.createOrUpdateDefaultOrganizationStation(
        organization.getOrganizationId(), brokerId, segregationCode
      );

      organization.setDefaultOrganizationStationId(station.getOrganizationStationId());
    }
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

  private void saveOrganizationKeyIfPresent(Long organizationId, OrganizationApiKeys.KeyTypeEnum keyType, String key) {
    if (key != null) {
      organizationKeysService.encryptAndSave(organizationId, new OrganizationApiKeys(keyType, key), null);
    }
  }
}
