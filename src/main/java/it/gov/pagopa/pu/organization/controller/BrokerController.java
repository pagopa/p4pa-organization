package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.BrokerApi;
import it.gov.pagopa.pu.organization.dto.generated.*;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.service.broker.BrokerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BrokerController implements BrokerApi {

  private final BrokerService brokerService;

  public BrokerController(BrokerService brokerService){
    this.brokerService = brokerService;
  }

  @Override
  public ResponseEntity<BrokerApiKeys> getBrokerApiKeys(Long brokerId) {
    log.info("invoking getBrokerApiKeys, brokerId[{}]", brokerId);
    return ResponseEntity.ofNullable(brokerService.getBrokerApiKeys(brokerId));
  }

  @Override
  public ResponseEntity<Void> encryptAndSaveBrokerApiKey(Long brokerId, BrokerApiKey brokerApiKey) {
    log.info("Storing new ApiKey {} for brokerId {}", brokerApiKey.getKeyType(), brokerId);
    brokerService.encryptAndSaveApiKey(brokerId, brokerApiKey);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<String> getBrokerApiKey(Long brokerId, BrokerApiKeyType keyType) {
    log.info("invoking getBrokerApiKey, brokerId[{}], keyType[{}]", brokerId, keyType);
    String apiKey = brokerService.getBrokerApiKey(brokerId, keyType);
    return apiKey != null
      ? ResponseEntity.ok(apiKey)
      : ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Broker> createBroker(BrokerRequestDTO brokerRequestDTO) {
    log.info("Requested broker creation with CF {} and organizationId {}", brokerRequestDTO.getBrokerFiscalCode(), brokerRequestDTO.getOrganizationId());
    return ResponseEntity.ok(brokerService.createBroker(brokerRequestDTO));
  }
}
