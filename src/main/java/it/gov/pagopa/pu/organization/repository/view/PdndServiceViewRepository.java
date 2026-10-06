package it.gov.pagopa.pu.organization.repository.view;

import io.swagger.v3.oas.annotations.Parameter;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.model.view.PdndServiceView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "pdnd-services-view")
public interface PdndServiceViewRepository extends Repository<PdndServiceView, String> {

  @Query("""
      SELECT new PdndServiceView(
        ps.purposeId,
        ps.serviceName,
        ps.serviceType,
        pc.clientId,
        pc.clientName,
        pc.subUnitCode,
        osu.subUnitName
      )
      FROM PdndService ps
      JOIN PdndClient pc ON ps.clientId = pc.clientId
      LEFT JOIN OrgSubUnit osu ON (
        osu.id.organizationId = pc.organizationId
        AND osu.id.subUnitCode = pc.subUnitCode
      )
      WHERE pc.organizationId = :organizationId
      AND (:serviceType IS NULL OR ps.serviceType = :serviceType)
      AND (
        (:subUnitCode IS NULL AND pc.subUnitCode IS NULL)
        OR (pc.subUnitCode = :subUnitCode)
      )
      """)
  List<PdndServiceView> findByOrganizationIdAndServiceTypeAndSubUnitCode(
    @Parameter(required = true) Long organizationId,
    @Parameter(required = true) PdndServiceType serviceType,
    @RequestParam(required = false) @Param("subUnitCode") String subUnitCode
  );

  @Query("""
    SELECT new PdndServiceView(
      ps.purposeId,
      ps.serviceName,
      ps.serviceType,
      pc.clientId,
      pc.clientName,
      pc.subUnitCode,
      osu.subUnitName
    )
    FROM PdndService ps
    JOIN PdndClient pc ON ps.clientId = pc.clientId
    LEFT JOIN OrgSubUnit osu ON (
      osu.id.organizationId = pc.organizationId
      AND osu.id.subUnitCode = pc.subUnitCode
    )
    WHERE ps.purposeId = :purposeId
    AND pc.organizationId = :organizationId
    """)
  Optional<PdndServiceView> findByOrganizationIdAndPurposeId(
    @Param("organizationId") Long organizationId,
    @Param("purposeId") String purposeId
  );


  @RestResource(exported = false)
  @Query("""
  SELECT new PdndServiceView(
    ps.purposeId,
    ps.serviceName,
    ps.serviceType,
    pc.clientId,
    pc.clientName,
    pc.subUnitCode,
    osu.subUnitName
  )
  FROM PdndService ps
  JOIN PdndClient pc ON ps.clientId = pc.clientId
  JOIN OrgSubUnit osu ON (
    osu.id.organizationId = pc.organizationId
    AND osu.id.subUnitCode = pc.subUnitCode
  )
  WHERE pc.organizationId = :organizationId
  AND (:subUnitCode IS NULL OR pc.subUnitCode = :subUnitCode)
  AND (CAST(:subUnitName AS string) IS NULL OR LOWER(osu.subUnitName) LIKE LOWER(CONCAT('%', CAST(:subUnitName AS string), '%')))
  """)
  Page<PdndServiceView> findAllPdndServicesByOrganizationIdAndOrgSubUnits(
    @Param("organizationId") Long organizationId,
    @RequestParam(required = false) @Param("subUnitCode") String subUnitCode,
    @RequestParam(required = false) @Param("subUnitName") String subUnitName,
    Pageable pageable
  );

}
