package it.gov.pagopa.pu.organization.dto;

import it.gov.pagopa.pu.organization.enums.OrganizationStatus;

public interface BaseOrganization {
  Long getOrganizationId();
  String getOrgFiscalCode();
  String getIpaCode();
  OrganizationStatus getStatus();
  String getOrgLogo();
  String getIban();
  Long getDefaultOrganizationStationId();
}
