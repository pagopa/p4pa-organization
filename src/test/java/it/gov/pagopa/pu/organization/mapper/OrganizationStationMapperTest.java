package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrganizationStationDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import it.gov.pagopa.pu.organization.exception.custom.NotFoundException;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.model.Station;
import it.gov.pagopa.pu.organization.repository.OrganizationStationRepository;
import it.gov.pagopa.pu.organization.repository.StationRepository;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import it.gov.pagopa.pu.organization.service.organizationkeys.OrganizationKeysService;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationStationMapperTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private OrganizationEncryptionService encryptionServiceMock;
  @Mock
  private OrganizationStationRepository organizationStationRepositoryMock;
  @Mock
  private StationRepository stationRepositoryMock;
  @Mock
  private OrganizationKeysService organizationKeysServiceMock;

  @InjectMocks
  private OrganizationStationMapper mapper;

  private Organization org;
  private Station station;
  private OrganizationStation organizationStation;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      encryptionServiceMock,
      organizationStationRepositoryMock,
      stationRepositoryMock,
      organizationKeysServiceMock
    );
  }

  @BeforeEach
  void setUp() {
    org = podamFactory.manufacturePojo(Organization.class);
    station = podamFactory.manufacturePojo(Station.class);
    organizationStation = podamFactory.manufacturePojo(OrganizationStation.class);

    organizationStation.setStationId(station.getStationId());
    organizationStation.setOrganizationId(org.getOrganizationId());
  }

  private void stubEncryption() {
    when(encryptionServiceMock.decryptKey(org.getPassword())).thenReturn("decryptedPassword");
    when(organizationKeysServiceMock.getApiKey(org.getOrganizationId(), OrganizationApiKeyType.GENERATE_NOTICE, null)).thenReturn("decryptedGenerateNoticeApiKey");
    when(organizationKeysServiceMock.getApiKey(org.getOrganizationId(), OrganizationApiKeyType.IO, null)).thenReturn("decryptedIoApiKey");
    when(organizationKeysServiceMock.getApiKey(org.getOrganizationId(), OrganizationApiKeyType.SEND, null)).thenReturn("decryptedSendApiKey");
  }

  @Test
  void givenOrgIdAndStationIdNullWhenMapToDTOThenUseDefaultOrganizationStation() {
    stubEncryption();
    Long defaultOrganizationStationId = org.getDefaultOrganizationStationId();

    when(organizationStationRepositoryMock.findById(defaultOrganizationStationId)).thenReturn(Optional.of(organizationStation));
    when(stationRepositoryMock.findById(organizationStation.getStationId())).thenReturn(Optional.of(station));

    OrganizationStationDTO result = mapper.mapToDTO(org, null);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result, "brokerId", "segregationCode", "defaultOrganizationStationId");
    assertOrganizationFields(result);
    assertStationFields(result, station);
    assertThat(result.getSegregationCode()).isEqualTo(organizationStation.getSegregationCode());

    verify(organizationStationRepositoryMock).findById(defaultOrganizationStationId);
    verify(stationRepositoryMock).findById(organizationStation.getStationId());
  }

  @Test
  void givenStationIdAndDefaultOrgStationNotFoundNullWhenMapToDTOThenThrowNotFoundException() {
    stubEncryption();
    Long defaultOrganizationStationId = org.getDefaultOrganizationStationId();

    when(organizationStationRepositoryMock.findById(defaultOrganizationStationId)).thenReturn(Optional.empty());

    // When
    NotFoundException result = assertThrows(NotFoundException.class, () -> mapper.mapToDTO(org, null));

    // Then
    Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_ORGANIZATION_STATION_NOT_FOUND, result.getCode());
    Assertions.assertEquals("Relation Organization-Station not found for organizationStationId %s".formatted(defaultOrganizationStationId), result.getMessage());

    verify(organizationStationRepositoryMock).findById(defaultOrganizationStationId);
    verifyNoInteractions(stationRepositoryMock);
  }

  @Test
  void givenStationIdAndStationNotFoundNullWhenMapToDTOThenThrowNotFoundException() {
    stubEncryption();
    Long defaultOrganizationStationId = org.getDefaultOrganizationStationId();

    when(organizationStationRepositoryMock.findById(defaultOrganizationStationId)).thenReturn(Optional.of(organizationStation));
    when(stationRepositoryMock.findById(organizationStation.getStationId())).thenReturn(Optional.empty());

    // When
    NotFoundException result = assertThrows(NotFoundException.class, () -> mapper.mapToDTO(org, null));

    // Then
    Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_STATION_NOT_FOUND, result.getCode());
    Assertions.assertEquals("Station having id "+organizationStation.getStationId()+" not found", result.getMessage());

    verify(organizationStationRepositoryMock).findById(defaultOrganizationStationId);
  }

  @Test
  void givenStationIdWhenMapToDtoThenUseExplicitStation() {
    String stationId = station.getStationId();
    stubEncryption();
    when(stationRepositoryMock.findById(stationId)).thenReturn(Optional.of(station));
    when(organizationStationRepositoryMock.findByOrganizationIdAndStationId(org.getOrganizationId(), stationId))
      .thenReturn(Optional.of(organizationStation));

    OrganizationStationDTO result = mapper.mapToDTO(org, stationId);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result, "brokerId", "segregationCode", "defaultOrganizationStationId");
    assertOrganizationFields(result);
    assertStationFields(result, station);
    assertThat(result.getSegregationCode()).isEqualTo(organizationStation.getSegregationCode());

    verify(stationRepositoryMock).findById(stationId);
    verify(organizationStationRepositoryMock).findByOrganizationIdAndStationId(org.getOrganizationId(), stationId);
    verify(organizationStationRepositoryMock, never()).findById(anyLong());
  }

  @Test
  void givenStationIdAndStationNotFoundWhenMapToDtoThenThrowNotFoundException() {
    String stationId = station.getStationId();
    stubEncryption();
    when(stationRepositoryMock.findById(stationId)).thenReturn(Optional.empty());

    // When
    NotFoundException result = assertThrows(NotFoundException.class, () -> mapper.mapToDTO(org, stationId));

    // Then
    Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_STATION_NOT_FOUND, result.getCode());
    Assertions.assertEquals("Station having id "+organizationStation.getStationId()+" not found", result.getMessage());

    verify(organizationStationRepositoryMock, never()).findByOrganizationIdAndStationId(any(), any());
  }

  @Test
  void givenStationIdAndOrganizationStationNotFoundWhenMapToDtoThenThrowNotFoundException() {
    String stationId = station.getStationId();
    stubEncryption();
    when(stationRepositoryMock.findById(stationId)).thenReturn(Optional.of(station));
    when(organizationStationRepositoryMock.findByOrganizationIdAndStationId(org.getOrganizationId(), stationId))
      .thenReturn(Optional.empty());

    // When
    NotFoundException result = assertThrows(NotFoundException.class, () -> mapper.mapToDTO(org, stationId));

    // Then
    Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_ORGANIZATION_STATION_NOT_FOUND, result.getCode());
    Assertions.assertEquals("Relation Organization-Station not found having orgid "+org.getOrganizationId()+" and stationId "+stationId,
      result.getMessage());
  }

  private void assertOrganizationFields(OrganizationStationDTO result) {
    Assertions.assertEquals(org.getOrganizationId(), result.getOrganizationId());
    Assertions.assertEquals(org.getExternalOrganizationId(), result.getExternalOrganizationId());
    Assertions.assertEquals(org.getIpaCode(), result.getIpaCode());
    Assertions.assertEquals(org.getOrgFiscalCode(), result.getOrgFiscalCode());
    Assertions.assertEquals(org.getOrgName(), result.getOrgName());
    Assertions.assertEquals(org.getOrgTypeCode(), result.getOrgTypeCode());
    Assertions.assertEquals(org.getOrgEmail(), result.getOrgEmail());
    Assertions.assertEquals(org.getPostalIban(), result.getPostalIban());
    Assertions.assertEquals(org.getIban(), result.getIban());
    Assertions.assertEquals(org.getCbillInterBankCode(), result.getCbillInterBankCode());
    Assertions.assertEquals(org.getOrgLogo(), result.getOrgLogo());
    Assertions.assertEquals(org.getStatus(), result.getStatus());
    Assertions.assertEquals(org.getAdditionalLanguage(), result.getAdditionalLanguage());
    Assertions.assertEquals(org.getStartDate(), result.getStartDate());
    Assertions.assertEquals(org.isFlagNotifyIo(), result.getFlagNotifyIo());
    Assertions.assertEquals(org.isFlagNotifyOutcomePush(), result.getFlagNotifyOutcomePush());
    Assertions.assertEquals(org.isFlagPaymentNotification(), result.getFlagPaymentNotification());
    Assertions.assertEquals(org.isPdndEnabled(), result.getPdndEnabled());
    Assertions.assertEquals(org.isFlagTreasury(), result.getFlagTreasury());
    Assertions.assertEquals(org.isFlagPaymentsReporting(), result.getFlagPaymentsReporting());
    Assertions.assertEquals(org.isFlagClassification(), result.getFlagClassification());
    Assertions.assertEquals(org.getBrokerId(), result.getBrokerId());
    Assertions.assertEquals(org.getAddress(), result.getAddress());
    Assertions.assertEquals(org.getZipCode(), result.getZipCode());
    Assertions.assertEquals(org.getCity(), result.getCity());
  }

  private void assertStationFields(OrganizationStationDTO result, Station expectedStation) {
    Assertions.assertEquals(expectedStation.getStationId(), result.getStationId());
    Assertions.assertEquals(expectedStation.getPagoPaInteractionModel(), result.getPagoPaInteractionModel());
    Assertions.assertEquals(expectedStation.getBroadcastStationId(), result.getBroadcastStationId());
    Assertions.assertEquals(expectedStation.isEnabled(), result.getEnabled());
  }

}
