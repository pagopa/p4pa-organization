package it.gov.pagopa.pu.organization.exception.custom;

import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;

public class OrgSilServiceNotFoundException extends BaseBusinessException {

  public OrgSilServiceNotFoundException(String message) {
    super(ErrorCodeConstants.ERROR_CODE_ORG_SIL_SERVICE_NOT_FOUND, message);
  }
}
