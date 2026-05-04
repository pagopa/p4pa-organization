package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.Broker;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(path = "brokers")
public interface BrokerRepository extends JpaRepository<Broker,Long> {

  @RestResource(exported = false)
  @Nonnull
  @Override
  <S extends Broker> S save(@Nonnull S entity);

  @Query("select b from broker b join Organization o on b.brokerId = o.brokerId " +
    "where o.orgFiscalCode = :orgFiscalCode")
  Optional<Broker> findByBrokeredOrgFiscalCode(String orgFiscalCode);


  @Query("select b from broker b join Organization o on b.brokerId = o.brokerId " +
    "where o.organizationId = :organizationId")
  Optional<Broker> findByBrokeredOrganizationId(String organizationId);

  Optional<Broker> findBrokerByExternalId(String externalId);
  Optional<Broker> findByBrokerFiscalCode(String brokerFiscalCode);

}
