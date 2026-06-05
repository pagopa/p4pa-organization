package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.service.broker.BrokerEncryptionService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerMapperTest {

  @Mock
  private BrokerEncryptionService encryptionServiceMock;

  @InjectMocks
  private BrokerMapper brokerMapper;

  private BrokerRequestDTO dto;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      encryptionServiceMock
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
    byte[] expectedEncryptedSyncPaymentsReportingKey = "encryptedSyncPaymentsReportingKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encryptKey(dto.getSyncPaymentsReportingKey())).thenReturn(expectedEncryptedSyncPaymentsReportingKey);

    byte[] expectedEncryptedSyncKey = "encryptedSyncKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encryptKey(dto.getSyncKey())).thenReturn(expectedEncryptedSyncKey);

    byte[] expectedEncryptedGpdKey = "encryptedGpdKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encryptKey(dto.getGpdKey())).thenReturn(expectedEncryptedGpdKey);

    byte[] expectedEncryptedGenerateNoticeKey = "encryptedGenerateNoticeKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encryptKey(dto.getGenerateNoticeKey())).thenReturn(expectedEncryptedGenerateNoticeKey);

    byte[] expectedEncryptedAcaKey = "encryptedAcaKey".getBytes(StandardCharsets.UTF_8);
    when(encryptionServiceMock.encryptKey(dto.getAcaKey())).thenReturn(expectedEncryptedAcaKey);


    Broker result = brokerMapper.toModel(dto);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");

    assertThat(result.getBrokerId()).isEqualTo(1L);
    assertThat(result.getOrganizationId()).isEqualTo(23L);
    assertThat(result.getBrokerFiscalCode()).isEqualTo("99999000099");
    assertThat(result.getBrokerName()).isEqualTo("Broker Test");
    assertThat(result.getDefaultStationId()).isEqualTo("12345000000_01");
    assertThat(result.isFlagDelegate()).isTrue();
    assertThat(result.isFlagPaymentsReporting()).isTrue();
    assertThat(result.getExternalId()).isEqualTo("testcreate");

    assertThat(result.getSyncPaymentsReportingKey()).isEqualTo(expectedEncryptedSyncPaymentsReportingKey);
    assertThat(result.getSyncKey()).isEqualTo(expectedEncryptedSyncKey);
    assertThat(result.getGpdKey()).isEqualTo(expectedEncryptedGpdKey);
    assertThat(result.getGenerateNoticeKey()).isEqualTo(expectedEncryptedGenerateNoticeKey);
    assertThat(result.getAcaKey()).isEqualTo(expectedEncryptedAcaKey);
  }

  @Test
  void testEncryptKeyNotCalledWhenAllKeysNull() {
    dto.setSyncPaymentsReportingKey(null);
    dto.setSyncKey(null);
    dto.setGpdKey(null);
    dto.setGenerateNoticeKey(null);
    dto.setAcaKey(null);

    brokerMapper.toModel(dto);

    verifyNoInteractions(encryptionServiceMock);
  }

}
