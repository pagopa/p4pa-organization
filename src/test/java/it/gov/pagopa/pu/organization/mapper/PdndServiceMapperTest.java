package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.repository.PdndClientRepository;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdndServiceMapperTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private PdndClientRepository pdndClientRepositoryMock;

  @InjectMocks
  private PdndServiceMapper pdndServiceMapper;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      pdndClientRepositoryMock
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
    assertNull(pdndServiceMapper.toPdndServiceDTO(null));
  }

  @Test
  void whenToPdndServiceThenReturnPdndService() {
    PdndService request = podamFactory.manufacturePojo(PdndService.class);
    PdndClient pdndClient = podamFactory.manufacturePojo(PdndClient.class);

    when(pdndClientRepositoryMock.findById(request.getClientId()))
      .thenReturn(Optional.ofNullable(pdndClient));

    PdndServiceDTO result = pdndServiceMapper.toPdndServiceDTO(request);

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
