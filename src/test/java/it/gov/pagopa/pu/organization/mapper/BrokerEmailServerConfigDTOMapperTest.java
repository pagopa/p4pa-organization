package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.EmailServerConfig;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.BrokerConfiguration;
import it.gov.pagopa.pu.organization.service.brokerconfiguration.BrokerConfigurationEncryptionService;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BrokerEmailServerConfigDTOMapperTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  @InjectMocks
  private BrokerEmailServerConfigDTOMapper brokerEmailServerConfigDTOMapper;

  @Mock
  private BrokerConfigurationEncryptionService encryptionServiceMock;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      encryptionServiceMock
    );
  }

  @Test
  void whenMapToDTOThenOk() {
    Broker broker = podamFactory.manufacturePojo(Broker.class);
    BrokerConfiguration brokerConfiguration = podamFactory.manufacturePojo(BrokerConfiguration.class);
    EmailServerConfig emailServerConfig = podamFactory.manufacturePojo(EmailServerConfig.class);

    Mockito.when(encryptionServiceMock.decryptEmailServerConfig(brokerConfiguration.getEmailServerConfig(), broker.getBrokerId()))
        .thenReturn(emailServerConfig);

    EmailServerConfigDTO result = brokerEmailServerConfigDTOMapper.mapToDTO(broker, brokerConfiguration);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result);
    Assertions.assertEquals(broker.getExternalId(),result.getBrokerExternalId());
    Assertions.assertEquals(brokerConfiguration.getMailSenderAddress(),result.getMailSenderAddress());
    Assertions.assertEquals(emailServerConfig,result.getMailServerConfig());
  }

  @Test
  void givenNullBrokerConfigurationWhenMapToDTOThenNull() {
    Broker broker = podamFactory.manufacturePojo(Broker.class);

    EmailServerConfigDTO result = brokerEmailServerConfigDTOMapper.mapToDTO(broker, null);

    assertNull(result);
  }

  @Test
  void givenNullBrokerWhenMapToDTOThenNull() {
    BrokerConfiguration brokerConfiguration = podamFactory.manufacturePojo(BrokerConfiguration.class);

    EmailServerConfigDTO result = brokerEmailServerConfigDTOMapper.mapToDTO(null, brokerConfiguration);

    assertNull(result);
  }

  @Test
  void givenNullBrokerAndBrokerConfigurationWhenMapToDTOThenNull() {
    EmailServerConfigDTO result = brokerEmailServerConfigDTOMapper.mapToDTO(null, null);

    assertNull(result);
  }
}
