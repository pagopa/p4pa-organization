package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.BrokerApi;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.service.BrokerService;
import lombok.extern.slf4j.Slf4j;
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
    return ResponseEntity.ofNullable(brokerService.getBrokerApiKeys(brokerId));
  }

}
