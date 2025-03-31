package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.OrgSilService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "org-sil-services")
public interface OrgSilServiceRepository extends JpaRepository<OrgSilService, Long> {
}
