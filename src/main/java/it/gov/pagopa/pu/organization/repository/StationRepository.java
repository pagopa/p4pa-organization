package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "stations")
public interface StationRepository extends JpaRepository<Station, String> {

  Optional<Station> findByBrokerIdAndStationId(Long brokerId, String stationId);
}
