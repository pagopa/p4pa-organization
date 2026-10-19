package it.gov.pagopa.pu.organization.repository.view;

import it.gov.pagopa.pu.organization.model.view.PdndClientView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource(path = "pdnd-clients-view")
public interface PdndClientViewRepository extends Repository<PdndClientView, String> {
  @RestResource(exported = false)
  @Query("""
  SELECT new PdndClientView(
    pc.clientId,
    pc.organizationId,
    pc.subUnitCode,
    pc.clientName,
    pc.kid,
    osu.subUnitName
  )
  FROM PdndClient pc
  JOIN OrgSubUnit osu ON (
    osu.id.organizationId = pc.organizationId
    AND osu.id.subUnitCode = pc.subUnitCode
  )
  WHERE pc.organizationId = :organizationId
  AND (:subUnitCode IS NULL OR pc.subUnitCode = :subUnitCode)
  AND (:subUnitName IS NULL OR osu.subUnitName ILIKE CONCAT('%', cast(:subUnitName as text), '%'))
  """)
  Page<PdndClientView> findAllPdndClientsByOrganizationIdAndOrgSubUnits(
    @Param("organizationId") Long organizationId,
    @RequestParam(required = false) @Param("subUnitCode") String subUnitCode,
    @RequestParam(required = false) @Param("subUnitName") String subUnitName,
    Pageable pageable
  );

}
