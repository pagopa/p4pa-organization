package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKey;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.service.brokerkeys.BrokerKeysService;
import it.gov.pagopa.pu.organization.util.Constants;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BrokerMapperTest {

  @Mock
  private BrokerKeysService brokerKeysServiceMock;

  @InjectMocks
  private BrokerMapper brokerMapper;

  private BrokerRequestDTO dto;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      brokerKeysServiceMock
    );
  }

  @BeforeEach
  void setUp() {
    dto = BrokerRequestDTO.builder()
      .brokerId(1L)
      .organizationId(23L)
      .brokerFiscalCode("99999000099")
      .brokerName("Broker Test")
      .pagoPaInteractionModel("SYNC_ACA")
      .stationId("12345000000_01")
      .defaultStationId("12345000000_01")
      .broadcastStationId("99999000015_04")
      .syncPaymentsReportingKey("syncKey")
      .syncKey("sync")
      .gpdKey("gpd")
      .generateNoticeKey("generate")
      .acaKey("aca")
      .flagDelegate(true)
      .flagPaymentsReporting(true)
      .externalId("testcreate")
      .iuvSystemId("01")
      .build();
  }

  @Test
  void givenNullBrokerRequestDTOWhenMapToModelThenReturnNull() {
    assertNull(brokerMapper.toModel(null));
  }

  @Test
  void givenValidBrokerRequestDTOWhenMapToModelThenReturnModel() {
    // Given
    Long expectedBrokerId = 1L;

    String syncKey = dto.getSyncKey();
    String acaKey = dto.getAcaKey();
    String gpdKey = dto.getGpdKey();
    String generateNoticeKey = dto.getGenerateNoticeKey();
    String syncPaymentsReportingKey = dto.getSyncPaymentsReportingKey();

    // When
    Broker result = brokerMapper.toModel(dto);

    // Then
    assertNotNull(result);

    TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId",
      "syncPaymentsReportingKey", "generateNoticeKey", "syncKey", "acaKey", "gpdKey");
    assertThat(result.getBrokerId()).isEqualTo(expectedBrokerId);
    assertThat(result.getOrganizationId()).isEqualTo(23L);
    assertThat(result.getBrokerFiscalCode()).isEqualTo("99999000099");
    assertThat(result.getBrokerName()).isEqualTo("Broker Test");
    assertThat(result.getDefaultStationId()).isEqualTo("12345000000_01");
    assertThat(result.isFlagDelegate()).isTrue();
    assertThat(result.isFlagPaymentsReporting()).isTrue();
    assertThat(result.getExternalId()).isEqualTo("testcreate");
    assertThat(result.getIuvSystemId()).isEqualTo(dto.getIuvSystemId() != null ? dto.getIuvSystemId() : Constants.DEFAULT_IUV_SYSTEM_ID);

    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.SYNC, syncKey)
    );
    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.ACA, acaKey)
    );
    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.GPD, gpdKey)
    );
    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.GENERATE_NOTICE, generateNoticeKey)
    );
    Mockito.verify(brokerKeysServiceMock).encryptAndSaveApiKey(
      expectedBrokerId, new BrokerApiKey(BrokerApiKeyType.SYNC_PAYMENTS_REPORTING, syncPaymentsReportingKey)
    );
  }

  @Test
  void testEncryptKeyNotCalledWhenAllKeysNull() {
    dto.setSyncPaymentsReportingKey(null);
    dto.setSyncKey(null);
    dto.setGpdKey(null);
    dto.setGenerateNoticeKey(null);
    dto.setAcaKey(null);

    brokerMapper.toModel(dto);

    verifyNoInteractions(brokerKeysServiceMock);
  }

}
