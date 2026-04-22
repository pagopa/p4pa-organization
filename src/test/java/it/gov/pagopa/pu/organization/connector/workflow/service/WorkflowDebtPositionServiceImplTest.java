package it.gov.pagopa.pu.organization.connector.workflow.service;

import it.gov.pagopa.pu.organization.connector.workflow.client.WorkflowDebtPositionApiClient;
import it.gov.pagopa.pu.workflowhub.dto.generated.MassiveDebtPositionIbanUpdateRequestDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.WorkflowCreatedDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowDebtPositionServiceImplTest {
  @Mock
  private WorkflowDebtPositionApiClient workflowApiClientMock;

  private WorkflowDebtPositionService workflowService;

  @BeforeEach
  void init() {
    workflowService = new WorkflowDebtPositionServiceImpl(
      workflowApiClientMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      workflowApiClientMock
    );
  }

  @Test
  void whenMassiveDpIbanUpdateThenOk() {
    String accessToken = "accessToken";
    Long orgId = 1L;
    MassiveDebtPositionIbanUpdateRequestDTO requestDTO = new MassiveDebtPositionIbanUpdateRequestDTO();
    WorkflowCreatedDTO expectedResult = new WorkflowCreatedDTO();

    Mockito.when(workflowApiClientMock.massiveDpIbanUpdate(orgId, requestDTO, accessToken))
      .thenReturn(expectedResult);

    WorkflowCreatedDTO result = workflowService.massiveDpIbanUpdate(orgId, requestDTO, accessToken);

    Assertions.assertSame(expectedResult, result);
  }
}
