package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrganizationStationDTO;
import it.gov.pagopa.pu.organization.exception.custom.NotFoundException;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.model.Station;
import it.gov.pagopa.pu.organization.repository.OrganizationStationRepository;
import it.gov.pagopa.pu.organization.repository.StationRepository;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrganizationStationMapper {

  private final OrganizationEncryptionService encryptionService;
  private final OrganizationStationRepository organizationStationRepository;
  private final StationRepository stationRepository;

  public OrganizationStationDTO mapToDTO(Organization org, String stationId) {
    OrganizationStationDTO dto = new OrganizationStationDTO();
    OrganizationStation organizationStation;
    Station station;

    //organization
    dto.setPassword(encryptionService.decryptKey(org.getPassword()));
    dto.setGenerateNoticeApiKey(encryptionService.decryptKey(org.getGenerateNoticeApiKey()));
    dto.setIoApiKey(encryptionService.decryptKey(org.getIoApiKey()));
    dto.setSendApiKey(encryptionService.decryptKey(org.getSendApiKey()));
    dto.setOrganizationId(org.getOrganizationId());
    dto.setExternalOrganizationId(org.getExternalOrganizationId());
    dto.setIpaCode(org.getIpaCode());
    dto.setOrgFiscalCode(org.getOrgFiscalCode());
    dto.setOrgName(org.getOrgName());
    dto.setOrgTypeCode(org.getOrgTypeCode());
    dto.setOrgEmail(org.getOrgEmail());
    dto.setPostalIban(org.getPostalIban());
    dto.setIban(org.getIban());
    dto.setSegregationCode(org.getSegregationCode());
    dto.setCbillInterBankCode(org.getCbillInterBankCode());
    dto.setOrgLogo(org.getOrgLogo());
    dto.setStatus(org.getStatus());
    dto.setAdditionalLanguage(org.getAdditionalLanguage());
    dto.setStartDate(org.getStartDate());
    dto.setFlagNotifyIo(org.isFlagNotifyIo());
    dto.setFlagNotifyOutcomePush(org.isFlagNotifyOutcomePush());
    dto.setFlagPaymentNotification(org.isFlagPaymentNotification());
    dto.setPdndEnabled(org.isPdndEnabled());
    dto.setFlagTreasury(org.isFlagTreasury());
    dto.setFlagPaymentsReporting(org.isFlagPaymentsReporting());
    dto.setFlagClassification(org.isFlagClassification());
    dto.setBrokerId(org.getBrokerId());
    dto.setAddress(org.getAddress());
    dto.setZipCode(org.getZipCode());
    dto.setCity(org.getCity());

    if(Objects.isNull(stationId)) {
      //organizationStation = organizationStationRepository.findById(org.getDefaultOrganizationStationId());
      organizationStation = organizationStationRepository.findById(1L)
        .orElseThrow(() -> new NotFoundException("[ORGANIZATION_STATION_NOT_FOUND]","Relation Organization-Station not found for organizationStationId "));;
      station = stationRepository.findById(organizationStation.getStationId())
        .orElseThrow(() -> new NotFoundException("[STATION_NOT_FOUND]","Station having id "+organizationStation.getStationId()+" not found"));
    }
    else {
      station = stationRepository.findById(stationId)
        .orElseThrow(() -> new NotFoundException("[STATION_NOT_FOUND]","Station having id "+stationId+" not found"));
      organizationStation = organizationStationRepository.findByOrganizationIdAndStationId(org.getOrganizationId(), station.getStationId())
        .orElseThrow(() -> new NotFoundException("[ORGANIZATION_STATION_NOT_FOUND]",
          String.format("Relation Organization-Station not found having orgid %s and stationId %s",org.getOrganizationId(), stationId)));
    }

    dto.setStationId(station.getStationId());
    dto.setPagoPaInteractionModel(station.getPagoPaInteractionModel());
    dto.setBroadcastStationId(station.getBroadcastStationId());
    dto.setEnabled(station.isEnabled());

    dto.setSegregationCode(organizationStation.getSegregationCode());

    return dto;
  }
}
