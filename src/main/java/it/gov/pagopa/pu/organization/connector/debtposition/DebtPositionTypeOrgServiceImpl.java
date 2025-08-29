package it.gov.pagopa.pu.organization.connector.debtposition;

import it.gov.pagopa.pu.organization.connector.debtposition.client.DebtPositionTypeOrgClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class DebtPositionTypeOrgServiceImpl implements DebtPositionTypeOrgService {

  private final DebtPositionTypeOrgClient debtPositionTypeOrgClient;

  public DebtPositionTypeOrgServiceImpl(DebtPositionTypeOrgClient debtPositionTypeOrgClient) {
    this.debtPositionTypeOrgClient = debtPositionTypeOrgClient;
  }

  @Override
  public void createTechnicalDebtPositionTypeOrg(Long organizationId, String accessToken) {
    debtPositionTypeOrgClient.createTechnicalDebtPositionTypeOrg(organizationId, accessToken);
  }
}
