package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.BrokerProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "broker", path = "brokers",excerptProjection = BrokerProjection.class)
public interface BrokerRepository extends JpaRepository<Broker,Long> {

}
