package it.gov.pagopa.pu.organization.service;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeys;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrokerService {

  private final BrokerRepository brokerRepository;
  private final BrokerEncryptionService brokerEncryptionService;

  public BrokerService(
    BrokerRepository brokerRepository,
    BrokerEncryptionService brokerEncryptionService) {
    this.brokerEncryptionService = brokerEncryptionService;
    this.brokerRepository = brokerRepository;
  }

  public BrokerApiKeys getBrokerApiKeys(Long brokerId){
    Broker broker = brokerRepository.findById(brokerId).orElseThrow(() -> new ResourceNotFoundException("broker [%s]".formatted(brokerId)));
    return brokerEncryptionService.getBrokerDecryptedApiKeys(broker);
  }

}
