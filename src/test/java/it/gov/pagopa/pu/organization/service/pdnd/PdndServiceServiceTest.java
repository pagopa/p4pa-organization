package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PagedPdndServiceView;
import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceUpdateDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.exception.common.ConflictException;
import it.gov.pagopa.pu.organization.exception.common.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.common.NotFoundException;
import it.gov.pagopa.pu.organization.mapper.PdndServiceMapper;
import it.gov.pagopa.pu.organization.mapper.PdndServiceViewMapper;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.model.view.PdndServiceView;
import it.gov.pagopa.pu.organization.repository.PdndClientRepository;
import it.gov.pagopa.pu.organization.repository.PdndServiceRepository;
import it.gov.pagopa.pu.organization.repository.view.PdndServiceViewRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdndServiceServiceTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private PdndServiceRepository pdndServiceRepositoryMock;
  @Mock
  private PdndServiceViewRepository pdndServiceViewRepositoryMock;
  @Mock
  private PdndServiceMapper pdndServiceMapperMock;
  @Mock
  private PdndClientService pdndClientServiceMock;
  @Mock
  private PdndServiceViewMapper pdndServiceViewMapper;
  @Mock
  private PdndClientRepository pdndClientRepositoryMock;

  @InjectMocks
  private PdndServiceService service;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      pdndServiceRepositoryMock,
      pdndServiceMapperMock,
      pdndClientServiceMock,
      pdndServiceViewRepositoryMock,
      pdndServiceViewMapper,
      pdndClientRepositoryMock
    );
  }

  @Test
  void whenSavePdndServiceThenOk() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_01";
    PdndServiceRequestDTO requestDTO = podamFactory.manufacturePojo(PdndServiceRequestDTO.class);
    PdndService mappedService = podamFactory.manufacturePojo(PdndService.class);
    PdndService expectedResult = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndServiceTypeAndSubUnitCode(
      organizationId, requestDTO.getServiceType(), subUnitCode
    )).thenReturn(Collections.emptyList());

    when(pdndClientServiceMock.getUsablePdndClientByOrganizationIdAndPdndServiceType(
      organizationId, requestDTO.getServiceType(), subUnitCode
    )).thenThrow(new NotFoundException(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_NOT_FOUND, "Not found"));

    when(pdndServiceMapperMock.toModel(requestDTO)).thenReturn(mappedService);
    when(pdndServiceRepositoryMock.save(mappedService)).thenReturn(expectedResult);

    PdndService result = service.savePdndService(organizationId, requestDTO, subUnitCode);

    assertSame(expectedResult, result);
  }

  @Test
  void givenAlreadyExistingServiceTypeWhenSavePdndServiceThenConflictException() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_01";
    PdndServiceRequestDTO requestDTO = podamFactory.manufacturePojo(PdndServiceRequestDTO.class);
    PdndService existingService = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndServiceTypeAndSubUnitCode(
      organizationId, requestDTO.getServiceType(), subUnitCode
    )).thenReturn(List.of(existingService));

    ConflictException exception = Assertions.assertThrows(
      ConflictException.class,
      () -> service.savePdndService(organizationId, requestDTO, subUnitCode)
    );

    assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_SERVICE_TYPE, exception.getCode());
  }

  @Test
  void givenUsablePdndClientFoundWhenSavePdndServiceThenInvalidValueException() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_01";
    PdndServiceRequestDTO requestDTO = podamFactory.manufacturePojo(PdndServiceRequestDTO.class);
    PdndClientDTO pdndClientDTO = podamFactory.manufacturePojo(PdndClientDTO.class);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndServiceTypeAndSubUnitCode(
      organizationId, requestDTO.getServiceType(), subUnitCode
    )).thenReturn(Collections.emptyList());

    when(pdndClientServiceMock.getUsablePdndClientByOrganizationIdAndPdndServiceType(
      organizationId, requestDTO.getServiceType(), subUnitCode
    )).thenReturn(pdndClientDTO);

    InvalidValueException exception = assertThrows(
      InvalidValueException.class,
      () -> service.savePdndService(organizationId, requestDTO, subUnitCode)
    );

    assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_SERVICE_TYPE, exception.getCode());
  }

  @Test
  void givenOrganizationIdWhenGetPdndServicesByOrganizationIdAndSubUnitCodeThenOk() {
    Long organizationId = 1L;
    String subUnitCode = "subUnitCode";

    PdndServiceView expectedResponse = podamFactory.manufacturePojo(PdndServiceView.class);

    when(pdndServiceViewRepositoryMock.findByOrganizationIdAndServiceTypeAndSubUnitCode(organizationId, PdndServiceType.SEND, subUnitCode))
      .thenReturn(List.of(expectedResponse));

    List<PdndServiceView> result = service.getPdndServices(organizationId, PdndServiceType.SEND, subUnitCode);

    assertEquals(List.of(expectedResponse), result);
  }

  @Test
  void whenGetPdndServiceThenOk() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";

    PdndServiceView expectedResult = podamFactory.manufacturePojo(PdndServiceView.class);

    when(pdndServiceViewRepositoryMock.findByOrganizationIdAndPurposeId(organizationId, purposeId))
      .thenReturn(Optional.of(expectedResult));

    PdndServiceView result = service.getPdndService(organizationId, purposeId);

    assertSame(expectedResult, result);
  }

  @Test
  void givenPdndServiceNotFoundWhenGetPdndServiceThenThrowNotFoundException() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";

    when(pdndServiceViewRepositoryMock.findByOrganizationIdAndPurposeId(organizationId, purposeId))
      .thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(
      NotFoundException.class,
      () -> service.getPdndService(organizationId, purposeId));

    assertEquals(ErrorCodeConstants.ERROR_CODE_PDND_SERVICE_NOT_FOUND, exception.getCode());
  }

  @Test
  void whenDeletePdndServiceThenOk() {
    String purposeId = "PURPOSE_ID";

    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findById(purposeId))
      .thenReturn(Optional.of(pdndService));

    assertDoesNotThrow(() -> service.deletePdndService(purposeId));
    verify(pdndServiceRepositoryMock).delete(pdndService);
  }

  @Test
  void givenPdndServiceNotFoundWhenDeletePdndServiceThenThrowNotFoundException() {
    String purposeId = "PURPOSE_ID";

    when(pdndServiceRepositoryMock.findById(purposeId))
      .thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> service.deletePdndService(purposeId));

    assertEquals(ErrorCodeConstants.ERROR_CODE_PDND_SERVICE_NOT_FOUND, exception.getCode());
  }

  @Test
  void whenGetOrgSubUnitsPdndServicesThenOk() {
    Long organizationId = 1L;
    String subUnitCode = "CODE";
    String subUnitName = "NAME";
    Pageable pageable = Pageable.ofSize(5);

    Page<PdndServiceView> pdndServiceViews = new PageImpl<>(List.of(podamFactory.manufacturePojo(PdndServiceView.class)));
    PagedPdndServiceView expectedResult = podamFactory.manufacturePojo(PagedPdndServiceView.class);

    when(pdndServiceViewRepositoryMock.findAllPdndServicesByOrganizationIdAndOrgSubUnits(organizationId, subUnitCode, subUnitName, pageable))
      .thenReturn(pdndServiceViews);
    when(pdndServiceViewMapper.mapToPagedPdndServiceView(pdndServiceViews)).thenReturn(expectedResult);

    PagedPdndServiceView result = service.getOrgSubUnitsPdndServices(organizationId, subUnitCode, subUnitName, pageable);

    assertSame(expectedResult, result);
  }

  @Test
  void givenNewClientWhenUpdatePdndServiceThenOk() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";
    String subUnitCode = "SUB_01";
    PdndServiceUpdateDTO updateDTO = podamFactory.manufacturePojo(PdndServiceUpdateDTO.class);
    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    pdndService.setClientId("oldClientId");
    PdndClient newPdndClient = podamFactory.manufacturePojo(PdndClient.class);
    newPdndClient.setOrganizationId(organizationId);
    newPdndClient.setSubUnitCode(subUnitCode);
    PdndService expectedResult = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, subUnitCode))
      .thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(updateDTO.getClientId())).thenReturn(Optional.of(newPdndClient));
    when(pdndServiceRepositoryMock.findByClientIdAndServiceType(updateDTO.getClientId(), pdndService.getServiceType()))
      .thenReturn(null);
    doNothing().when(pdndServiceMapperMock).updateModel(pdndService, updateDTO);
    when(pdndServiceRepositoryMock.save(pdndService)).thenReturn(expectedResult);

    PdndService result = service.updatePdndService(organizationId, purposeId, updateDTO, subUnitCode);

    assertSame(expectedResult, result);
  }

  @Test
  void givenNewClientAndNullSubUnitCodeWhenUpdatePdndServiceThenOk() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";
    PdndServiceUpdateDTO updateDTO = podamFactory.manufacturePojo(PdndServiceUpdateDTO.class);
    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    pdndService.setClientId("oldClientId");
    PdndClient newPdndClient = podamFactory.manufacturePojo(PdndClient.class);
    newPdndClient.setOrganizationId(organizationId);
    newPdndClient.setSubUnitCode(null);
    PdndService expectedResult = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, null))
      .thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(updateDTO.getClientId())).thenReturn(Optional.of(newPdndClient));
    when(pdndServiceRepositoryMock.findByClientIdAndServiceType(updateDTO.getClientId(), pdndService.getServiceType()))
      .thenReturn(null);
    doNothing().when(pdndServiceMapperMock).updateModel(pdndService, updateDTO);
    when(pdndServiceRepositoryMock.save(pdndService)).thenReturn(expectedResult);

    PdndService result = service.updatePdndService(organizationId, purposeId, updateDTO, null);

    assertSame(expectedResult, result);
  }

  @Test
  void givenSameClientWhenUpdatePdndServiceThenOk() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";
    String subUnitCode = "SUB_01";
    PdndServiceUpdateDTO updateDTO = podamFactory.manufacturePojo(PdndServiceUpdateDTO.class);
    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    pdndService.setClientId(updateDTO.getClientId());
    PdndService expectedResult = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, subUnitCode))
      .thenReturn(Optional.of(pdndService));
    doNothing().when(pdndServiceMapperMock).updateModel(pdndService, updateDTO);
    when(pdndServiceRepositoryMock.save(pdndService)).thenReturn(expectedResult);

    PdndService result = service.updatePdndService(organizationId, purposeId, updateDTO, subUnitCode);

    assertSame(expectedResult, result);
  }

  @Test
  void givenNewClientAndServiceTypeAlreadyConfiguredWhenUpdatePdndServiceThenConflictException() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";
    String subUnitCode = "SUB_01";
    PdndServiceUpdateDTO updateDTO = podamFactory.manufacturePojo(PdndServiceUpdateDTO.class);
    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    pdndService.setClientId("oldClientId");
    PdndClient newPdndClient = podamFactory.manufacturePojo(PdndClient.class);
    newPdndClient.setOrganizationId(organizationId);
    newPdndClient.setSubUnitCode(subUnitCode);
    PdndService conflictingService = podamFactory.manufacturePojo(PdndService.class);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, subUnitCode))
      .thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(updateDTO.getClientId())).thenReturn(Optional.of(newPdndClient));
    when(pdndServiceRepositoryMock.findByClientIdAndServiceType(updateDTO.getClientId(), pdndService.getServiceType()))
      .thenReturn(conflictingService);

    ConflictException exception = assertThrows(ConflictException.class,
      () -> service.updatePdndService(organizationId, purposeId, updateDTO, subUnitCode));

    assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_SERVICE_TYPE, exception.getCode());
  }

  @Test
  void givenNewClientAndNoMatchingSubUnitWhenUpdatePdndServiceThenInvalidValueException() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";
    String subUnitCode = "SUB_01";
    PdndServiceUpdateDTO updateDTO = podamFactory.manufacturePojo(PdndServiceUpdateDTO.class);
    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    pdndService.setClientId("oldClientId");
    PdndClient newPdndClient = podamFactory.manufacturePojo(PdndClient.class);
    newPdndClient.setOrganizationId(organizationId);
    newPdndClient.setSubUnitCode("SUB_02");

    when(pdndServiceRepositoryMock.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, subUnitCode))
      .thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(updateDTO.getClientId())).thenReturn(Optional.of(newPdndClient));

    InvalidValueException exception = assertThrows(InvalidValueException.class,
      () -> service.updatePdndService(organizationId, purposeId, updateDTO, subUnitCode));

    assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_CLIENT, exception.getCode());
  }

  @Test
  void givenNewClientAndNoSubUnitCodeWhenUpdatePdndServiceThenInvalidValueException() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";
    PdndServiceUpdateDTO updateDTO = podamFactory.manufacturePojo(PdndServiceUpdateDTO.class);
    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    pdndService.setClientId("oldClientId");
    PdndClient newPdndClient = podamFactory.manufacturePojo(PdndClient.class);
    newPdndClient.setOrganizationId(organizationId);
    newPdndClient.setSubUnitCode("SUB_01");

    when(pdndServiceRepositoryMock.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, null))
      .thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(updateDTO.getClientId())).thenReturn(Optional.of(newPdndClient));

    InvalidValueException exception = assertThrows(InvalidValueException.class,
      () -> service.updatePdndService(organizationId, purposeId, updateDTO, null));

    assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_CLIENT, exception.getCode());
  }

  @Test
  void givenNewClientAndNoMatchingOrgIdWhenUpdatePdndServiceThenInvalidValueException() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";
    String subUnitCode = "SUB_01";
    PdndServiceUpdateDTO updateDTO = podamFactory.manufacturePojo(PdndServiceUpdateDTO.class);
    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    pdndService.setClientId("oldClientId");
    PdndClient newPdndClient = podamFactory.manufacturePojo(PdndClient.class);
    newPdndClient.setOrganizationId(2L);
    newPdndClient.setSubUnitCode(subUnitCode);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, subUnitCode))
      .thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(updateDTO.getClientId())).thenReturn(Optional.of(newPdndClient));

    InvalidValueException exception = assertThrows(InvalidValueException.class,
      () -> service.updatePdndService(organizationId, purposeId, updateDTO, subUnitCode));

    assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_PDND_CLIENT, exception.getCode());
  }

  @Test
  void givenNewClientNotFoundWhenUpdatePdndServiceThenThrowNotFoundException() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";
    String subUnitCode = "SUB_01";
    PdndServiceUpdateDTO updateDTO = podamFactory.manufacturePojo(PdndServiceUpdateDTO.class);
    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    pdndService.setClientId("oldClientId");

    when(pdndServiceRepositoryMock.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, subUnitCode))
      .thenReturn(Optional.of(pdndService));
    when(pdndClientRepositoryMock.findById(updateDTO.getClientId())).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
      () -> service.updatePdndService(organizationId, purposeId, updateDTO, subUnitCode));

    assertEquals(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_NOT_FOUND, exception.getCode());
  }

  @Test
  void givenPdndServiceNotFoundWhenUpdatePdndServiceThenThrowNotFoundException() {
    Long organizationId = 1L;
    String purposeId = "PURPOSE_ID";
    String subUnitCode = "SUB_01";
    PdndServiceUpdateDTO updateDTO = podamFactory.manufacturePojo(PdndServiceUpdateDTO.class);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndPurposeIdAndSubUnitCode(organizationId, purposeId, subUnitCode))
      .thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
      () -> service.updatePdndService(organizationId, purposeId, updateDTO, subUnitCode));

    assertEquals(ErrorCodeConstants.ERROR_CODE_PDND_SERVICE_NOT_FOUND, exception.getCode());
  }
}
