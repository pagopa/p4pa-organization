package it.gov.pagopa.pu.organization.service.organizationstation;

import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationStationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefaultOrganizationStationService {
  private final BrokerRepository brokerRepository;
  private final OrganizationStationRepository organizationStationRepository;

  public DefaultOrganizationStationService(BrokerRepository brokerRepository, OrganizationStationRepository organizationStationRepository) {
    this.brokerRepository = brokerRepository;
    this.organizationStationRepository = organizationStationRepository;
  }

  public OrganizationStation createDefaultOrganizationStation(Long organizationId, Long brokerId, String segregationCode) {
    Broker broker = brokerRepository
      .findById(brokerId)
      .orElseThrow(() -> new BrokerNotFoundException("Broker having brokerId " + brokerId + " not found"));

    String defaultStationId = broker.getDefaultStationId();

    OrganizationStation toSave = new OrganizationStation();
    toSave.setStationId(defaultStationId);
    toSave.setSegregationCode(segregationCode);
    toSave.setOrganizationId(organizationId);

    return organizationStationRepository.save(toSave);
  }
}
