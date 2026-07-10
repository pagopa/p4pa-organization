package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.BaseOrganization;
import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.pu.organization.util.Utilities.*;

@Service
public class OrganizationValidatorService {
  private final boolean isOrgPIvaCheckEnabled;

  public OrganizationValidatorService(@Value("${features.organization.piva-check}") boolean isOrgPIvaCheckEnabled) {
    this.isOrgPIvaCheckEnabled = isOrgPIvaCheckEnabled;
  }

  public void validateOrganizationCreateDTO(OrganizationCreateDTO organizationCreateDTO) {
    validateSegregationCode(organizationCreateDTO);
    validateOrgFiscalCode(organizationCreateDTO);
    validateIban(organizationCreateDTO);
    validatePostalIban(organizationCreateDTO);
  }

  public void validateOrganizationDTO(OrganizationDetailDTO organization, Organization existingOrganization) {
    validateOrganizationCreateDTO(organization);
    checkReadOnlyFields(existingOrganization,organization);
    validateStatusUpdate(organization);
  }

  public void validateStatusUpdate(BaseOrganization organization) {
    if(OrganizationStatus.ACTIVE.equals(organization.getStatus())){
      List<String> emptyOrNullFields = new ArrayList<>();
      checkBlankOrNullField("orgLogo", organization.getOrgLogo(), emptyOrNullFields);
      checkBlankOrNullField("iban", organization.getIban(), emptyOrNullFields);
      checkBlankOrNullField("defaultOrganizationStationId", organization.getDefaultOrganizationStationId(), emptyOrNullFields);

      if (!CollectionUtils.isEmpty(emptyOrNullFields)) {
        throw new InvalidValueException(
          ErrorCodeConstants.ERROR_CODE_MISSING_ORGANIZATION_FIELDS,
          "The following Organization fields are required for organizations with status ACTIVE. " + emptyOrNullFields
        );
      }
    }
  }

  private void validateOrgFiscalCode(OrganizationCreateDTO organizationCreateDTO) {
    if (StringUtils.isBlank(organizationCreateDTO.getOrgFiscalCode()) ||
      !isValidPIVA(organizationCreateDTO.getOrgFiscalCode(), isOrgPIvaCheckEnabled)) {
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_INVALID_VAT_CODE, "Fiscal code is not valid");
    }
  }

  private void validateIban(OrganizationCreateDTO dto) {
    if (StringUtils.isNotBlank(dto.getIban()) && !isValidIban(dto.getIban())) {
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_INVALID_IBAN, "Iban is not valid");
    }
  }

  private void validatePostalIban(OrganizationCreateDTO dto) {
    String postalIban = dto.getPostalIban();

    // Postal IBAN is optional, but if provided, it must not be blank
    if (postalIban != null && !isValidPostalIban(postalIban)) {
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_INVALID_POSTAL_IBAN, "Postal iban is not valid");
    }
  }

  private void validateSegregationCode(OrganizationCreateDTO organizationCreateDTO) {
    String segregationCode = organizationCreateDTO.getSegregationCode();
    if (OrganizationStatus.ACTIVE.equals(organizationCreateDTO.getStatus()) && StringUtils.isBlank(segregationCode)) {
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_INVALID_SEGREGATION_CODE,
        "Segregation code is required for organization status %s".formatted(OrganizationStatus.ACTIVE)
      );
    }

    if (StringUtils.isNotBlank(segregationCode) && !isValidSegregationCode(organizationCreateDTO.getSegregationCode())) {
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_INVALID_SEGREGATION_CODE, "Segregation code is not valid");
    }
  }

  private void checkReadOnlyFields(Organization existingOrganization, OrganizationDetailDTO organization) {
    List<String> modifiedFields = new ArrayList<>();
    checkImmutableField("brokerId", existingOrganization.getBrokerId(), organization.getBrokerId(), modifiedFields);
    checkImmutableField("externalOrganizationId", existingOrganization.getExternalOrganizationId(), organization.getExternalOrganizationId(), modifiedFields);
    checkImmutableField("ipaCode", existingOrganization.getIpaCode(), organization.getIpaCode(), modifiedFields);
    checkImmutableField("orgFiscalCode", existingOrganization.getOrgFiscalCode(), organization.getOrgFiscalCode(), modifiedFields);
    checkImmutableField("orgName", existingOrganization.getOrgName(), organization.getOrgName(), modifiedFields);
    checkImmutableField("orgTypeCode", existingOrganization.getOrgTypeCode(), organization.getOrgTypeCode(), modifiedFields);
    if(!CollectionUtils.isEmpty(modifiedFields)){
      throw new InvalidValueException(ErrorCodeConstants.ERROR_CODE_IMMUTABLE_FIELD, "The following Organization fields are readOnly. "+modifiedFields);
    }
  }
}
