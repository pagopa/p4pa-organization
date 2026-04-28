package it.gov.pagopa.pu.organization.service.brokerconfiguration;

import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigRequestDTO;
import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.mapper.BrokerEmailServerConfigDTOMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.BrokerConfiguration;
import it.gov.pagopa.pu.organization.repository.BrokerConfigurationRepository;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BrokerConfigurationServiceTest {
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();
  @Mock
  private BrokerRepository brokerRepositoryMock;
  @Mock
  private BrokerConfigurationRepository brokerConfigurationRepositoryMock;
  @Mock
  private BrokerConfigurationEncryptionService brokerConfigurationEncryptionServiceMock;
  @Mock
  private BrokerEmailServerConfigDTOMapper brokerEmailServerConfigDTOMapperMock;

  private BrokerConfigurationService brokerConfigurationService;

  @BeforeEach
  void setUp() {
    brokerConfigurationService = new BrokerConfigurationService(
      brokerRepositoryMock,
      brokerConfigurationRepositoryMock,
      brokerConfigurationEncryptionServiceMock,
      brokerEmailServerConfigDTOMapperMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
      brokerRepositoryMock,
      brokerConfigurationRepositoryMock,
      brokerConfigurationEncryptionServiceMock,
      brokerEmailServerConfigDTOMapperMock
    );
  }

  @Test
  void whenSaveBrokerEmailServerConfigThenOk() {
    Long brokerId = 1L;
    EmailServerConfigRequestDTO emailServerConfigRequestDTO = podamFactory.manufacturePojo(EmailServerConfigRequestDTO.class);
    BrokerConfiguration brokerConfiguration = podamFactory.manufacturePojo(BrokerConfiguration.class);
    brokerConfiguration.setEmailServerConfig(null);
    byte[] encrypted = "encrypted".getBytes();

    Mockito.when(brokerConfigurationRepositoryMock.findById(brokerId)).thenReturn(Optional.of(brokerConfiguration));
    Mockito.when(brokerConfigurationEncryptionServiceMock.encryptEmailServerConfig(emailServerConfigRequestDTO.getMailServerConfig())).thenReturn(encrypted);
    Mockito.when(brokerConfigurationRepositoryMock.save(brokerConfiguration)).thenReturn(null);

    brokerConfigurationService.saveBrokerEmailServerConfig(brokerId, emailServerConfigRequestDTO);

    Assertions.assertEquals(emailServerConfigRequestDTO.getMailSenderAddress(),brokerConfiguration.getMailSenderAddress());
    Assertions.assertEquals(encrypted,brokerConfiguration.getEmailServerConfig());
  }

  @Test
  void givenNotFoundWhenSaveBrokerEmailServerConfigThenBrokerNotFoundException() {
    Long brokerId = 1L;
    EmailServerConfigRequestDTO emailServerConfigRequestDTO = podamFactory.manufacturePojo(EmailServerConfigRequestDTO.class);

    Mockito.when(brokerConfigurationRepositoryMock.findById(brokerId)).thenReturn(Optional.empty());

    Assertions.assertThrows(BrokerNotFoundException.class, ()->brokerConfigurationService.saveBrokerEmailServerConfig(brokerId, emailServerConfigRequestDTO));
  }

  @Test
  void whenGetBrokerEmailServerConfigThenOk() {
    Long brokerId = 1L;
    Broker broker = podamFactory.manufacturePojo(Broker.class);
    BrokerConfiguration brokerConfiguration = podamFactory.manufacturePojo(BrokerConfiguration.class);
    EmailServerConfigDTO expectedResult = podamFactory.manufacturePojo(EmailServerConfigDTO.class);

    Mockito.when(brokerRepositoryMock.findById(brokerId)).thenReturn(Optional.of(broker));
    Mockito.when(brokerConfigurationRepositoryMock.findById(brokerId)).thenReturn(Optional.of(brokerConfiguration));
    Mockito.when(brokerEmailServerConfigDTOMapperMock.mapToDTO(broker, brokerConfiguration)).thenReturn(expectedResult);

    EmailServerConfigDTO result = brokerConfigurationService.getBrokerEmailServerConfig(brokerId);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult,result);
  }

  @Test
  void givenNoBrokerConfigurationWhenGetBrokerEmailServerConfigThenBrokerNotFoundException() {
    Long brokerId = 1L;
    Broker broker = podamFactory.manufacturePojo(Broker.class);

    Mockito.when(brokerRepositoryMock.findById(brokerId)).thenReturn(Optional.of(broker));
    Mockito.when(brokerConfigurationRepositoryMock.findById(brokerId)).thenReturn(Optional.empty());

    Assertions.assertThrows(BrokerNotFoundException.class, () -> brokerConfigurationService.getBrokerEmailServerConfig(brokerId));
  }

  @Test
  void givenNoBrokerWhenGetBrokerEmailServerConfigThenBrokerNotFoundException() {
    Long brokerId = 1L;

    Mockito.when(brokerRepositoryMock.findById(brokerId)).thenReturn(Optional.empty());

    Assertions.assertThrows(BrokerNotFoundException.class, () -> brokerConfigurationService.getBrokerEmailServerConfig(brokerId));
  }
}
