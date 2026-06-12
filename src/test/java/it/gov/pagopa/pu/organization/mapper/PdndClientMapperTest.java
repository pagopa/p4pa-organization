package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.service.pdnd.PdndClientEncryptionService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdndClientMapperTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  @InjectMocks
  private PdndClientMapper pdndClientMapper;

  @Mock
  private PdndClientEncryptionService encryptionServiceMock;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      encryptionServiceMock
    );
  }

  @Test
  void givenNullPdndClientCreateDTOWhenToModelThenReturnNull() {
    assertNull(pdndClientMapper.toModel(null));
  }

  @Test
  void whenToModelThenReturnPdndClient() {
    PdndClientDTO pdndClientDTO = podamFactory.manufacturePojo(PdndClientDTO.class);
    byte[] expectedEncryptedPrivateKey = "EncryptedPrivateKey".getBytes(StandardCharsets.UTF_8);

    when(encryptionServiceMock.encrypt(pdndClientDTO.getPrivateKey())).thenReturn(expectedEncryptedPrivateKey);

    PdndClient result = pdndClientMapper.toModel(pdndClientDTO);

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
      .isEqualTo(pdndClientDTO);

    assertThat(result.getPrivateKeyCipher()).isEqualTo(expectedEncryptedPrivateKey);
  }
}
