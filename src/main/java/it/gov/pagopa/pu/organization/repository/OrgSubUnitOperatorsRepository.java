package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.OrgSubUnitOperators;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "org-sub-unit-operators")
public interface OrgSubUnitOperatorsRepository extends JpaRepository<OrgSubUnitOperators,Long> {
}
