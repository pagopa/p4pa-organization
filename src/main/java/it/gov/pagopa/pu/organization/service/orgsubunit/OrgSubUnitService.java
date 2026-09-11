package it.gov.pagopa.pu.organization.service.orgsubunit;

import it.gov.pagopa.pu.organization.dto.OrgAndSubUnitDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrgSubUnitService {

  private final OrganizationRepository organizationRepository;
  private final OrgSubUnitRepository orgSubUnitRepository;

  public OrgSubUnitService(OrganizationRepository organizationRepository, OrgSubUnitRepository orgSubUnitRepository) {
    this.organizationRepository = organizationRepository;
    this.orgSubUnitRepository = orgSubUnitRepository;
  }

  public List<OrgAndSubUnitDTO> getOrgSubUnitWithNoServiceType(Long organizationId, PdndServiceType serviceType) {
    List<OrgAndSubUnitDTO> result = new ArrayList<>();

    organizationRepository.findOrgWithNoServiceType(organizationId, serviceType)
      .ifPresent(result::add);

    List<OrgAndSubUnitDTO> subUnits = orgSubUnitRepository.findSubUnitsWithNoServiceType(organizationId, serviceType);
    result.addAll(subUnits);

    return result;
  }
}
