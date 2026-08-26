package it.gov.pagopa.pu.organization.repository;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
  @RestResource(exported = false)
  @Modifying
  @Query("""
        UPDATE OrgSubUnit o
        SET o.status = :newStatus
        WHERE o.id.organizationId = :organizationId
        AND o.id.subUnitCode = :subUnitCode
        """)
  void updateStatus(Long organizationId, String subUnitCode, OrgSubUnitStatus newStatus);
}
