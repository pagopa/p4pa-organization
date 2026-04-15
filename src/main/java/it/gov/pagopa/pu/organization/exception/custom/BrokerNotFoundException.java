package it.gov.pagopa.pu.organization.exception.custom;

public class BrokerNotFoundException extends BaseBusinessException {

    public BrokerNotFoundException(String message) {
            super("BROKER_NOT_FOUND", message);
        }
}
