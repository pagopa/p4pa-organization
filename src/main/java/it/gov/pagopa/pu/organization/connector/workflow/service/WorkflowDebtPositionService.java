package it.gov.pagopa.pu.organization.connector.workflow.service;

import it.gov.pagopa.pu.workflowhub.dto.generated.MassiveDebtPositionIbanUpdateRequestDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;

public interface WorkflowDebtPositionService {
  WorkflowCreatedDTO massiveDpIbanUpdate(Long orgId, MassiveDebtPositionIbanUpdateRequestDTO massiveDebtPositionIbanUpdateRequestDTO, String accessToken);
}
