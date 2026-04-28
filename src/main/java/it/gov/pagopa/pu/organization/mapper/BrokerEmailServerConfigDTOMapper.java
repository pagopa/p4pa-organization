package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.BrokerConfiguration;
import it.gov.pagopa.pu.organization.service.brokerconfiguration.BrokerConfigurationEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerEmailServerConfigDTOMapper {

  private final BrokerConfigurationEncryptionService encryptionService;

  public EmailServerConfigDTO mapToDTO(Broker broker, BrokerConfiguration brokerConfiguration) {
    if (broker == null || brokerConfiguration == null) {
      return null;
    }

    EmailServerConfigDTO dto = new EmailServerConfigDTO();
    dto.setBrokerExternalId(broker.getExternalId());
    dto.setMailSenderAddress(brokerConfiguration.getMailSenderAddress());
    dto.setMailServerConfig(encryptionService.decryptEmailServerConfig(brokerConfiguration.getEmailServerConfig(), broker.getBrokerId()));
    return dto;
  }
}
