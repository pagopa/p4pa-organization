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
import java.util.Optional;

@RepositoryRestResource(path = "pdnd-services")
public interface PdndServiceRepository extends JpaRepository<PdndService, String> {
  PdndService findByClientIdAndServiceType(String clientId, PdndServiceType serviceType);

  boolean existsByClientId(String clientId);

  @RestResource(exported = false)
  @Query("""
    select ps
    FROM PdndService ps
    JOIN PdndClient pc ON ps.clientId=pc.clientId
    WHERE ps.serviceType=:serviceType AND pc.organizationId=:organizationId
    AND (
      (:subUnitCode IS NULL AND pc.subUnitCode IS NULL)
      OR
      (pc.subUnitCode = :subUnitCode)
   )
   """)
  List<PdndService> findByOrganizationIdAndServiceTypeAndSubUnitCode(
    @Parameter(required = true) Long organizationId,
    @Parameter(required = true) PdndServiceType serviceType,
    @RequestParam(required = false) @Param("subUnitCode") String subUnitCode
  );

  @RestResource(exported = false)
  @Query("""
  SELECT ps
  FROM PdndService ps
  JOIN PdndClient pc ON ps.clientId = pc.clientId
  WHERE pc.organizationId = :organizationId
  AND ps.purposeId = :purposeId
  """)
  Optional<PdndService> findByOrganizationIdAndPurposeId(
    @Param("organizationId") Long organizationId,
    @Param("purposeId") String purposeId
  );
}
