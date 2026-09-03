package it.gov.pagopa.pu.organization.exception.custom;

import it.gov.pagopa.pu.organization.exception.common.NotFoundException;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;

@SuppressWarnings("java:S110") // Suppress "Inheritance tree of classes should not be too deep": allowed for exception hierarchy
public class OrgSilServiceNotFoundException extends NotFoundException {

  public OrgSilServiceNotFoundException(String message) {
    super(ErrorCodeConstants.ERROR_CODE_ORG_SIL_SERVICE_NOT_FOUND, message);
  }
}
