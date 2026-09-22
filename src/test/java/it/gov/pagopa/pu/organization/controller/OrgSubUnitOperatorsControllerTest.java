package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.service.orgsubunitoperators.OrgSubUnitOperatorsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrgSubUnitOperatorsControllerTest {

  @Mock
  private OrgSubUnitOperatorsService orgSubUnitOperatorsServiceMock;

  private OrgSubUnitOperatorsController controller;

  @BeforeEach
  void setUp() {
    controller = new OrgSubUnitOperatorsController(
      orgSubUnitOperatorsServiceMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      orgSubUnitOperatorsServiceMock
    );
  }

  @Test
  void whenAddOrgSubUnitsToOperatorThenOk() {
    Long organizationId = 1L;
    String mappedExternalUserId = "mappedExternalUserId";
    List<String> orgSubUnitCodes = List.of("SUB_UNIT_1", "SUB_UNIT_2");

    ResponseEntity<Void> result = controller.addOrgSubUnitsToOperator(organizationId, mappedExternalUserId, orgSubUnitCodes);

    assertEquals(200, result.getStatusCode().value());

    verify(orgSubUnitOperatorsServiceMock).addOrgSubUnitsToOperator(organizationId, mappedExternalUserId, orgSubUnitCodes);
  }

  @Test
  void whenDeleteOrgSubUnitFromOperatorThenOk() {
    Long organizationId = 1L;
    String mappedExternalUserId = "mappedExternalUserId";
    String subUnitCode = "SUB_UNIT_1";

    ResponseEntity<Void> result = controller.deleteOrgSubUnitFromOperator(organizationId, mappedExternalUserId, subUnitCode);

    assertEquals(200, result.getStatusCode().value());

    verify(orgSubUnitOperatorsServiceMock).deleteOrgSubUnitFromOperator(organizationId, mappedExternalUserId, subUnitCode);
  }

  @Test
  void whenAddOperatorsToOrgSubUnitThenOk() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_UNIT_1";
    List<String> mappedExternalUserIds = List.of("mappedExternalUserId1", "mappedExternalUserId2");

    ResponseEntity<Void> result = controller.addOperatorsToOrgSubUnit(organizationId, subUnitCode, mappedExternalUserIds);

    assertEquals(200, result.getStatusCode().value());

    verify(orgSubUnitOperatorsServiceMock).addOperatorsToOrgSubUnit(organizationId, subUnitCode, mappedExternalUserIds);
  }
}
