package it.gov.pagopa.pu.organization.connector.workflow.client;

import it.gov.pagopa.pu.organization.connector.workflow.config.WorkflowApisHolder;
import it.gov.pagopa.pu.workflowhub.dto.generated.MassiveDebtPositionIbanUpdateRequestDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import org.springframework.stereotype.Service;

@Service
public class WorkflowDebtPositionApiClient {
  private final WorkflowApisHolder workflowApisHolder;

  public WorkflowDebtPositionApiClient(WorkflowApisHolder workflowApisHolder) {
    this.workflowApisHolder = workflowApisHolder;
  }

  public WorkflowCreatedDTO massiveDpIbanUpdate(
    Long orgId,
    MassiveDebtPositionIbanUpdateRequestDTO massiveDebtPositionIbanUpdateRequestDTO,
    String accessToken
  ) {
    return workflowApisHolder.getDebtPositionApi(accessToken).massiveDpIbanUpdate(orgId, massiveDebtPositionIbanUpdateRequestDTO);
  }
}
