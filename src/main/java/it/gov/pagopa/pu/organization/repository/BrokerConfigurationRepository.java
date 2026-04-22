package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.BrokerConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "broker-configurations")
public interface BrokerConfigurationRepository extends JpaRepository<BrokerConfiguration,Long> {
  @Query("""
      select bc
      from BrokerConfiguration bc
      join Organization o on bc.brokerId = o.brokerId
      where o.organizationId = :organizationId
  """)
  Optional<BrokerConfiguration> findByOrganizationId(Long organizationId);
}
