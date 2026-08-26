package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdndServiceMapperTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @InjectMocks
  private PdndServiceMapper pdndServiceMapper;

  @Test
  void givenNullPdndServiceRequestDTOWhenToModelThenReturnNull() {
    assertNull(pdndServiceMapper.toModel(null));
  }

  @Test
  void whenToModelThenReturnPdndClient() {
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
        "updateTraceId",
        "privateKeyCipher"
      )
      .isEqualTo(requestDTO);
  }
}
