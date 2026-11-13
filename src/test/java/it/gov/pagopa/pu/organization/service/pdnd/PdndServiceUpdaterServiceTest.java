package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PdndServiceUpdateDTO;
import it.gov.pagopa.pu.organization.exception.common.ConflictException;
import it.gov.pagopa.pu.organization.exception.common.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.common.NotFoundException;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.repository.PdndClientRepository;
import it.gov.pagopa.pu.organization.repository.PdndServiceRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdndServiceUpdaterServiceTest {

  private static final String PURPOSE_ID = "PURPOSE_ID";
  private static final String OLD_CLIENT_ID = "OLD_CLIENT_ID";
  private static final String NEW_CLIENT_ID = "NEW_CLIENT_ID";
  private static final String NEW_NAME = "NEW_NAME";

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private PdndServiceRepository pdndServiceRepositoryMock;
  @Mock
  private PdndClientRepository pdndClientRepositoryMock;

  @InjectMocks
  private PdndServiceUpdaterService service;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(pdndServiceRepositoryMock, pdndClientRepositoryMock);
  }

  @Test
  void whenUpdatePdndServiceThenOk() {
    PdndServiceUpdateDTO updateDTO = new PdndServiceUpdateDTO(NEW_NAME, NEW_CLIENT_ID);
    PdndService pdndService = buildPdndService();
    PdndService expectedResult = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findById(PURPOSE_ID)).thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(OLD_CLIENT_ID)).thenReturn(Optional.of(buildPdndClient(1L, "SUB_01")));
    when(pdndClientRepositoryMock.findById(NEW_CLIENT_ID)).thenReturn(Optional.of(buildPdndClient(1L, "SUB_01")));
    when(pdndServiceRepositoryMock.findByClientIdAndServiceType(NEW_CLIENT_ID, pdndService.getServiceType())).thenReturn(null);
    when(pdndServiceRepositoryMock.save(pdndService)).thenReturn(expectedResult);

    PdndService result = service.updatePdndService(PURPOSE_ID, updateDTO);

    assertSame(expectedResult, result);
    assertEquals(NEW_NAME, pdndService.getServiceName());
    assertEquals(NEW_CLIENT_ID, pdndService.getClientId());
  }

  @Test
  void givenNullSubUnitCodesWhenUpdatePdndServiceThenOk() {
    PdndServiceUpdateDTO updateDTO = new PdndServiceUpdateDTO(NEW_NAME, NEW_CLIENT_ID);
    PdndService pdndService = buildPdndService();
    PdndService expectedResult = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findById(PURPOSE_ID)).thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(OLD_CLIENT_ID)).thenReturn(Optional.of(buildPdndClient(1L, null)));
    when(pdndClientRepositoryMock.findById(NEW_CLIENT_ID)).thenReturn(Optional.of(buildPdndClient(1L, null)));
    when(pdndServiceRepositoryMock.findByClientIdAndServiceType(NEW_CLIENT_ID, pdndService.getServiceType())).thenReturn(null);
    when(pdndServiceRepositoryMock.save(pdndService)).thenReturn(expectedResult);

    PdndService result = service.updatePdndService(PURPOSE_ID, updateDTO);

    assertSame(expectedResult, result);
    assertEquals(NEW_CLIENT_ID, pdndService.getClientId());
  }

  @Test
  void givenSameClientIdWhenUpdatePdndServiceThenOk() {
    PdndServiceUpdateDTO updateDTO = new PdndServiceUpdateDTO(NEW_NAME, OLD_CLIENT_ID);
    PdndService pdndService = buildPdndService();
    PdndService expectedResult = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findById(PURPOSE_ID)).thenReturn(Optional.of(pdndService));
    when(pdndServiceRepositoryMock.save(pdndService)).thenReturn(expectedResult);

    PdndService result = service.updatePdndService(PURPOSE_ID, updateDTO);

    assertSame(expectedResult, result);
    assertEquals(NEW_NAME, pdndService.getServiceName());
    assertEquals(OLD_CLIENT_ID, pdndService.getClientId());
  }

  @Test
  void givenServiceTypeAlreadyUsedByNewClientWhenUpdatePdndServiceThenConflictException() {
    PdndServiceUpdateDTO updateDTO = new PdndServiceUpdateDTO(NEW_NAME, NEW_CLIENT_ID);
    PdndService pdndService = buildPdndService();
    PdndService conflictingService = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findById(PURPOSE_ID)).thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(OLD_CLIENT_ID)).thenReturn(Optional.of(buildPdndClient(1L, "SUB_01")));
    when(pdndClientRepositoryMock.findById(NEW_CLIENT_ID)).thenReturn(Optional.of(buildPdndClient(1L, "SUB_01")));
    when(pdndServiceRepositoryMock.findByClientIdAndServiceType(NEW_CLIENT_ID, pdndService.getServiceType())).thenReturn(conflictingService);

    ConflictException exception = assertThrows(ConflictException.class,
      () -> service.updatePdndService(PURPOSE_ID, updateDTO));

    assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_SERVICE_TYPE, exception.getCode());
  }

  @Test
  void givenNewClientOfOtherSubUnitWhenUpdatePdndServiceThenInvalidValueException() {
    assertInvalidNewClient(buildPdndClient(1L, "SUB_01"), buildPdndClient(1L, "SUB_02"));
  }

  @Test
  void givenNewClientWithSubUnitAndOldClientWithoutWhenUpdatePdndServiceThenInvalidValueException() {
    assertInvalidNewClient(buildPdndClient(1L, null), buildPdndClient(1L, "SUB_01"));
  }

  @Test
  void givenNewClientOfOtherOrganizationWhenUpdatePdndServiceThenInvalidValueException() {
    assertInvalidNewClient(buildPdndClient(1L, "SUB_01"), buildPdndClient(2L, "SUB_01"));
  }

  @Test
  void givenNewClientNotFoundWhenUpdatePdndServiceThenThrowNotFoundException() {
    PdndServiceUpdateDTO updateDTO = new PdndServiceUpdateDTO(NEW_NAME, NEW_CLIENT_ID);

    when(pdndServiceRepositoryMock.findById(PURPOSE_ID)).thenReturn(Optional.of(buildPdndService()));
    when(pdndClientRepositoryMock.findById(OLD_CLIENT_ID)).thenReturn(Optional.of(buildPdndClient(1L, "SUB_01")));
    when(pdndClientRepositoryMock.findById(NEW_CLIENT_ID)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
      () -> service.updatePdndService(PURPOSE_ID, updateDTO));

    assertEquals(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_NOT_FOUND, exception.getCode());
  }

  @Test
  void givenPdndServiceNotFoundWhenUpdatePdndServiceThenThrowNotFoundException() {
    PdndServiceUpdateDTO updateDTO = new PdndServiceUpdateDTO(NEW_NAME, NEW_CLIENT_ID);

    when(pdndServiceRepositoryMock.findById(PURPOSE_ID)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
      () -> service.updatePdndService(PURPOSE_ID, updateDTO));

    assertEquals(ErrorCodeConstants.ERROR_CODE_PDND_SERVICE_NOT_FOUND, exception.getCode());
  }

  private void assertInvalidNewClient(PdndClient oldPdndClient, PdndClient newPdndClient) {
    PdndServiceUpdateDTO updateDTO = new PdndServiceUpdateDTO(NEW_NAME, NEW_CLIENT_ID);

    when(pdndServiceRepositoryMock.findById(PURPOSE_ID)).thenReturn(Optional.of(buildPdndService()));
    when(pdndClientRepositoryMock.findById(OLD_CLIENT_ID)).thenReturn(Optional.of(oldPdndClient));
    when(pdndClientRepositoryMock.findById(NEW_CLIENT_ID)).thenReturn(Optional.of(newPdndClient));

    InvalidValueException exception = assertThrows(InvalidValueException.class,
      () -> service.updatePdndService(PURPOSE_ID, updateDTO));

    assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_CLIENT, exception.getCode());
  }

  private PdndService buildPdndService() {
    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    pdndService.setClientId(OLD_CLIENT_ID);
    return pdndService;
  }

  private PdndClient buildPdndClient(Long organizationId, String subUnitCode) {
    PdndClient pdndClient = podamFactory.manufacturePojo(PdndClient.class);
    pdndClient.setOrganizationId(organizationId);
    pdndClient.setSubUnitCode(subUnitCode);
    return pdndClient;
  }
}
