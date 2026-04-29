package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrganizationStationDTO;
import it.gov.pagopa.pu.organization.exception.custom.NotFoundException;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.model.Station;
import it.gov.pagopa.pu.organization.repository.OrganizationStationRepository;
import it.gov.pagopa.pu.organization.repository.StationRepository;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
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
    OrganizationStationDTO orgStationDto = new OrganizationStationDTO();
    OrganizationStation organizationStation;
    Station station;

    //organization
    orgStationDto.setPassword(encryptionService.decryptKey(org.getPassword()));
    orgStationDto.setGenerateNoticeApiKey(encryptionService.decryptKey(org.getGenerateNoticeApiKey()));
    orgStationDto.setIoApiKey(encryptionService.decryptKey(org.getIoApiKey()));
    orgStationDto.setSendApiKey(encryptionService.decryptKey(org.getSendApiKey()));
    orgStationDto.setOrganizationId(org.getOrganizationId());
    orgStationDto.setExternalOrganizationId(org.getExternalOrganizationId());
    orgStationDto.setIpaCode(org.getIpaCode());
    orgStationDto.setOrgFiscalCode(org.getOrgFiscalCode());
    orgStationDto.setOrgName(org.getOrgName());
    orgStationDto.setOrgTypeCode(org.getOrgTypeCode());
    orgStationDto.setOrgEmail(org.getOrgEmail());
    orgStationDto.setPostalIban(org.getPostalIban());
    orgStationDto.setIban(org.getIban());
    orgStationDto.setSegregationCode(org.getSegregationCode());
    orgStationDto.setCbillInterBankCode(org.getCbillInterBankCode());
    orgStationDto.setOrgLogo(org.getOrgLogo());
    orgStationDto.setStatus(org.getStatus());
    orgStationDto.setAdditionalLanguage(org.getAdditionalLanguage());
    orgStationDto.setStartDate(org.getStartDate());
    orgStationDto.setFlagNotifyIo(org.isFlagNotifyIo());
    orgStationDto.setFlagNotifyOutcomePush(org.isFlagNotifyOutcomePush());
    orgStationDto.setFlagPaymentNotification(org.isFlagPaymentNotification());
    orgStationDto.setPdndEnabled(org.isPdndEnabled());
    orgStationDto.setFlagTreasury(org.isFlagTreasury());
    orgStationDto.setFlagPaymentsReporting(org.isFlagPaymentsReporting());
    orgStationDto.setFlagClassification(org.isFlagClassification());
    orgStationDto.setBrokerId(org.getBrokerId());
    orgStationDto.setAddress(org.getAddress());
    orgStationDto.setZipCode(org.getZipCode());
    orgStationDto.setCity(org.getCity());

    if(Objects.isNull(stationId)) {
      organizationStation = organizationStationRepository.findById(1L) // TODO change ID with org.getDefaultOrganizationStationId()
        .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_ORGANIZATION_STATION_NOT_FOUND,
          String.format("Relation Organization-Station not found for organizationStationId %s", 1L)));;
      station = stationRepository.findById(organizationStation.getStationId())
        .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_STATION_NOT_FOUND,
          "Station having id "+organizationStation.getStationId()+" not found"));
    }
    else {
      station = stationRepository.findById(stationId)
        .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_STATION_NOT_FOUND,"Station having id "+stationId+" not found"));
      organizationStation = organizationStationRepository.findByOrganizationIdAndStationId(org.getOrganizationId(), station.getStationId())
        .orElseThrow(() -> new NotFoundException(ErrorCodeConstants.ERROR_CODE_ORGANIZATION_STATION_NOT_FOUND,
          String.format("Relation Organization-Station not found having orgid %s and stationId %s",org.getOrganizationId(), stationId)));
    }

    orgStationDto.setStationId(station.getStationId());
    orgStationDto.setPagoPaInteractionModel(station.getPagoPaInteractionModel());
    orgStationDto.setBroadcastStationId(station.getBroadcastStationId());
    orgStationDto.setEnabled(station.isEnabled());

    orgStationDto.setSegregationCode(organizationStation.getSegregationCode());

    return orgStationDto;
  }
}
