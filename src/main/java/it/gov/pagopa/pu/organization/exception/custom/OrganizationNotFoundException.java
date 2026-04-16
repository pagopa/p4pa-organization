package it.gov.pagopa.pu.organization.exception.custom;

import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;

public class OrganizationNotFoundException extends NotFoundException {

  public OrganizationNotFoundException(String message) {
            super(ErrorCodeConstants.ERROR_CODE_ORGANIZATION_NOT_FOUND, message);
        }
}
