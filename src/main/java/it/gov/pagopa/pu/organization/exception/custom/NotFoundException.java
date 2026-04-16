package it.gov.pagopa.pu.organization.exception.custom;

public class NotFoundException extends BaseBusinessException {
  public NotFoundException(String code, String message) {
    this(code, message, null);
  }

  public NotFoundException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
