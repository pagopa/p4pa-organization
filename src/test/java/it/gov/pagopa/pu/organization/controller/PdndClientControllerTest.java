package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.service.pdnd.PdndClientService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class PdndClientControllerTest {
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private PdndClientService pdndClientServiceMock;
  @InjectMocks
  private PdndClientController pdndClientController;

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(pdndClientServiceMock);
  }

  @Test
  void whenSavePdndClientThenOk(){
    PdndClientDTO pdndClientDTO = podamFactory.manufacturePojo(PdndClientDTO.class);
    PdndClient expectedResponse = podamFactory.manufacturePojo(PdndClient.class);

    Mockito.when(pdndClientServiceMock.savePdndClient(pdndClientDTO)).thenReturn(expectedResponse);

    ResponseEntity<PdndClient> response = pdndClientController.savePdndClient(pdndClientDTO);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(expectedResponse, response.getBody());
  }
}
