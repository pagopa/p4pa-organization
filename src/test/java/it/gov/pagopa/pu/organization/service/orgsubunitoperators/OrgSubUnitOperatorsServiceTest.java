package it.gov.pagopa.pu.organization.service.orgsubunitoperators;

import it.gov.pagopa.pu.organization.model.OrgSubUnitOperators;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitOperatorsRepository;
import it.gov.pagopa.pu.organization.repository.OrgSubUnitRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgSubUnitOperatorsServiceTest {

  @Mock
  private OrgSubUnitRepository orgSubUnitRepositoryMock;

  @Mock
  private OrgSubUnitOperatorsRepository orgSubUnitOperatorsRepositoryMock;

  private OrgSubUnitOperatorsService service;

  @BeforeEach
  void setUp() {
    service = new OrgSubUnitOperatorsService(
      orgSubUnitRepositoryMock,
      orgSubUnitOperatorsRepositoryMock
    );
  }

  @AfterEach
  void tearDown() {
    Mockito.verifyNoMoreInteractions(
      orgSubUnitRepositoryMock,
      orgSubUnitOperatorsRepositoryMock
    );
  }

  @Test
  void whenAddOrgSubUnitsToOperatorThenOk() {
    Long organizationId = 1L;
    String mappedExternalUserId = "userId";
    List<String> orgSubUnitCodes = List.of("SUB_UNIT_1", "SUB_UNIT_2");

    Set<String> requestedSubUnitCodes = Set.of("SUB_UNIT_1", "SUB_UNIT_2");

    when(orgSubUnitRepositoryMock.findExistingSubUnitCodes(organizationId, requestedSubUnitCodes))
      .thenReturn(requestedSubUnitCodes);

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_1", mappedExternalUserId))
      .thenReturn(Optional.empty());

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_2", mappedExternalUserId))
      .thenReturn(Optional.empty());

    assertDoesNotThrow(() ->
      service.addOrgSubUnitsToOperator(organizationId, mappedExternalUserId, orgSubUnitCodes));

    ArgumentCaptor<OrgSubUnitOperators> captor = ArgumentCaptor.forClass(OrgSubUnitOperators.class);

    verify(orgSubUnitRepositoryMock).findExistingSubUnitCodes(organizationId, requestedSubUnitCodes);

    verify(orgSubUnitOperatorsRepositoryMock)
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_1", mappedExternalUserId);

    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_2", mappedExternalUserId);

    verify(orgSubUnitOperatorsRepositoryMock, times(2)).save(captor.capture());

    List<OrgSubUnitOperators> savedAssociations = captor.getAllValues();

    assertEquals(
      Set.of("SUB_UNIT_1", "SUB_UNIT_2"),
      savedAssociations.stream()
        .map(OrgSubUnitOperators::getSubUnitCode)
        .collect(Collectors.toSet())
    );

    savedAssociations.forEach(association -> {
      assertEquals(organizationId, association.getOrganizationId());
      assertEquals(mappedExternalUserId, association.getOperatorExternalUserId());
    });
  }

  @Test
  void givenAlreadyExistingAssociationWhenAddOrgSubUnitsToOperatorThenIgnoreIt() {
    Long organizationId = 1L;
    String mappedExternalUserId = "userId";
    List<String> orgSubUnitCodes = List.of("SUB_UNIT_1", "SUB_UNIT_2");

    Set<String> requestedSubUnitCodes = Set.of("SUB_UNIT_1", "SUB_UNIT_2");

    when(orgSubUnitRepositoryMock.findExistingSubUnitCodes(organizationId, requestedSubUnitCodes))
      .thenReturn(requestedSubUnitCodes);

    OrgSubUnitOperators existingAssociation = new OrgSubUnitOperators();
    existingAssociation.setOrganizationId(organizationId);
    existingAssociation.setOperatorExternalUserId(mappedExternalUserId);
    existingAssociation.setSubUnitCode("SUB_UNIT_1");

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_1", mappedExternalUserId))
      .thenReturn(Optional.of(existingAssociation));

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_2", mappedExternalUserId))
      .thenReturn(Optional.empty());

    assertDoesNotThrow(() ->
      service.addOrgSubUnitsToOperator(organizationId, mappedExternalUserId, orgSubUnitCodes));

    verify(orgSubUnitRepositoryMock).findExistingSubUnitCodes(organizationId, requestedSubUnitCodes);

    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_1", mappedExternalUserId);

    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_2", mappedExternalUserId);

    ArgumentCaptor<OrgSubUnitOperators> captor = ArgumentCaptor.forClass(OrgSubUnitOperators.class);

    verify(orgSubUnitOperatorsRepositoryMock).save(captor.capture());

    OrgSubUnitOperators savedAssociation = captor.getValue();

    assertEquals(organizationId, savedAssociation.getOrganizationId());
    assertEquals("SUB_UNIT_2", savedAssociation.getSubUnitCode());
    assertEquals(mappedExternalUserId, savedAssociation.getOperatorExternalUserId());
  }

  @Test
  void givenAllAssociationsAlreadyExistWhenAddOrgSubUnitsToOperatorThenOk() {
    Long organizationId = 1L;
    String mappedExternalUserId = "userId";
    List<String> orgSubUnitCodes = List.of("SUB_UNIT_1", "SUB_UNIT_2");

    Set<String> requestedSubUnitCodes = Set.of("SUB_UNIT_1", "SUB_UNIT_2");

    when(orgSubUnitRepositoryMock.findExistingSubUnitCodes(organizationId, requestedSubUnitCodes))
      .thenReturn(requestedSubUnitCodes);

    OrgSubUnitOperators firstAssociation = new OrgSubUnitOperators();
    OrgSubUnitOperators secondAssociation = new OrgSubUnitOperators();

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_1", mappedExternalUserId))
      .thenReturn(Optional.of(firstAssociation));

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_2", mappedExternalUserId))
      .thenReturn(Optional.of(secondAssociation));

    assertDoesNotThrow(() -> service.addOrgSubUnitsToOperator(organizationId, mappedExternalUserId, orgSubUnitCodes));

    verify(orgSubUnitRepositoryMock).findExistingSubUnitCodes(organizationId, requestedSubUnitCodes);

    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_1", mappedExternalUserId);

    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_2", mappedExternalUserId);
  }

  @Test
  void givenMissingOrgSubUnitWhenAddOrgSubUnitsToOperatorThenBadRequestException() {
    Long organizationId = 1L;
    String mappedExternalUserId = "userId";
    List<String> orgSubUnitCodes = List.of("SUB_UNIT_1", "SUB_UNIT_2");

    Set<String> requestedSubUnitCodes = Set.of("SUB_UNIT_1", "SUB_UNIT_2");

    when(orgSubUnitRepositoryMock.findExistingSubUnitCodes(organizationId, requestedSubUnitCodes))
      .thenReturn(Set.of("SUB_UNIT_1"));

    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class,
      () -> service.addOrgSubUnitsToOperator(organizationId, mappedExternalUserId, orgSubUnitCodes));

    assertEquals(400, exception.getStatusCode().value());

    verify(orgSubUnitRepositoryMock).findExistingSubUnitCodes(organizationId, requestedSubUnitCodes);
  }

  @Test
  void givenDuplicatedOrgSubUnitCodesWhenAddOrgSubUnitsToOperatorThenProcessOnce() {
    Long organizationId = 1L;
    String mappedExternalUserId = "userId";

    List<String> orgSubUnitCodes =
      List.of("SUB_UNIT_1", "SUB_UNIT_1");

    Set<String> requestedSubUnitCodes = Set.of("SUB_UNIT_1");

    when(orgSubUnitRepositoryMock.findExistingSubUnitCodes(organizationId, requestedSubUnitCodes))
      .thenReturn(requestedSubUnitCodes);

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_1", mappedExternalUserId))
      .thenReturn(Optional.empty());

    assertDoesNotThrow(() ->
      service.addOrgSubUnitsToOperator(organizationId, mappedExternalUserId, orgSubUnitCodes));

    verify(orgSubUnitRepositoryMock).findExistingSubUnitCodes(organizationId, requestedSubUnitCodes);

    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, "SUB_UNIT_1", mappedExternalUserId);

    verify(orgSubUnitOperatorsRepositoryMock).save(any(OrgSubUnitOperators.class));
  }
}
