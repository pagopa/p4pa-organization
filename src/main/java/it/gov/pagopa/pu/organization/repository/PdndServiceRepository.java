package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.PdndService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "pdnd-services")
public interface PdndServiceRepository extends JpaRepository<PdndService, String> {
}
