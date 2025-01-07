package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "brokers")
public interface BrokerRepository extends JpaRepository<Broker,Long> {

  @Query("select b from broker b join Organization o on b.brokerId = o.brokerId " +
    "where o.orgFiscalCode = :orgFiscalCode")
  Optional<Broker> findByOrgFiscalCode(String orgFiscalCode);

}
