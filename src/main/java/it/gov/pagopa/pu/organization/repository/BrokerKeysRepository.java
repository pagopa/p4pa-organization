package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.model.BrokerKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "broker-keys", exported = false)
public interface BrokerKeysRepository extends JpaRepository<BrokerKeys, String>  {

  Optional<BrokerKeys> findByBrokerIdAndKeyType(Long brokerId, BrokerApiKeyType keyType);

  List<BrokerKeys> findByBrokerId(Long brokerId);
}
