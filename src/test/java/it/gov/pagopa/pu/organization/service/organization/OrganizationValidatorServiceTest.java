package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.OrganizationDetailDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.custom.NotFoundException;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.repository.OrganizationStationRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import it.gov.pagopa.pu.organization.util.TestUtils;
import it.gov.pagopa.pu.organization.util.faker.OrganizationFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationValidatorServiceTest {
  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private OrganizationStationRepository organizationStationRepositoryMock;

  private OrganizationValidatorService organizationValidatorService;

  @BeforeEach
  void setUp() {
    organizationValidatorService = new OrganizationValidatorService(organizationStationRepositoryMock, true);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(organizationStationRepositoryMock);
  }

  @Test
  void givenValidCreateDTOWhenValidateOrganizationCreateDTOThenOk() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT60X0542811101000000123456");
    dto.setSegregationCode("12");

    assertDoesNotThrow(() -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenValidCreateDTOWitNullPostalIbanWhenValidateOrganizationCreateDTOThenOk() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban(null);
    dto.setSegregationCode("12");

    assertDoesNotThrow(() -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenInvalidIbanWhenValidateOrganizationCreateDTOThenThrowException() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("invalidIban");
    dto.setPostalIban("IT60X0542811101000000123456");
    dto.setSegregationCode("12");

    assertThrows(InvalidValueException.class, () -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenInvalidPostalIbanWhenValidateOrganizationCreateDTOThenThrowException() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("invalidPostalIban");
    dto.setSegregationCode("12");

    assertThrows(InvalidValueException.class, () -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenEmptyPostalIbanWhenValidateOrganizationCreateDTOThenThrowException() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("");
    dto.setSegregationCode("12");

    assertThrows(InvalidValueException.class, () -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenNullFiscalCodeWhenValidateOrganizationCreateDTOThenThrowException() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode(null);
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT60X0542811101000000123456");
    dto.setSegregationCode("12");

    assertThrows(InvalidValueException.class, () -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenInvalidSegregationCodeWhenValidateOrganizationCreateDTOThenThrowException() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT60X0542811101000000123456");
    dto.setSegregationCode("abc");

    assertThrows(InvalidValueException.class, () -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenValidOrganizationDTOWhenValidateOrganizationDTOThenOk() {
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organizationDetailDTO.setOrgFiscalCode("12345678903");
    organizationDetailDTO.setIban("IT60X0542811101000000123456");
    organizationDetailDTO.setPostalIban("IT60X0542811101000000123456");
    organizationDetailDTO.setSegregationCode("02");
    organizationDetailDTO.setStatus(OrganizationStatus.DRAFT);

    Organization existingOrganization = OrganizationFaker.buildOrganization();
    existingOrganization.setBrokerId(organizationDetailDTO.getBrokerId());
    existingOrganization.setExternalOrganizationId(organizationDetailDTO.getExternalOrganizationId());
    existingOrganization.setIpaCode(organizationDetailDTO.getIpaCode());
    existingOrganization.setOrgFiscalCode(organizationDetailDTO.getOrgFiscalCode());
    existingOrganization.setOrgName(organizationDetailDTO.getOrgName());
    existingOrganization.setOrgTypeCode(organizationDetailDTO.getOrgTypeCode());

    assertDoesNotThrow(() -> organizationValidatorService.validateOrganizationDTO(organizationDetailDTO, existingOrganization));
  }

  @Test
  void givenUpdatedImmutableFieldWhenValidateOrganizationDTOThenValidationException() {
    OrganizationDetailDTO organizationDetailDTO = podamFactory.manufacturePojo(OrganizationDetailDTO.class);
    organizationDetailDTO.setOrgFiscalCode("12345678903");
    organizationDetailDTO.setIban("IT60X0542811101000000123456");
    organizationDetailDTO.setPostalIban("IT60X0542811101000000123456");
    organizationDetailDTO.setSegregationCode("01");

    Organization existingOrganization = OrganizationFaker.buildOrganization();
    existingOrganization.setBrokerId(organizationDetailDTO.getBrokerId());
    existingOrganization.setExternalOrganizationId(organizationDetailDTO.getExternalOrganizationId());
    existingOrganization.setIpaCode(organizationDetailDTO.getIpaCode());
    existingOrganization.setOrgFiscalCode(organizationDetailDTO.getOrgFiscalCode());
    existingOrganization.setOrgName(organizationDetailDTO.getOrgName());
    existingOrganization.setOrgTypeCode(organizationDetailDTO.getOrgTypeCode() + "_old");

    InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
      organizationValidatorService.validateOrganizationDTO(organizationDetailDTO, existingOrganization));

    assertEquals(ErrorCodeConstants.ERROR_CODE_IMMUTABLE_FIELD, exception.getCode());
  }

  @Test
  void givenStatusActiveAndMissingLogoAndIbanWhenValidateStatusUpdateThenValidationException() {
    OrganizationDetailDTO organization = new OrganizationDetailDTO();
    organization.setStatus(OrganizationStatus.ACTIVE);
    organization.setOrgLogo(null);
    organization.setIban(null);
    organization.setDefaultOrganizationStationId(1L);

    OrganizationStation station = new OrganizationStation();
    station.setSegregationCode("01");
    when(organizationStationRepositoryMock.findById(1L)).thenReturn(Optional.of(station));

    InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
      organizationValidatorService.validateStatusUpdate(organization));

    assertEquals(ErrorCodeConstants.ERROR_CODE_MISSING_ORGANIZATION_FIELDS, exception.getCode());
    assertTrue(exception.getMessage().contains("orgLogo"));
    assertTrue(exception.getMessage().contains("iban"));
  }

  @Test
  void givenStatusActiveAndMissingStationIdWhenValidateStatusUpdateThenValidationException() {
    OrganizationDetailDTO organization = new OrganizationDetailDTO();
    organization.setStatus(OrganizationStatus.ACTIVE);
    organization.setOrgLogo("orgLogo");
    organization.setIban("IT60X0542811101000000123456");
    organization.setDefaultOrganizationStationId(null);

    InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
      organizationValidatorService.validateStatusUpdate(organization));

    assertEquals(ErrorCodeConstants.ERROR_CODE_MISSING_ORGANIZATION_FIELDS, exception.getCode());
    assertTrue(exception.getMessage().contains("defaultOrganizationStationId"));
  }

  @Test
  void givenStatusActiveAndOrganizationStationNotFoundWhenValidateStatusUpdateThenNotFoundException() {
    OrganizationDetailDTO organization = new OrganizationDetailDTO();
    organization.setStatus(OrganizationStatus.ACTIVE);
    organization.setOrgLogo("logo_url");
    organization.setIban("IT60X0542811101000000123456");
    organization.setDefaultOrganizationStationId(1L);

    when(organizationStationRepositoryMock.findById(1L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () ->
      organizationValidatorService.validateStatusUpdate(organization));

    assertEquals(ErrorCodeConstants.ERROR_CODE_ORGANIZATION_STATION_NOT_FOUND, exception.getCode());
  }

  @Test
  void givenStatusActiveAndStationWithMissingSegregationCodeWhenValidateStatusUpdateThenValidationException() {
    OrganizationDetailDTO organization = new OrganizationDetailDTO();
    organization.setStatus(OrganizationStatus.ACTIVE);
    organization.setOrgLogo("orgLogo");
    organization.setIban("IT60X0542811101000000123456");
    organization.setDefaultOrganizationStationId(1L);

    OrganizationStation station = new OrganizationStation();
    station.setSegregationCode(null);

    when(organizationStationRepositoryMock.findById(1L)).thenReturn(Optional.of(station));

    InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
      organizationValidatorService.validateStatusUpdate(organization));

    assertEquals(ErrorCodeConstants.ERROR_CODE_MISSING_ORGANIZATION_FIELDS, exception.getCode());
    assertTrue(exception.getMessage().contains("segregationCode"));
  }

  @Test
  void givenValidActiveStatusWhenValidateStatusUpdateThenOk() {
    OrganizationDetailDTO organization = new OrganizationDetailDTO();
    organization.setStatus(OrganizationStatus.ACTIVE);
    organization.setOrgLogo("logo_url");
    organization.setIban("IT60X0542811101000000123456");
    organization.setDefaultOrganizationStationId(1L);

    OrganizationStation station = new OrganizationStation();
    station.setSegregationCode("01");

    when(organizationStationRepositoryMock.findById(1L)).thenReturn(Optional.of(station));

    assertDoesNotThrow(() -> organizationValidatorService.validateStatusUpdate(organization));
  }

  @Test
  void givenDraftStatusWhenValidateStatusUpdateThenOk() {
    OrganizationDetailDTO organization = new OrganizationDetailDTO();
    organization.setStatus(OrganizationStatus.DRAFT);

    organization.setOrgLogo(null);
    organization.setIban(null);
    organization.setDefaultOrganizationStationId(null);

    assertDoesNotThrow(() -> organizationValidatorService.validateStatusUpdate(organization));

    Mockito.verifyNoInteractions(organizationStationRepositoryMock);
  }
}
