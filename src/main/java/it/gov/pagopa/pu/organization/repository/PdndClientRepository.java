package it.gov.pagopa.pu.organization.repository;

import io.swagger.v3.oas.annotations.Parameter;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.model.PdndClient;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "pdnd-clients")
public interface PdndClientRepository extends JpaRepository<PdndClient, String> {

  @RestResource(exported = false)
  @Nonnull
  @Override
  <S extends PdndClient> S save(@Nonnull S entity);

  @RestResource(exported = false)
  @Query("""
    SELECT pc
    FROM PdndClient pc
    JOIN PdndService ps on pc.clientId = ps.clientId
    JOIN Organization o ON pc.organizationId = o.organizationId
    LEFT JOIN OrgSubUnit osu ON pc.organizationId = osu.id.organizationId AND pc.subUnitCode = osu.id.subUnitCode
    WHERE ps.serviceType = :serviceType
    AND pc.organizationId = :organizationId
    AND o.status = :#{T(it.gov.pagopa.pu.organization.enums.OrganizationStatus).ACTIVE}
    AND (
      (:subUnitCode IS NULL AND pc.subUnitCode IS NULL)
      OR
      (pc.subUnitCode = :subUnitCode AND osu.status = :#{T(it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus).ACTIVE})
    )
   """)
  Optional<PdndClient> findUsableByOrganizationIdAndServiceTypeAndSubUnitCode(
    @Parameter(required = true) Long organizationId,
    @Parameter(required = true) PdndServiceType serviceType,
    @RequestParam(required = false) @Param("subUnitCode") String subUnitCode
  );

  @RestResource(exported = false)
  List<PdndClient> findAllByOrganizationIdAndSubUnitCode(Long organizationId, String subUnitCode);

  @RestResource(exported = false)
  List<PdndClient> findAllByOrganizationIdAndSubUnitCodeIsNull(Long organizationId);

  @RestResource(exported = false)
  Optional<PdndClient> findByClientIdAndOrganizationId(String clientId, Long organizationId);
}
