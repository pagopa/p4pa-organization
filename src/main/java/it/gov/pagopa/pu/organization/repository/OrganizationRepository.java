package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "organizations", collectionResourceRel = "organization")
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  Optional<Organization> findByIpaCode(String ipaCode);

}
