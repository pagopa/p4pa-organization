package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.service.brokerkeys.BrokerKeysService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import it.gov.pagopa.pu.organization.util.Constants;


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

    return broker;
  }

}
