package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.service.pdnd.PdndServiceService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdndServiceControllerTest {

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private PdndServiceService pdndServiceServiceMock;
  @InjectMocks
  private PdndServiceController pdndServiceController;

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(pdndServiceServiceMock);
  }

  @Test
  void whenSavePdndServiceThenOk(){
    Long organizationId = 1L;
    String subUnitCode = "SUBUNITCODE";
    PdndServiceRequestDTO pdndServiceRequestDTO = podamFactory.manufacturePojo(PdndServiceRequestDTO.class);
    PdndService expectedResponse = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceServiceMock.savePdndService(organizationId, pdndServiceRequestDTO, subUnitCode))
      .thenReturn(expectedResponse);

    ResponseEntity<PdndService> response = pdndServiceController
      .savePdndService(organizationId, pdndServiceRequestDTO, subUnitCode);

    assertNotNull(response);
    assertEquals(expectedResponse, response.getBody());
  }

  @Test
  void whenGetPdndServiceThenOk() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";

    PdndService expectedResponse = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceServiceMock.getPdndService(organizationId, purposeId))
      .thenReturn(expectedResponse);

    ResponseEntity<PdndService> response = pdndServiceController.getPdndService(organizationId, purposeId);

    assertNotNull(response);
    assertEquals(expectedResponse, response.getBody());
  }
}
