package it.gov.pagopa.pu.organization.exception.custom;

public class OrganizationNotFoundException extends BaseBusinessException {

    public OrganizationNotFoundException(String message) {
            super("ORGANIZATION_NOT_FOUND", message);
        }
}
