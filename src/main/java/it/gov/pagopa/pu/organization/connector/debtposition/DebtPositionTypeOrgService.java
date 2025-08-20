package it.gov.pagopa.pu.organization.connector.debtposition;

/**
 * Service for handling DebtPositionTypeOrg operations.
 */
public interface DebtPositionTypeOrgService {

  /**
   * Create a technical DebtPositionTypeOrg given the Organization ID.
   *
   * @param organizationId the ID of the Organization
   * @param accessToken    the access token for authentication
   */
  void createTechnicalDebtPositionTypeOrg(Long organizationId, String accessToken);
}
