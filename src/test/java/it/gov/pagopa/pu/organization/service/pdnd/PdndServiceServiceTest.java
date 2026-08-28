package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndServiceRequestDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.exception.common.ConflictException;
import it.gov.pagopa.pu.organization.exception.common.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.common.NotFoundException;
import it.gov.pagopa.pu.organization.mapper.PdndServiceMapper;
import it.gov.pagopa.pu.organization.model.PdndService;
import it.gov.pagopa.pu.organization.repository.PdndServiceRepository;
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
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdndServiceServiceTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private PdndServiceRepository pdndServiceRepositoryMock;
  @Mock
  private PdndServiceMapper pdndServiceMapperMock;
  @Mock
  private PdndClientService pdndClientServiceMock;
  @InjectMocks
  private PdndServiceService service;

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      pdndServiceRepositoryMock,
      pdndServiceMapperMock,
      pdndClientServiceMock
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

    PdndService pdndService = podamFactory.manufacturePojo(PdndService.class);
    PdndServiceDTO expectedResponse = podamFactory.manufacturePojo(PdndServiceDTO.class);

    when(pdndServiceRepositoryMock.findByOrganizationIdAndServiceTypeAndSubUnitCode(organizationId, PdndServiceType.SEND, subUnitCode))
      .thenReturn(List.of(pdndService));
    when(pdndServiceMapperMock.toPdndServiceDTO(pdndService)).thenReturn(expectedResponse);

    List<PdndServiceDTO> result = service.getPdndServices(organizationId, PdndServiceType.SEND, subUnitCode);

    assertEquals(List.of(expectedResponse), result);
  }

}
