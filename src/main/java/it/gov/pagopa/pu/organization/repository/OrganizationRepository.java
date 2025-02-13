package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "organizations")
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  Optional<Organization> findByIpaCode(String ipaCode);
  Optional<Organization> findByOrgFiscalCode(String orgFiscalCode);
  List<Organization> findByBrokerId(Long brokerId);

}
