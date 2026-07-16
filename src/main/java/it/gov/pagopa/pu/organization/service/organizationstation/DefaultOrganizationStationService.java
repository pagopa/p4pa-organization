package it.gov.pagopa.pu.organization.service.organizationstation;

import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.custom.NotFoundException;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationStationRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultOrganizationStationService {
  private final BrokerRepository brokerRepository;
  private final OrganizationStationRepository organizationStationRepository;

  public OrganizationStation createOrUpdateDefaultOrganizationStation(Long organizationId, Long brokerId, String segregationCode) {
    Broker broker = brokerRepository.findById(brokerId)
      .orElseThrow(() -> new BrokerNotFoundException("Broker having brokerId " + brokerId + " not found"));

    String defaultStationId = broker.getDefaultStationId();

    return organizationStationRepository.findByOrganizationIdAndStationId(organizationId, defaultStationId)
      .map(existingOrganizationStation -> {
        existingOrganizationStation.setSegregationCode(segregationCode);
        return organizationStationRepository.save(existingOrganizationStation);
      })
      .orElseGet(() -> {
        OrganizationStation toSave = new OrganizationStation();
        toSave.setStationId(defaultStationId);
        toSave.setSegregationCode(segregationCode);
        toSave.setOrganizationId(organizationId);
        return organizationStationRepository.save(toSave);
      });
  }

  public void updateDefaultOrganizationStationSegregationCode(Long organizationStationId, Long organizationId, String segregationCode) {
    OrganizationStation existingOrganizationStation = organizationStationRepository
      .findById(organizationStationId)
      .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_ORGANIZATION_STATION_NOT_FOUND, "OrganizationStation having id " + organizationStationId + " not found"));

    if (!Objects.equals(existingOrganizationStation.getOrganizationId(), organizationId)) {
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_INVALID_ORGANIZATION_STATION_ID, "OrganizationStation does not belong to the given organization");
    }

    existingOrganizationStation.setSegregationCode(segregationCode);
    organizationStationRepository.save(existingOrganizationStation);
  }
}
