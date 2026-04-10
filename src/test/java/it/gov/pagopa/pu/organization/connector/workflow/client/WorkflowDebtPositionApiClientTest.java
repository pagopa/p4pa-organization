package it.gov.pagopa.pu.organization.connector.workflow.client;

import it.gov.pagopa.pu.organization.connector.workflow.config.WorkflowApisHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowDebtPositionApiClientTest {
  @Mock
  private WorkflowApisHolder workflowApisHolderMock;

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

  // TODO: add tests
}
