package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.entity.BrokerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "broker", path = "broker")
public interface BrokerRepository extends JpaRepository<BrokerEntity,Long> {

}
