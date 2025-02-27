package it.gov.pagopa.pu.organization.exception.custom;

public class OrganizationNotFoundException extends RuntimeException {

    public OrganizationNotFoundException(String message) {
            super(message);
        }
}
