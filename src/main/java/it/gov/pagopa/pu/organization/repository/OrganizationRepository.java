package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "organization", path = "organization")
public interface OrganizationRepository extends
  JpaRepository<OrganizationEntity,Long> {

}
