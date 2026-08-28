package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.service.pdnd.PdndClientService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdndServiceMapperTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private PdndClientService pdndClientServiceMock;

  @InjectMocks
  private PdndServiceMapper pdndServiceMapper;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      pdndClientServiceMock
    );
  }


  @Test
  void givenNullPdndServiceRequestDTOWhenToModelThenReturnNull() {
    assertNull(pdndServiceMapper.toModel(null));
  }

  @Test
  void whenToModelThenReturnPdndServiceDTO() {
    PdndServiceRequestDTO requestDTO = podamFactory.manufacturePojo(PdndServiceRequestDTO.class);

    PdndService result = pdndServiceMapper.toModel(requestDTO);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");

    assertThat(result)
      .usingRecursiveComparison()
      .ignoringFields(
        "creationDate",
        "updateDate",
        "updateOperatorExternalId",
        "updateTraceId"
      )
      .isEqualTo(requestDTO);
  }

  @Test
  void givenNullPdndServiceWhenToPdndServiceDTOThenReturnNull() {
    assertNull(pdndServiceMapper.toPdndServiceDTO(1L, null, null));
  }

  @Test
  void whenToPdndServiceThenReturnPdndService() {
    Long organizationId = 1L;
    String subUnitCode = "SUB";
    PdndService request = podamFactory.manufacturePojo(PdndService.class);
    PdndClientDTO pdndClientDTO = podamFactory.manufacturePojo(PdndClientDTO.class);

    when(pdndClientServiceMock.getUsablePdndClientByOrganizationIdAndPdndServiceType(organizationId, request.getServiceType(), subUnitCode))
      .thenReturn(pdndClientDTO);

    PdndServiceDTO result = pdndServiceMapper.toPdndServiceDTO(organizationId, request, subUnitCode);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");

    assertThat(result)
      .usingRecursiveComparison()
      .ignoringFields(
        "creationDate",
        "updateDate",
        "updateOperatorExternalId",
        "updateTraceId",
        "clientName"
      )
      .isEqualTo(request);
  }
}
