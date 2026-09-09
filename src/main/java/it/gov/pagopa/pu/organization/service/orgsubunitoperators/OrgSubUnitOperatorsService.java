package it.gov.pagopa.pu.organization.service.orgsubunitoperators;

import it.gov.pagopa.pu.organization.model.OrgSubUnitOperators;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitOperatorsRepository;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrgSubUnitOperatorsService {

  private final OrgSubUnitRepository orgSubUnitRepository;
  private final OrgSubUnitOperatorsRepository orgSubUnitOperatorsRepository;

  @Transactional
  public void addOrgSubUnitsToOperator(Long organizationId, String mappedExternalUserId, List<String> orgSubUnitCodes) {
    Set<String> requestedSubUnitCodes = new HashSet<>(orgSubUnitCodes);

    Set<String> existingSubUnitCodes = orgSubUnitRepository.findExistingSubUnitCodes(organizationId, requestedSubUnitCodes);

    Set<String> missingSubUnitCodes = new HashSet<>(requestedSubUnitCodes);
    missingSubUnitCodes.removeAll(existingSubUnitCodes);

    if (!missingSubUnitCodes.isEmpty()) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST, "OrgSubUnits %s do not exist for organization %s".formatted(missingSubUnitCodes, organizationId));
    }

    Set<String> addedSubUnitCodes = new HashSet<>();

    requestedSubUnitCodes.forEach(subUnitCode -> {
      boolean alreadyAssociated =
        orgSubUnitOperatorsRepository.findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, mappedExternalUserId)
          .isPresent();

      if (!alreadyAssociated) {
        OrgSubUnitOperators association = new OrgSubUnitOperators();
        association.setOrganizationId(organizationId);
        association.setSubUnitCode(subUnitCode);
        association.setOperatorExternalUserId(mappedExternalUserId);

        orgSubUnitOperatorsRepository.save(association);
        addedSubUnitCodes.add(subUnitCode);
      }
    });
    log.info("Added orgSubUnits {} to operator {} for organization {}", addedSubUnitCodes, mappedExternalUserId, organizationId);
  }
}
