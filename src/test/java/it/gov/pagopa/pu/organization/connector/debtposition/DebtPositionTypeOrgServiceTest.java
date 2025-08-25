package it.gov.pagopa.pu.organization.connector.debtposition;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import it.gov.pagopa.pu.organization.connector.debtposition.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgServiceTest {

  @InjectMocks
  private DebtPositionTypeOrgServiceImpl debtPositionTypeOrgService;

  @Mock
  private DebtPositionTypeOrgClient client;

  @Test
  void whenCreateTechnicalDebtPositionThenOk() {
    String accessToken = TestUtils.getFakeAccessToken();
    Long organizationId = 1L;

    doNothing().when(client).createTechnicalDebtPositionTypeOrg(organizationId, accessToken);

    debtPositionTypeOrgService.createTechnicalDebtPositionTypeOrg(organizationId, accessToken);

    verifyNoMoreInteractions(client);
  }
}
