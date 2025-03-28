package it.gov.pagopa.pu.organization.repository;

import io.swagger.v3.oas.annotations.Parameter;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.model.Organization;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "organizations")
public interface OrganizationRepository extends
  JpaRepository<Organization, Long> {

  Optional<Organization> findByIpaCode(String ipaCode);

  Optional<Organization> findByOrgFiscalCode(String orgFiscalCode);

  List<Organization> findByBrokerIdAndStatus(Long brokerId,
    OrganizationStatus status);

  @Query("SELECT o FROM Organization o WHERE o.brokerId = :brokerId AND " +
    "(:orgName is null OR o.orgName ILIKE CONCAT('%', cast(:orgName as text), '%'))")
  Page<Organization> findByBrokerIdAndOrgName(@Parameter(required = true) @Param("brokerId") Long brokerId,
    @Param("orgName") String orgName, Pageable pageable);

  @Modifying
  @Transactional
  @RestResource(exported = false)
  @Query("UPDATE Organization o SET o.ioApiKey = :apiKey WHERE o.id = :organizationId")
  int updateIoApiKey(Long organizationId, byte[] apiKey);

  @Modifying
  @Transactional
  @RestResource(exported = false)
  @Query("UPDATE Organization o SET o.sendApiKey = :apiKey WHERE o.id = :organizationId")
  int updateSendApiKey(Long organizationId, byte[] apiKey);


}
