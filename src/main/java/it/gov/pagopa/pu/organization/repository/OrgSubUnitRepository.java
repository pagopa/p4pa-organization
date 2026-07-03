package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

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
}
