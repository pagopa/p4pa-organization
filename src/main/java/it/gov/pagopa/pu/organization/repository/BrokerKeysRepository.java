package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.BrokerKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "broker-keys", exported = false)
public interface BrokerKeysRepository extends JpaRepository<BrokerKeys, String>  {
}
