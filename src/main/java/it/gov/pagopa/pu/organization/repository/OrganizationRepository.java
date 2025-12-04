package it.gov.pagopa.pu.organization.repository;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.model.Organization;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource(path = "organizations")
public interface OrganizationRepository extends
  JpaRepository<Organization, Long> {

  @RestResource(exported = false)
  @Nonnull
  @Override
  <S extends Organization> S save(@Nonnull S entity);

  Optional<Organization> findByIpaCode(String ipaCode);

  Optional<Organization> findByOrgFiscalCode(String orgFiscalCode);

  List<Organization> findByBrokerIdAndStatus(Long brokerId,
    OrganizationStatus status);

  Page<Organization> findPagedOrganizationsByBrokerIdAndStatus(
    @Parameter(required = true, schema = @Schema(type = "integer", format = "int64")) @Param("brokerId") Long brokerId,
    @Param("status")OrganizationStatus status,
    Pageable pageable);

  Organization findByExternalOrganizationId(String externalOrganizationId);

  @Query("""
    SELECT o
    FROM Organization o
    WHERE o.brokerId = :brokerId
    AND (:orgName is null OR o.orgName ILIKE CONCAT('%', cast(:orgName as text), '%'))
    AND (:ipaCode is null OR o.ipaCode = :ipaCode)
    AND (:allowedOrganizationIds is null OR o.organizationId IN :allowedOrganizationIds)
   """)
  Page<Organization> findByBrokerIdAndFilters(
    @Parameter(required = true, schema = @Schema(type = "integer", format = "int64")) @Param("brokerId") Long brokerId,
    @Param("orgName") String orgName,
    @Param("ipaCode") String ipaCode,
    @Param("allowedOrganizationIds") Set<Long> allowedOrganizationIds,
    Pageable pageable);

  @Query("SELECT o FROM Organization o WHERE o.brokerId = :brokerId AND " +
    "(:orgName is null OR o.orgName ILIKE CONCAT('%', cast(:orgName as text), '%'))")
  Page<Organization> findByBrokerIdAndOrgName(@Parameter(required = true) @Param("brokerId") Long brokerId,
    @Param("orgName") String orgName, Pageable pageable);

  @Query("""
    SELECT o
    FROM Organization o
    WHERE
    o.brokerId = :brokerId
    AND (:orgName is null OR o.orgName ILIKE CONCAT('%', cast(:orgName as text), '%'))
    AND (:orgFiscalCode is null OR o.orgFiscalCode ILIKE CONCAT('%', cast(:orgFiscalCode as text), '%'))
  """)
  Page<Organization> findByBrokerIdAndOrgNameAndOrgFiscalCode(
    @Parameter(required = true) @Param("brokerId") Long brokerId,
    @Param("orgName") String orgName,
    @Param("orgFiscalCode") String orgFiscalCode,
    Pageable pageable);

  @Modifying
  @Transactional
  @RestResource(exported = false)
  @Query("UPDATE Organization o SET o.ioApiKey = :apiKey WHERE o.id = :organizationId")
  Integer updateIoApiKey(Long organizationId, byte[] apiKey);

  @Modifying
  @Transactional
  @RestResource(exported = false)
  @Query("UPDATE Organization o SET o.sendApiKey = :apiKey WHERE o.id = :organizationId")
  Integer updateSendApiKey(Long organizationId, byte[] apiKey);

  @Modifying
  @Transactional
  @RestResource(exported = false)
  @Query("UPDATE Organization o SET o.generateNoticeApiKey = :apiKey WHERE o.id = :organizationId")
  Integer updateGenerateNoticeApiKey(Long organizationId, byte[] apiKey);

  @Query("""
    SELECT o
    FROM Organization o
    JOIN broker b on b.organizationId = o.organizationId
    WHERE b.brokerId = :brokerId
    """)
  Organization getBrokerOrganization(Long brokerId);
}
