package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.BrokerConfigurationApi;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigRequestDTO;
import it.gov.pagopa.pu.organization.service.brokerconfiguration.BrokerConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BrokerConfigurationController implements BrokerConfigurationApi {

  private final BrokerConfigurationService service;

  public BrokerConfigurationController(BrokerConfigurationService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Void> saveBrokerEmailServerConfig(Long brokerId, EmailServerConfigRequestDTO emailServerConfigRequestDTO) {
    log.info("Updating email server config for brokerConfiguration having brokerId {}", brokerId);
    service.saveBrokerEmailServerConfig(brokerId, emailServerConfigRequestDTO);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<EmailServerConfigDTO> getBrokerEmailServerConfig(Long brokerId) {
    log.info("Retrieving email server config for brokerConfiguration having brokerId {}", brokerId);
    return ResponseEntity.ok(service.getBrokerEmailServerConfig(brokerId));
  }
}
