package it.gov.pagopa.pu.organization.exception.custom;

public class OrgSilServiceNotFoundException extends BaseBusinessException {

  public OrgSilServiceNotFoundException(String message) {
    super("ORG_SIL_SERVICE_NOT_FOUND", message);
  }
}
