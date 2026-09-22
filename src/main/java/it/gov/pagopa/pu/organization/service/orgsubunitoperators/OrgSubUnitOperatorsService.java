package it.gov.pagopa.pu.organization.service.orgsubunitoperators;

import it.gov.pagopa.pu.organization.model.OrgSubUnit;
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

  @Transactional
  public void deleteOrgSubUnitFromOperator(Long organizationId, String mappedExternalUserId, String subUnitCode) {
    orgSubUnitOperatorsRepository.deleteByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, mappedExternalUserId);
    log.info("Removed orgSubUnit {} from operator {} for organization {}", subUnitCode, mappedExternalUserId, organizationId);
  }

  @Transactional
  public void addOperatorsToOrgSubUnit(Long organizationId, String subUnitCode, List<String> mappedExternalUserIds) {
    boolean orgSubUnitExists = orgSubUnitRepository.existsById(new OrgSubUnit.OrgSubUnitId(organizationId, subUnitCode));

    if (!orgSubUnitExists) {throw new ResponseStatusException(
      HttpStatus.BAD_REQUEST, "OrgSubUnit %s does not exist for organization %s".formatted(subUnitCode, organizationId));
    }

    Set<String> requestedMappedExternalUserIds = new HashSet<>(mappedExternalUserIds);
    Set<String> addedMappedExternalUserIds = new HashSet<>();

    requestedMappedExternalUserIds.forEach(mappedExternalUserId -> {
      boolean alreadyAssociated = orgSubUnitOperatorsRepository
        .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, mappedExternalUserId)
        .isPresent();

      if (!alreadyAssociated) {
        OrgSubUnitOperators association = new OrgSubUnitOperators();
        association.setOrganizationId(organizationId);
        association.setSubUnitCode(subUnitCode);
        association.setOperatorExternalUserId(mappedExternalUserId);

        orgSubUnitOperatorsRepository.save(association);
        addedMappedExternalUserIds.add(mappedExternalUserId);
      }
    });

    log.info("Added operators {} to orgSubUnit {} for organization {}", addedMappedExternalUserIds, subUnitCode, organizationId);
  }
}
