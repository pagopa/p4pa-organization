package it.gov.pagopa.pu.organization.connector.workflow.client;

import it.gov.pagopa.pu.organization.connector.workflow.config.WorkflowApisHolder;
import it.gov.pagopa.pu.workflowhub.controller.generated.DebtPositionApi;
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
class WorkflowDebtPositionApiClientTest {
  @Mock
  private WorkflowApisHolder workflowApisHolderMock;
  @Mock
  private DebtPositionApi debtPositionApiMock;

  private WorkflowDebtPositionApiClient workflowApiClient;

  @BeforeEach
  void setUp() {
    workflowApiClient = new WorkflowDebtPositionApiClient(workflowApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      workflowApisHolderMock
    );
  }

  @Test
  void whenMassiveDpIbanUpdateThenInvokeWithAccessToken() {
    String accessToken = "accessToken";
    Long orgId = 1L;
    MassiveDebtPositionIbanUpdateRequestDTO requestDTO = new MassiveDebtPositionIbanUpdateRequestDTO();
    WorkflowCreatedDTO expectedResult = new WorkflowCreatedDTO();

    Mockito.when(workflowApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    Mockito.when(debtPositionApiMock.massiveDpIbanUpdate(orgId, requestDTO))
      .thenReturn(expectedResult);

    WorkflowCreatedDTO result = workflowApiClient.massiveDpIbanUpdate(orgId, requestDTO, accessToken);

    Assertions.assertEquals(expectedResult, result);
  }
}
