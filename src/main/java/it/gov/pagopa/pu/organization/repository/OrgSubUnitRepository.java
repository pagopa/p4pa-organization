package it.gov.pagopa.pu.organization.repository;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus;
import it.gov.pagopa.pu.organization.enums.SubUnitType;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource(path = "org-sub-unit")
public interface OrgSubUnitRepository extends JpaRepository<OrgSubUnit, OrgSubUnit.OrgSubUnitId> {
  @Query("""
      select orgSub
      from OrgSubUnit orgSub
        join OrganizationKeys ok on orgSub.id.organizationId = ok.organizationId and orgSub.id.subUnitCode = ok.subUnitCode
        join OrgSubUnitOperators orgSubOperators on orgSub.id.organizationId = orgSubOperators.organizationId and orgSub.id.subUnitCode = orgSubOperators.subUnitCode
      where orgSub.status = :#{T(it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus).ACTIVE}
        and orgSubOperators.operatorExternalUserId = :operatorExternalUserId
        and orgSub.id.organizationId = :organizationId
        and ok.keyType = :keyType
  """)
  List<OrgSubUnit> getActiveOrgSubUnitWithKey(Long organizationId, String operatorExternalUserId, OrganizationApiKeyType keyType);

  @Query("""
        SELECT o
        FROM OrgSubUnit o
        WHERE o.id.organizationId = :organizationId
        """)
  List<OrgSubUnit> findAllByOrganizationId(@Param("organizationId") Long organizationId);

  @Query("""
        SELECT distinct orgSub
        FROM OrgSubUnit orgSub
        JOIN OrgSubUnitOperators orgSubOperators on orgSubOperators.organizationId = orgSub.id.organizationId
        AND orgSubOperators.subUnitCode = orgSub.id.subUnitCode
        WHERE orgSub.id.organizationId = :organizationId
        AND orgSubOperators.operatorExternalUserId = :operatorExternalUserId
        """)
  List<OrgSubUnit> findAllByOrganizationIdAndOperatorExternalUserId(
    @Param("organizationId") @Parameter(required = true, schema = @Schema(type = "integer", format = "int64")) Long organizationId,
    @Param("operatorExternalUserId") @Parameter(required = true) String operatorExternalUserId);

  @Query("""
         SELECT DISTINCT osu
         FROM OrgSubUnit osu
         LEFT JOIN OrgSubUnitOperators osuo
          ON osuo.organizationId = osu.id.organizationId AND osuo.subUnitCode = osu.id.subUnitCode
         WHERE osu.id.organizationId = :organizationId
          AND (:operatorExternalUserId IS NULL OR osuo.operatorExternalUserId = :operatorExternalUserId)
          AND (:subUnitCode IS NULL OR osu.id.subUnitCode = :subUnitCode)
          AND (:status IS NULL OR osu.status = :status)
          AND (:subUnitType IS NULL OR osu.subUnitType = :subUnitType)
         """)
  Page<OrgSubUnit> findByOrganizationIdAndFilters(
    @Param("organizationId") Long organizationId,
    @RequestParam(required = false) @Param("operatorExternalUserId") String operatorExternalUserId,
    @RequestParam(required = false) @Param("subUnitCode") String subUnitCode,
    @RequestParam(required = false) @Param("status") OrgSubUnitStatus status,
    @RequestParam(required = false) @Param("subUnitType") SubUnitType subUnitType,
    Pageable pageable
  );

  @Transactional
  @RestResource(exported = false)
  @Modifying
  @Query("""
        UPDATE OrgSubUnit o
        SET o.status = :newStatus,
          o.updateDate = CURRENT_TIMESTAMP,
          o.updateOperatorExternalId = :#{T(it.gov.pagopa.pu.organization.util.SecurityUtils).getCurrentUserExternalId()},
          o.updateTraceId = :#{T(it.gov.pagopa.pu.organization.util.Utilities).getTraceId()}
        WHERE o.id.organizationId = :organizationId
        AND o.id.subUnitCode = :subUnitCode
        """)
  void updateStatus(Long organizationId, String subUnitCode, OrgSubUnitStatus newStatus);
}
