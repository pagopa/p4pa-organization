package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.BrokerRequestDTO;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.service.broker.BrokerEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerMapper {

  private final BrokerEncryptionService brokerEncryptionService;

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
    broker.setSyncPaymentsReportingKey(requestDTO.getSyncPaymentsReportingKey()!=null ? brokerEncryptionService.encryptKey(requestDTO.getSyncPaymentsReportingKey()) : null);
    broker.setSyncKey(requestDTO.getSyncKey()!=null ? brokerEncryptionService.encryptKey(requestDTO.getSyncKey()) : null);
    broker.setGpdKey(requestDTO.getGpdKey()!=null ? brokerEncryptionService.encryptKey(requestDTO.getGpdKey()) : null);
    broker.setGenerateNoticeKey(requestDTO.getGenerateNoticeKey()!=null ? brokerEncryptionService.encryptKey(requestDTO.getGenerateNoticeKey()) : null);
    broker.setAcaKey(requestDTO.getAcaKey()!=null ? brokerEncryptionService.encryptKey(requestDTO.getAcaKey()) : null);
    broker.setFlagDelegate(requestDTO.getFlagDelegate());
    broker.setFlagPaymentsReporting(requestDTO.getFlagPaymentsReporting());
    broker.setExternalId(requestDTO.getExternalId());
    broker.setIuvSystemId(requestDTO.getIuvSystemId());

    return broker;
  }

}
