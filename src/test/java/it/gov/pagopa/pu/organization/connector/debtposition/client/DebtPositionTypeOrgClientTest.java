package it.gov.pagopa.pu.organization.connector.debtposition.client;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.organization.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgClientTest {

  @InjectMocks
  private DebtPositionTypeOrgClient debtPositionTypeOrgClient;

  @Mock
  private DebtPositionApisHolder debtPositionApisHolderMock;
  @Mock
  private DebtPositionTypeOrgApi debtPositionTypeOrgApiMock;

  @Test
  void whenCreateTechnicalDebtPositionThenOk() {
    String accessToken = TestUtils.getFakeAccessToken();
    Long organizationId = 1L;

    when(debtPositionApisHolderMock.getDebtPositionTypeOrgApi(accessToken)).thenReturn(debtPositionTypeOrgApiMock);
    doNothing().when(debtPositionTypeOrgApiMock).createTechnicalDebtPositionTypeOrg(organizationId);

    debtPositionTypeOrgClient.createTechnicalDebtPositionTypeOrg(organizationId, accessToken);

    Mockito.verifyNoMoreInteractions(debtPositionApisHolderMock);
  }

}
