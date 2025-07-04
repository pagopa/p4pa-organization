package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.enums.OrgSilServiceType;
import it.gov.pagopa.pu.organization.model.orgsilservice.OrgSilService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "org-sil-services")
public interface OrgSilServiceRepository extends JpaRepository<OrgSilService, Long> {
    List<OrgSilService> findAllByOrganizationIdAndServiceType(Long organizationId, OrgSilServiceType serviceType);
}
