package it.gov.pagopa.pu.organization.connector.debtposition.client;

import it.gov.pagopa.pu.organization.connector.debtposition.config.DebtPositionApisHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DebtPositionTypeOrgClient {
  private final DebtPositionApisHolder debtPositionApisHolder;

  public DebtPositionTypeOrgClient(DebtPositionApisHolder debtPositionApisHolder) {
    this.debtPositionApisHolder = debtPositionApisHolder;
  }

  public void createTechnicalDebtPositionTypeOrg(Long organizationId, String accessToken) {
    debtPositionApisHolder.getDebtPositionTypeOrgApi(accessToken).createTechnicalDebtPositionTypeOrg(organizationId);
  }

}
