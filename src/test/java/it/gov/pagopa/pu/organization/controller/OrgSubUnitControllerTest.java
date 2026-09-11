package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.dto.OrgAndSubUnitDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.service.orgsubunit.OrgSubUnitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrgSubUnitControllerTest {

  @Mock
  private OrgSubUnitService serviceMock;

  @InjectMocks
  private OrgSubUnitController controller;

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(serviceMock);
  }

  @Test
  void givenPdndServiceTypeWhenGetOrgSubUnitWithNoServiceTypeThenOk(){
    Long organizationId = 1L;
    List<OrgAndSubUnitDTO> expectedResult = List.of(new OrgAndSubUnitDTO());

    //given
    Mockito.when(serviceMock.getOrgSubUnitWithNoServiceType(organizationId, PdndServiceType.SEND))
      .thenReturn(expectedResult);
    //when
    ResponseEntity<List<OrgAndSubUnitDTO>> response = controller.getOrgSubUnitWithNoServiceType(organizationId, PdndServiceType.SEND);
    //verify
    assertNotNull(response);
    assertEquals(expectedResult, response.getBody());
  }

}
