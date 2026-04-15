package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.BrokerConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "broker-configurations")
public interface BrokerConfigurationRepository extends JpaRepository<BrokerConfiguration,Long> {

}
