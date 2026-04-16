package it.gov.pagopa.pu.organization.exception.custom;

import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;

public class BrokerNotFoundException extends NotFoundException {

  public BrokerNotFoundException(String message) {
    super(ErrorCodeConstants.ERROR_CODE_BROKER_NOT_FOUND, message);
  }
}
