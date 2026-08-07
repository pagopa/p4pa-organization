package it.gov.pagopa.pu.organization.service.pdnd;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndClientNoSecretDTO;
import it.gov.pagopa.pu.organization.enums.PdndServiceType;
import it.gov.pagopa.pu.organization.exception.common.NotFoundException;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.mapper.PdndClientMapper;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import it.gov.pagopa.pu.organization.model.Organization;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import it.gov.pagopa.pu.organization.repository.OrganizationRepository;
import it.gov.pagopa.pu.organization.repository.PdndClientRepository;
import it.gov.pagopa.pu.organization.util.ErrorCodeConstants;
import it.gov.pagopa.pu.organization.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdndClientServiceTest {
  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private OrganizationRepository organizationRepositoryMock;
  @Mock
  private PdndClientRepository pdndClientRepositoryMock;
  @Mock
  private OrgSubUnitRepository orgSubUnitRepositoryMock;
  @Mock
  private PdndClientMapper pdndClientMapperMock;

  private PdndClientService service;

  @BeforeEach
  void setUp() {
    service = new PdndClientService(
      pdndClientRepositoryMock,
      organizationRepositoryMock,
      orgSubUnitRepositoryMock,
      pdndClientMapperMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      pdndClientRepositoryMock,
      organizationRepositoryMock,
      orgSubUnitRepositoryMock,
      pdndClientMapperMock
    );
  }

  @Test
  void whenSavePdndClientThenOk() {
    PdndClientDTO pdndClientDTO = podamFactory.manufacturePojo(PdndClientDTO.class);
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    OrgSubUnit orgSubUnit = podamFactory.manufacturePojo(OrgSubUnit.class);
    PdndClient expectedResult = podamFactory.manufacturePojo(PdndClient.class);

    when(organizationRepositoryMock.findById(pdndClientDTO.getOrganizationId())).thenReturn(Optional.of(organization));
    when(orgSubUnitRepositoryMock.findById(Mockito.argThat(id->
      id.getOrganizationId().equals(pdndClientDTO.getOrganizationId()) && id.getSubUnitCode().equals(pdndClientDTO.getSubUnitCode()))))
      .thenReturn(Optional.of(orgSubUnit));
    when(pdndClientMapperMock.toModel(pdndClientDTO)).thenReturn(expectedResult);
    when(pdndClientRepositoryMock.save(expectedResult)).thenReturn(expectedResult);

    PdndClient result = service.savePdndClient(pdndClientDTO);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenNoOrgSubUnitWhenSavePdndClientThenOk() {
    PdndClientDTO pdndClientDTO = podamFactory.manufacturePojo(PdndClientDTO.class);
    pdndClientDTO.setSubUnitCode(null);
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    PdndClient expectedResult = podamFactory.manufacturePojo(PdndClient.class);

    when(organizationRepositoryMock.findById(pdndClientDTO.getOrganizationId())).thenReturn(Optional.of(organization));
    when(pdndClientMapperMock.toModel(pdndClientDTO)).thenReturn(expectedResult);
    when(pdndClientRepositoryMock.save(expectedResult)).thenReturn(expectedResult);

    PdndClient result = service.savePdndClient(pdndClientDTO);

    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void givenOrgSubUnitNotFoundWhenSavePdndClientThenNotFoundException() {
    PdndClientDTO pdndClientDTO = podamFactory.manufacturePojo(PdndClientDTO.class);
    Organization organization = podamFactory.manufacturePojo(Organization.class);

    when(organizationRepositoryMock.findById(pdndClientDTO.getOrganizationId())).thenReturn(Optional.of(organization));
    when(orgSubUnitRepositoryMock.findById(Mockito.argThat(id->
      id.getOrganizationId().equals(pdndClientDTO.getOrganizationId()) && id.getSubUnitCode().equals(pdndClientDTO.getSubUnitCode()))))
      .thenReturn(Optional.empty());

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> service.savePdndClient(pdndClientDTO));

    Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_ORG_SUB_UNIT_NOT_FOUND, exception.getCode());
  }

  @Test
  void givenOrganizationNotFoundWhenSavePdndClientThenOrganizationNotFoundException() {
    PdndClientDTO pdndClientDTO = podamFactory.manufacturePojo(PdndClientDTO.class);

    when(organizationRepositoryMock.findById(pdndClientDTO.getOrganizationId())).thenReturn(Optional.empty());

    OrganizationNotFoundException exception = Assertions.assertThrows(OrganizationNotFoundException.class, () -> service.savePdndClient(pdndClientDTO));

    Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_ORGANIZATION_NOT_FOUND, exception.getCode());
  }

  @Test
  void whenGetPdndClientByOrganizationIdAndPdndServiceTypeThenOk() {
    Long organizationId = 1L;
    PdndServiceType pdndServiceType = PdndServiceType.SEND;
    String subUnitCode = "subUnitCode";

    PdndClient pdndClient = podamFactory.manufacturePojo(PdndClient.class);
    PdndClientDTO expectedResult = podamFactory.manufacturePojo(PdndClientDTO.class);

    when(pdndClientRepositoryMock.findUsableByOrganizationIdAndServiceTypeAndSubUnitCode(organizationId,pdndServiceType,subUnitCode))
      .thenReturn(Optional.of(pdndClient));
    when(pdndClientMapperMock.toDTO(pdndClient)).thenReturn(expectedResult);

    PdndClientDTO result = service.getUsablePdndClientByOrganizationIdAndPdndServiceType(organizationId,pdndServiceType,subUnitCode);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenNoPdndClientWhenGetPdndClientByOrganizationIdAndPdndServiceTypeThenNotFoundException() {
    Long organizationId = 1L;
    PdndServiceType pdndServiceType = PdndServiceType.SEND;
    String subUnitCode = "subUnitCode";

    when(pdndClientRepositoryMock.findUsableByOrganizationIdAndServiceTypeAndSubUnitCode(organizationId,pdndServiceType,subUnitCode))
      .thenReturn(Optional.empty());

    NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () -> service.getUsablePdndClientByOrganizationIdAndPdndServiceType(organizationId, pdndServiceType, subUnitCode));

    Assertions.assertSame(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_NOT_FOUND, notFoundException.getCode());
  }

  @Test
  void givenSubUnitCodeWhenGetPdndClientsByOrganizationIdAndSubUnitCodeThenOk() {
    Long organizationId = 1L;
    String subUnitCode = "subUnitCode";

    PdndClient firstPdndClient = podamFactory.manufacturePojo(PdndClient.class);
    PdndClient secondPdndClient = podamFactory.manufacturePojo(PdndClient.class);

    PdndClientNoSecretDTO firstExpectedResponse = podamFactory.manufacturePojo(PdndClientNoSecretDTO.class);
    PdndClientNoSecretDTO secondExpectedResponse = podamFactory.manufacturePojo(PdndClientNoSecretDTO.class);

    when(pdndClientRepositoryMock.findAllByOrganizationIdAndSubUnitCode(organizationId, subUnitCode))
      .thenReturn(List.of(firstPdndClient, secondPdndClient));
    when(pdndClientMapperMock.mapToPdndClientNoSecretDTO(firstPdndClient))
      .thenReturn(firstExpectedResponse);
    when(pdndClientMapperMock.mapToPdndClientNoSecretDTO(secondPdndClient))
      .thenReturn(secondExpectedResponse);

    List<PdndClientNoSecretDTO> result = service.getPdndClientsByOrganizationIdAndSubUnitCode(organizationId, subUnitCode);

    Assertions.assertEquals(List.of(firstExpectedResponse, secondExpectedResponse), result);
  }

  @Test
  void givenNullSubUnitCodeWhengetPdndClientsByOrganizationIdAndSubUnitCodeThenOk() {
    Long organizationId = 1L;

    PdndClient firstPdndClient = podamFactory.manufacturePojo(PdndClient.class);
    PdndClient secondPdndClient = podamFactory.manufacturePojo(PdndClient.class);

    PdndClientNoSecretDTO firstExpectedResponse = podamFactory.manufacturePojo(PdndClientNoSecretDTO.class);
    PdndClientNoSecretDTO secondExpectedResponse = podamFactory.manufacturePojo(PdndClientNoSecretDTO.class);

    when(pdndClientRepositoryMock.findAllByOrganizationIdAndSubUnitCodeIsNull(organizationId))
      .thenReturn(List.of(firstPdndClient, secondPdndClient));

    when(pdndClientMapperMock.mapToPdndClientNoSecretDTO(firstPdndClient))
      .thenReturn(firstExpectedResponse);
    when(pdndClientMapperMock.mapToPdndClientNoSecretDTO(secondPdndClient))
      .thenReturn(secondExpectedResponse);

    List<PdndClientNoSecretDTO> result = service.getPdndClientsByOrganizationIdAndSubUnitCode(organizationId, null);

    Assertions.assertEquals(List.of(firstExpectedResponse, secondExpectedResponse), result);
  }

  @Test
  void givenNoClientsAndSubUnitCodeWhenGetPdndClientsByOrganizationIdAndSubUnitCodeThenEmptyList() {
    Long organizationId = 1L;
    String subUnitCode = "notExistingSubUnitCode";

    when(pdndClientRepositoryMock.findAllByOrganizationIdAndSubUnitCode(organizationId, subUnitCode))
      .thenReturn(Collections.emptyList());

    List<PdndClientNoSecretDTO> result = service.getPdndClientsByOrganizationIdAndSubUnitCode(organizationId, subUnitCode);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void givenNoClientsAndNullSubUnitCodeWhenGetPdndClientsByOrganizationIdAndSubUnitCodeThenEmptyList() {
    Long organizationId = 1L;

    when(pdndClientRepositoryMock.findAllByOrganizationIdAndSubUnitCodeIsNull(organizationId))
      .thenReturn(Collections.emptyList());

    List<PdndClientNoSecretDTO> result = service.getPdndClientsByOrganizationIdAndSubUnitCode(organizationId, null);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void givenBlankSubUnitCodeWhenGetPdndClientsByOrganizationIdAndSubUnitCodeThenSearchByBlankSubUnitCode() {
    Long organizationId = 1L;
    String subUnitCode = "";

    when(pdndClientRepositoryMock.findAllByOrganizationIdAndSubUnitCode(organizationId, subUnitCode))
      .thenReturn(Collections.emptyList());

    List<PdndClientNoSecretDTO> result = service.getPdndClientsByOrganizationIdAndSubUnitCode(organizationId, subUnitCode);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void whenGetPdndClientDetailThenOk() {
    Long organizationId = 1L;
    String clientId = "clientId";

    PdndClient pdndClient = podamFactory.manufacturePojo(PdndClient.class);

    PdndClientNoSecretDTO expectedResult = podamFactory.manufacturePojo(PdndClientNoSecretDTO.class);

    when(pdndClientRepositoryMock.findByClientIdAndOrganizationId(clientId, organizationId))
      .thenReturn(Optional.of(pdndClient));

    when(pdndClientMapperMock.mapToPdndClientNoSecretDTO(pdndClient))
      .thenReturn(expectedResult);

    PdndClientNoSecretDTO result = service.getPdndClientDetail(organizationId, clientId);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenPdndClientNotFoundWhenGetPdndClientDetailThenThrowNotFoundException() {
    Long organizationId = 1L;
    String clientId = "notExistingClientId";

    when(pdndClientRepositoryMock.findByClientIdAndOrganizationId(clientId, organizationId))
      .thenReturn(Optional.empty());

    NotFoundException exception = Assertions.assertThrows(
      NotFoundException.class, () -> service.getPdndClientDetail(organizationId, clientId));

    Assertions.assertSame(ErrorCodeConstants.ERROR_CODE_PDND_CLIENT_NOT_FOUND, exception.getCode());
  }
}
