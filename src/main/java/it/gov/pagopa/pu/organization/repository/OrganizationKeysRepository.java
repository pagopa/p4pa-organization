package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.OrganizationKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "organization-keys")
public interface OrganizationKeysRepository extends JpaRepository<OrganizationKeys, String> {
}
