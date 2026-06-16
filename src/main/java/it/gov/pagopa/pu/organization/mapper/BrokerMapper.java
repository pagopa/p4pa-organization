package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKey;
import it.gov.pagopa.pu.organization.dto.generated.BrokerApiKeyType;
import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.service.brokerkeys.BrokerKeysService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import it.gov.pagopa.pu.organization.util.Constants;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BrokerMapper {

  private final BrokerKeysService brokerKeysService;

  public Broker toModel(BrokerRequestDTO requestDTO) {
    if (requestDTO == null) {
      return null;
    }

    Broker broker = new Broker();
    broker.setBrokerId(requestDTO.getBrokerId());
    broker.setOrganizationId(requestDTO.getOrganizationId());
    broker.setBrokerFiscalCode(requestDTO.getBrokerFiscalCode());
    broker.setBrokerName(requestDTO.getBrokerName());
    broker.setDefaultStationId(requestDTO.getDefaultStationId());
    broker.setFlagDelegate(requestDTO.getFlagDelegate());
    broker.setFlagPaymentsReporting(requestDTO.getFlagPaymentsReporting());
    broker.setExternalId(requestDTO.getExternalId());
    broker.setIuvSystemId(requestDTO.getIuvSystemId()!=null ? requestDTO.getIuvSystemId() : Constants.DEFAULT_IUV_SYSTEM_ID);

    saveKey(requestDTO.getBrokerId(), BrokerApiKeyType.SYNC, requestDTO.getSyncKey());
    saveKey(requestDTO.getBrokerId(), BrokerApiKeyType.ACA, requestDTO.getAcaKey());
    saveKey(requestDTO.getBrokerId(), BrokerApiKeyType.GPD, requestDTO.getGpdKey());
    saveKey(requestDTO.getBrokerId(), BrokerApiKeyType.GENERATE_NOTICE, requestDTO.getGenerateNoticeKey());
    saveKey(requestDTO.getBrokerId(), BrokerApiKeyType.SYNC_PAYMENTS_REPORTING, requestDTO.getSyncPaymentsReportingKey());

    return broker;
  }

  private void saveKey(Long brokerId, BrokerApiKeyType type, String key) {
    if(!Objects.isNull(key))
      brokerKeysService.encryptAndSaveApiKey(brokerId, new BrokerApiKey(type, key));
  }

}
