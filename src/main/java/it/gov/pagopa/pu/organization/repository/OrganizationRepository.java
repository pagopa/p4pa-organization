package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.Organization;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "organizations")
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  Optional<Organization> findByIpaCode(String ipaCode);
  Optional<Organization> findByOrgFiscalCode(String orgFiscalCode);
  List<Organization> findByBrokerId(Long brokerId);

  @Modifying
  @Transactional
  @RestResource(exported = false)
  @Query("UPDATE Organization o SET " +
    "o.ioApiKey = CASE WHEN :typeEnum = 'IO' THEN :apiKey ELSE o.ioApiKey END, " +
    "o.sendApiKey = CASE WHEN :typeEnum = 'SEND' THEN :apiKey ELSE o.sendApiKey END " +
    "WHERE o.id = :organizationId")
  void updateApiKeyByType(@Param("organizationId") Long organizationId,
                          @Param("typeEnum") String typeEnum,
                          @Param("apiKey") byte[] apiKey);

}
