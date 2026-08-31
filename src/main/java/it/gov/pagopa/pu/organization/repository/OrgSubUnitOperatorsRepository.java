package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.OrgSubUnitOperators;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "org-sub-unit-operators")
public interface OrgSubUnitOperatorsRepository extends JpaRepository<OrgSubUnitOperators,Long> {
  Optional<OrgSubUnitOperators> findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(Long organizationId, String subUnitCode, String operatorExternalUserId);

  Page<OrgSubUnitOperators> findByOrganizationIdAndSubUnitCode(Long organizationId, String subUnitCode, Pageable pageable);
}
