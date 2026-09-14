package it.gov.pagopa.pu.organization.repository;

import io.swagger.v3.oas.annotations.Parameter;
import it.gov.pagopa.pu.organization.model.OrganizationKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource(path = "organization-keys", exported = false)
public interface OrganizationKeysRepository extends JpaRepository<OrganizationKeys, String> {

  List<OrganizationKeys> findByOrganizationIdAndSubUnitCode(
    @Parameter(required = true) @Param("organizationId") Long organizationId,
    @RequestParam(required = false) @Param("subUnitCode") String subUnitCode
  );
}
