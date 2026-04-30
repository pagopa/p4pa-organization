package it.gov.pagopa.pu.organization.service.brokerconfiguration;

import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigRequestDTO;
import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.mapper.BrokerEmailServerConfigDTOMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.BrokerConfiguration;
import it.gov.pagopa.pu.organization.repository.BrokerConfigurationRepository;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrokerConfigurationService {
  private final BrokerRepository brokerRepository;
  private final BrokerConfigurationRepository brokerConfigurationRepository;
  private final BrokerConfigurationEncryptionService brokerConfigurationEncryptionService;
  private final BrokerEmailServerConfigDTOMapper brokerEmailServerConfigDTOMapper;

  public BrokerConfigurationService(BrokerRepository brokerRepository, BrokerConfigurationRepository brokerConfigurationRepository, BrokerConfigurationEncryptionService brokerConfigurationEncryptionService, BrokerEmailServerConfigDTOMapper brokerEmailServerConfigDTOMapper) {
    this.brokerRepository = brokerRepository;
    this.brokerConfigurationRepository = brokerConfigurationRepository;
    this.brokerConfigurationEncryptionService = brokerConfigurationEncryptionService;
    this.brokerEmailServerConfigDTOMapper = brokerEmailServerConfigDTOMapper;
  }

  public void saveBrokerEmailServerConfig(Long brokerId, EmailServerConfigRequestDTO emailServerConfigRequestDTO) {
    BrokerConfiguration brokerConfiguration = getBrokerConfiguration(brokerId);

    brokerConfiguration.setMailSenderAddress(emailServerConfigRequestDTO.getMailSenderAddress());
    brokerConfiguration.setEmailServerConfig(brokerConfigurationEncryptionService.encryptEmailServerConfig(emailServerConfigRequestDTO.getMailServerConfig()));

    brokerConfigurationRepository.save(brokerConfiguration);
  }

  public EmailServerConfigDTO getBrokerEmailServerConfig(Long brokerId) {
    Broker broker = getBroker(brokerId);
    BrokerConfiguration brokerConfiguration = getBrokerConfiguration(brokerId);
    return brokerEmailServerConfigDTOMapper.mapToDTO(broker, brokerConfiguration);
  }

  private Broker getBroker(Long brokerId) {
    return brokerRepository.findById(brokerId).orElseThrow(
      ()-> new BrokerNotFoundException("Broker having brokerId "+ brokerId +" not found"));
  }

  private BrokerConfiguration getBrokerConfiguration(Long brokerId) {
    return brokerConfigurationRepository.findById(brokerId).orElseThrow(
      () -> new BrokerNotFoundException("BrokerConfiguration having brokerId " + brokerId + " not found"));
  }
}
