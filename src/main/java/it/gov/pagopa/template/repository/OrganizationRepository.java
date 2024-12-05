package it.gov.pagopa.template.repository;

import it.gov.pagopa.template.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface OrganizationRepository extends JpaRepository<Organization, String> {

  Optional<Organization> findByIpaCode(String ipaCode);

}
