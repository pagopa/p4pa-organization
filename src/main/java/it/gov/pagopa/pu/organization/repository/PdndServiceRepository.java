package it.gov.pagopa.pu.organization.repository;

import io.swagger.v3.oas.annotations.Parameter;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.model.PdndService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource(path = "pdnd-services")
public interface PdndServiceRepository extends JpaRepository<PdndService, String> {
  PdndService findByClientIdAndServiceType(String clientId, PdndServiceType serviceType);

  @RestResource(exported = false)
  @Query("""
    select ps
    FROM PdndService ps
    JOIN PdndClient pc ON ps.clientId=pc.clientId
    WHERE ps.serviceType=:serviceType AND pc.organizationId=:organizationId
    AND (
      (:subUnitCode IS NULL AND pc.subUnitCode IS NULL)
      OR
      (pc.subUnitCode = :orgSubUnitCode)
   )
   """)
  List<PdndService> findByOrganizationIdAndServiceTypeAndSubUnitCode(
    @Parameter(required = true) Long organizationId,
    @Parameter(required = true) PdndServiceType serviceType,
    @RequestParam(required = false) @Param("subUnitCode") String subUnitCode
  );

}
