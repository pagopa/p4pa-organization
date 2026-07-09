package it.gov.pagopa.pu.organization.service.organizationstation;

import it.gov.pagopa.pu.organization.exception.custom.BrokerNotFoundException;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.custom.NotFoundException;
import it.gov.pagopa.pu.organization.model.Broker;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.repository.BrokerRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationStationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultOrganizationStationServiceTest {
  @Mock
  private BrokerRepository brokerRepositoryMock;
  @Mock
  private OrganizationStationRepository organizationStationRepositoryMock;

  @InjectMocks
  private DefaultOrganizationStationService defaultOrganizationStationService;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(brokerRepositoryMock, organizationStationRepositoryMock);
  }

  @Test
  void givenNewOrganizationStationWhenCreateOrUpdateDefaultOrganizationStationThenCreate() {
    Long organizationId = 1L;
    Long brokerId = 1L;
    String segregationCode = "01";
    String defaultStationId = "defaultStationId";

    Broker broker = new Broker();
    broker.setDefaultStationId(defaultStationId);

    OrganizationStation organizationStation = new OrganizationStation();
    organizationStation.setStationId(defaultStationId);
    organizationStation.setSegregationCode(segregationCode);
    organizationStation.setOrganizationId(organizationId);

    when(brokerRepositoryMock.findById(brokerId)).thenReturn(Optional.of(broker));
    when(organizationStationRepositoryMock.findByOrganizationIdAndStationId(organizationId, defaultStationId))
      .thenReturn(Optional.empty());
    when(organizationStationRepositoryMock.save(organizationStation)).thenReturn(organizationStation);

    OrganizationStation result = defaultOrganizationStationService.createOrUpdateDefaultOrganizationStation(organizationId, brokerId, segregationCode);

    assertNotNull(result);
    assertEquals(defaultStationId, result.getStationId());
    assertEquals(segregationCode, result.getSegregationCode());
    assertEquals(organizationId, result.getOrganizationId());
  }

  @Test
  void givenExistingOrganizationStationWhenCreateOrUpdateDefaultOrganizationStationThenUpdate() {
    Long organizationId = 1L;
    Long brokerId = 1L;
    String segregationCode = "01";
    String defaultStationId = "defaultStationId";

    Broker broker = new Broker();
    broker.setDefaultStationId(defaultStationId);

    OrganizationStation existingOrganizationStation = new OrganizationStation();
    existingOrganizationStation.setStationId(defaultStationId);
    existingOrganizationStation.setSegregationCode("oldSegregationCode");
    existingOrganizationStation.setOrganizationId(organizationId);

    when(brokerRepositoryMock.findById(brokerId)).thenReturn(Optional.of(broker));
    when(organizationStationRepositoryMock.findByOrganizationIdAndStationId(organizationId, defaultStationId))
      .thenReturn(Optional.of(existingOrganizationStation));
    when(organizationStationRepositoryMock.save(existingOrganizationStation)).thenReturn(existingOrganizationStation);

    OrganizationStation result = defaultOrganizationStationService.createOrUpdateDefaultOrganizationStation(organizationId, brokerId, segregationCode);

    assertNotNull(result);
    assertEquals(defaultStationId, result.getStationId());
    assertEquals(segregationCode, result.getSegregationCode());
    assertEquals(organizationId, result.getOrganizationId());
  }

  @Test
  void givenNonExistingBrokerWhenCreateOrUpdateDefaultOrganizationStationThenThrowException() {
    Long organizationId = 1L;
    Long brokerId = 1L;
    String segregationCode = "01";

    when(brokerRepositoryMock.findById(brokerId)).thenReturn(Optional.empty());

    assertThrows(BrokerNotFoundException.class, () ->
      defaultOrganizationStationService.createOrUpdateDefaultOrganizationStation(organizationId, brokerId, segregationCode)
    );
  }

  @Test
  void givenExistingOrganizationStationWhenUpdateDefaultOrganizationStationSegregationCodeThenOk() {
    Long orgId = 1L;
    Long organizationStationId = 1L;
    String newSegregationCode = "02";

    OrganizationStation existingStation = new OrganizationStation();
    existingStation.setSegregationCode("01");
    existingStation.setOrganizationId(orgId);

    when(organizationStationRepositoryMock.findById(organizationStationId)).thenReturn(Optional.of(existingStation));
    when(organizationStationRepositoryMock.save(existingStation)).thenReturn(existingStation);

    defaultOrganizationStationService.updateDefaultOrganizationStationSegregationCode(organizationStationId, orgId, newSegregationCode);

    assertEquals(newSegregationCode, existingStation.getSegregationCode());
  }

  @Test
  void givenMismatchedOrganizationIdWhenUpdateDefaultOrganizationStationSegregationCodeThenThrowException() {
    Long orgId = 1L;
    Long organizationStationId = 1L;
    String newSegregationCode = "02";

    OrganizationStation existingStation = new OrganizationStation();
    existingStation.setSegregationCode("01");
    existingStation.setOrganizationId(2L);

    when(organizationStationRepositoryMock.findById(organizationStationId)).thenReturn(Optional.of(existingStation));

    assertThrows(InvalidValueException.class, () ->
      defaultOrganizationStationService.updateDefaultOrganizationStationSegregationCode(organizationStationId, orgId, newSegregationCode)
    );
  }

  @Test
  void givenNonExistingOrganizationStationWhenUpdateDefaultOrganizationStationSegregationCodeThenThrowException() {
    Long orgId = 1L;
    Long organizationStationId = 1L;
    String newSegregationCode = "02";

    when(organizationStationRepositoryMock.findById(organizationStationId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () ->
      defaultOrganizationStationService.updateDefaultOrganizationStationSegregationCode(organizationStationId, orgId, newSegregationCode)
    );
  }
}
