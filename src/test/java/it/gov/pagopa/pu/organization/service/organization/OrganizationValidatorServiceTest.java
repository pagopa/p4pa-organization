package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.OrganizationUpdateDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.enums.OrganizationStatus;
import it.gov.pagopa.pu.organization.exception.common.InvalidValueException;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.model.OrganizationStation;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import it.gov.pagopa.pu.organization.util.TestUtils;
import it.gov.pagopa.pu.organization.util.faker.OrganizationFaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrganizationValidatorServiceTest {
  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private OrganizationValidatorService organizationValidatorService;

  @BeforeEach
  void setUp() {
    organizationValidatorService = new OrganizationValidatorService(true);
  }

  @Test
  void givenValidCreateDTOWhenValidateOrganizationCreateDTOThenOk() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT00X0760100000000000000000");
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
    dto.setPostalIban("IT00X0760100000000000000000");
    dto.setSegregationCode("12");

    assertThrows(InvalidValueException.class, () -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenInvalidPostalIbanWhenValidateOrganizationCreateDTOThenThrowException() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT00X076010000000000000");
    dto.setSegregationCode("12");

    assertThrows(InvalidValueException.class, () -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenInvalidAbiCodeForPostalIbanWhenValidateOrganizationCreateDTOThenThrowException() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT60X0542811101000000123456");
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
    dto.setPostalIban("IT00X0760100000000000000000");
    dto.setSegregationCode("12");

    assertThrows(InvalidValueException.class, () -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenInvalidSegregationCodeWhenValidateOrganizationCreateDTOThenThrowException() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT00X0760100000000000000000");
    dto.setSegregationCode("abc");

    assertThrows(InvalidValueException.class, () -> organizationValidatorService.validateOrganizationCreateDTO(dto));
  }

  @Test
  void givenValidOrganizationDTOWhenValidateOrganizationDTOThenOk() {
    OrganizationUpdateDTO organizationUpdateDTO = podamFactory.manufacturePojo(OrganizationUpdateDTO.class);
    organizationUpdateDTO.setOrgFiscalCode("12345678903");
    organizationUpdateDTO.setIban("IT60X0542811101000000123456");
    organizationUpdateDTO.setPostalIban("IT00X0760100000000000000000");
    organizationUpdateDTO.setSegregationCode("02");
    organizationUpdateDTO.setStatus(OrganizationStatus.DRAFT);

    Organization existingOrganization = OrganizationFaker.buildOrganization();
    existingOrganization.setBrokerId(organizationUpdateDTO.getBrokerId());
    existingOrganization.setExternalOrganizationId(organizationUpdateDTO.getExternalOrganizationId());
    existingOrganization.setIpaCode(organizationUpdateDTO.getIpaCode());
    existingOrganization.setOrgFiscalCode(organizationUpdateDTO.getOrgFiscalCode());
    existingOrganization.setOrgName(organizationUpdateDTO.getOrgName());
    existingOrganization.setOrgTypeCode(organizationUpdateDTO.getOrgTypeCode());

    assertDoesNotThrow(() -> organizationValidatorService.validateOrganizationDTO(organizationUpdateDTO, existingOrganization));
  }

  @Test
  void givenUpdatedImmutableFieldWhenValidateOrganizationDTOThenValidationException() {
    OrganizationUpdateDTO organizationUpdateDTO = podamFactory.manufacturePojo(OrganizationUpdateDTO.class);
    organizationUpdateDTO.setOrgFiscalCode("12345678903");
    organizationUpdateDTO.setIban("IT60X0542811101000000123456");
    organizationUpdateDTO.setPostalIban("IT00X0760100000000000000000");
    organizationUpdateDTO.setSegregationCode("01");

    Organization existingOrganization = OrganizationFaker.buildOrganization();
    existingOrganization.setBrokerId(organizationUpdateDTO.getBrokerId());
    existingOrganization.setExternalOrganizationId(organizationUpdateDTO.getExternalOrganizationId());
    existingOrganization.setIpaCode(organizationUpdateDTO.getIpaCode());
    existingOrganization.setOrgFiscalCode(organizationUpdateDTO.getOrgFiscalCode());
    existingOrganization.setOrgName(organizationUpdateDTO.getOrgName());
    existingOrganization.setOrgTypeCode(organizationUpdateDTO.getOrgTypeCode() + "_old");

    InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
      organizationValidatorService.validateOrganizationDTO(organizationUpdateDTO, existingOrganization));

    assertEquals(ErrorCodeConstants.ERROR_CODE_IMMUTABLE_FIELD, exception.getCode());
  }

  @Test
  void givenStatusActiveAndMissingLogoAndIbanWhenValidateStatusUpdateThenValidationException() {
    OrganizationUpdateDTO organization = new OrganizationUpdateDTO();
    organization.setStatus(OrganizationStatus.ACTIVE);
    organization.setOrgLogo(null);
    organization.setIban(null);
    organization.setDefaultOrganizationStationId(1L);

    OrganizationStation station = new OrganizationStation();
    station.setSegregationCode("01");

    InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
      organizationValidatorService.validateStatusUpdate(organization));

    assertEquals(ErrorCodeConstants.ERROR_CODE_MISSING_ORGANIZATION_FIELDS, exception.getCode());
    assertTrue(exception.getMessage().contains("orgLogo"));
    assertTrue(exception.getMessage().contains("iban"));
  }

  @Test
  void givenStatusActiveAndMissingStationIdWhenValidateStatusUpdateThenValidationException() {
    OrganizationUpdateDTO organization = new OrganizationUpdateDTO();
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
  void givenValidActiveStatusWhenValidateStatusUpdateThenOk() {
    OrganizationUpdateDTO organization = new OrganizationUpdateDTO();
    organization.setStatus(OrganizationStatus.ACTIVE);
    organization.setOrgLogo("logo_url");
    organization.setIban("IT60X0542811101000000123456");
    organization.setDefaultOrganizationStationId(1L);

    OrganizationStation station = new OrganizationStation();
    station.setSegregationCode("01");

    assertDoesNotThrow(() -> organizationValidatorService.validateStatusUpdate(organization));
  }

  @Test
  void givenDraftStatusWhenValidateStatusUpdateThenOk() {
    OrganizationUpdateDTO organization = new OrganizationUpdateDTO();
    organization.setStatus(OrganizationStatus.DRAFT);

    organization.setOrgLogo(null);
    organization.setIban(null);
    organization.setDefaultOrganizationStationId(null);

    assertDoesNotThrow(() -> organizationValidatorService.validateStatusUpdate(organization));
  }

  @Test
  void givenActiveStatusAndNullSegregationCodeWhenValidateOrganizationCreateDTOThenThrowException() {
    OrganizationCreateDTO dto = new OrganizationCreateDTO();
    dto.setStatus(OrganizationStatus.ACTIVE);
    dto.setSegregationCode(null);
    dto.setOrgFiscalCode("12345678903");
    dto.setIban("IT60X0542811101000000123456");
    dto.setPostalIban("IT00X0760100000000000000000");

    InvalidValueException exception = assertThrows(InvalidValueException.class,
      () -> organizationValidatorService.validateOrganizationCreateDTO(dto));

    assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_SEGREGATION_CODE, exception.getCode());
    assertTrue(exception.getMessage().contains("Segregation code is required for organization status ACTIVE"));
  }
}
