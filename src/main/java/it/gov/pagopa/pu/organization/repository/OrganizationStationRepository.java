package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.OrganizationStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationStationRepository extends JpaRepository<OrganizationStation, Long> {

  Optional<OrganizationStation> findByOrganizationIdAndStationId(Long organizationId, String stationId);
}
