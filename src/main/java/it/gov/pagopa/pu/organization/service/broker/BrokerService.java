package it.gov.pagopa.pu.organization.service.broker;

import it.gov.pagopa.pu.organization.dto.generated.*;
import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.mapper.BrokerMapper;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.service.station.StationService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BrokerService {

  private final BrokerRepository brokerRepository;
  private final BrokerEncryptionService brokerEncryptionService;
  private final BrokerMapper brokerMapper;
  private final StationService stationService;

  public BrokerService(
    BrokerRepository brokerRepository,
    BrokerEncryptionService brokerEncryptionService, BrokerMapper brokerMapper, StationService stationService) {
    this.brokerEncryptionService = brokerEncryptionService;
    this.brokerRepository = brokerRepository;
    this.brokerMapper = brokerMapper;
    this.stationService = stationService;
  }

  public BrokerApiKeys getBrokerApiKeys(Long brokerId) {
    Broker broker = getBrokerById(brokerId);
    return brokerEncryptionService.getBrokerDecryptedApiKeys(broker);
  }

  public void encryptAndSaveApiKey(Long brokerId, BrokerApiKey brokerApiKey) {
    Broker broker = getBrokerById(brokerId);
    byte[] encryptedKey = brokerEncryptionService.encryptKey(brokerApiKey.getApiKey());
    switch (brokerApiKey.getKeyType()) {
      case SYNC_PAYMENTS_REPORTING -> broker.setSyncPaymentsReportingKey(encryptedKey);
      case SYNC -> broker.setSyncKey(encryptedKey);
      case ACA -> broker.setAcaKey(encryptedKey);
      case GPD -> broker.setGpdKey(encryptedKey);
      case GENERATE_NOTICE -> broker.setGenerateNoticeKey(encryptedKey);
    }
    brokerRepository.save(broker);
  }

  public String getBrokerApiKey(Long brokerId, BrokerApiKeyType keyType) {
    Broker broker = getBrokerById(brokerId);
    return brokerEncryptionService.getBrokerDecryptedApiKey(broker, keyType);
  }

  @Transactional
  public Broker createBroker(BrokerRequestDTO brokerRequestDTO) {
    Broker broker = brokerMapper.toModel(brokerRequestDTO);
    broker.setDefaultStationId(null);
    broker = brokerRepository.save(broker);

    broker.setDefaultStationId(brokerRequestDTO.getDefaultStationId());
    stationService.upsertStation(broker);

    return brokerRepository.save(broker);
  }

  private @NonNull Broker getBrokerById(Long brokerId) {
    return brokerRepository.findById(brokerId).orElseThrow(() -> new BrokerNotFoundException("broker [%s]".formatted(brokerId)));
  }
}
