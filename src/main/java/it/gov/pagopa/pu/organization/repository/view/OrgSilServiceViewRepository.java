package it.gov.pagopa.pu.organization.repository.view;

import io.swagger.v3.oas.annotations.Parameter;
import it.gov.pagopa.pu.organization.enums.OrgSilServiceType;
import it.gov.pagopa.pu.organization.model.view.OrgSilServiceView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource(path = "org-sil-services-view")
public interface OrgSilServiceViewRepository extends Repository<OrgSilServiceView, Long> {

  @Query("""
    SELECT o
    FROM OrgSilServiceView o
    WHERE o.organizationId = :organizationId
    AND (:applicationName IS NULL OR o.applicationName ILIKE CONCAT('%', CAST(:applicationName AS text), '%'))
    AND (:serviceType IS NULL OR o.serviceType = :serviceType)
    AND (:flagLegacy IS NULL OR o.flagLegacy = :flagLegacy)
    """)
  Page<OrgSilServiceView> findOrgSilServicesByFilters(
    @Parameter(required = true) @Param("organizationId") Long organizationId,
    @RequestParam(required = false) @Param("applicationName") String applicationName,
    @RequestParam(required = false) @Param("serviceType") OrgSilServiceType serviceType,
    @RequestParam(required = false) @Param("flagLegacy") Boolean flagLegacy,
    Pageable pageable);
}
