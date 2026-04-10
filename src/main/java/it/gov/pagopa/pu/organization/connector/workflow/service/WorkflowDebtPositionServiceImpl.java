package it.gov.pagopa.pu.organization.connector.workflow.service;

import it.gov.pagopa.pu.organization.connector.workflow.client.WorkflowDebtPositionApiClient;
import it.gov.pagopa.pu.workflowhub.dto.generated.MassiveDebtPositionIbanUpdateRequestDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import org.springframework.stereotype.Service;

@Service
public class WorkflowDebtPositionServiceImpl implements WorkflowDebtPositionService {
  private final WorkflowDebtPositionApiClient workflowApiClient;

  public WorkflowDebtPositionServiceImpl(WorkflowDebtPositionApiClient workflowApiClient) {
    this.workflowApiClient = workflowApiClient;
  }

  @Override
  public WorkflowCreatedDTO massiveDpIbanUpdate(
    Long orgId,
    MassiveDebtPositionIbanUpdateRequestDTO massiveDebtPositionIbanUpdateRequestDTO,
    String accessToken
  ) {
    return this.workflowApiClient.massiveDpIbanUpdate(orgId, massiveDebtPositionIbanUpdateRequestDTO, accessToken);
  }
}
