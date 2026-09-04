package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import it.gov.pagopa.pu.organization.util.SecurityUtilsTest;
import it.gov.pagopa.pu.organization.util.UtilitiesTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgSubUnitEntityExtendedControllerTest {
  @Mock
  private OrgSubUnitRepository repositoryMock;

  private OrgSubUnitEntityExtendedController controller;

  @BeforeEach
  void init() {
    controller = new OrgSubUnitEntityExtendedController(repositoryMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(repositoryMock);
  }

  @Test
  void whenUpdateStatusThenInvokeRepository() {
    Long orgId = 1L;
    String subUnitCode = "subUnitCode";
    OrgSubUnitStatus newStatus = OrgSubUnitStatus.CANCELLED;

    controller.updateStatus(orgId, subUnitCode, newStatus);

    verify(repositoryMock).updateStatus(orgId, subUnitCode, newStatus);
  }
}
