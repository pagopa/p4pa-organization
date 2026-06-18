package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "org-sub-unit")
public interface OrgSubUnitRepository extends JpaRepository<OrgSubUnit, OrgSubUnit.OrgSubUnitId> {
}
