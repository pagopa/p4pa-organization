package it.gov.pagopa.pu.organization.service.broker;

import it.gov.pagopa.pu.organization.dto.generated.*;
import it.gov.pagopa.pu.organization.mapper.BrokerMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.service.brokerkeys.BrokerKeysService;
import it.gov.pagopa.pu.organization.service.station.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BrokerService {

  private final BrokerRepository brokerRepository;
  private final BrokerMapper brokerMapper;
  private final StationService stationService;
  private final BrokerKeysService brokerKeysService;

  public BrokerService(
    BrokerRepository brokerRepository, BrokerMapper brokerMapper,
    StationService stationService, BrokerKeysService brokerKeysService) {
    this.brokerRepository = brokerRepository;
    this.brokerMapper = brokerMapper;
    this.stationService = stationService;
    this.brokerKeysService = brokerKeysService;
  }

  public BrokerApiKeys getBrokerApiKeys(Long brokerId) {
    return brokerKeysService.getBrokerDecryptedApiKeys(brokerId);
  }

  public void encryptAndSaveApiKey(Long brokerId, BrokerApiKey brokerApiKey) {
    brokerKeysService.encryptAndSaveApiKey(brokerId, brokerApiKey);
  }

  public String getBrokerApiKey(Long brokerId, BrokerApiKeyType keyType) {
    return brokerKeysService.getBrokerDecryptedApiKey(brokerId, keyType);
  }

  @Transactional
  public Broker createBroker(BrokerRequestDTO brokerRequestDTO) {
    Broker broker = brokerMapper.toModel(brokerRequestDTO);
    broker.setDefaultStationId(null);
    broker = brokerRepository.save(broker);

    brokerRequestDTO.setBrokerId(broker.getBrokerId());
    stationService.upsertStation(brokerRequestDTO);

    broker.setDefaultStationId(brokerRequestDTO.getDefaultStationId());
    return brokerRepository.save(broker);
  }
}
