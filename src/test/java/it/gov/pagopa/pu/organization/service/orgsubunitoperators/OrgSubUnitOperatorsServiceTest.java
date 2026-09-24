package it.gov.pagopa.pu.organization.service.orgsubunitoperators;

import it.gov.pagopa.pu.organization.model.OrgSubUnit;
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

  @Test
  void whenDeleteOrgSubUnitFromOperatorThenOk() {
    Long organizationId = 1L;
    String mappedExternalUserId = "userId";
    String subUnitCode = "SUB_UNIT_1";

    assertDoesNotThrow(() -> service.deleteOrgSubUnitFromOperator(organizationId, mappedExternalUserId, subUnitCode));

    verify(orgSubUnitOperatorsRepositoryMock).deleteByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, mappedExternalUserId);
  }

  @Test
  void whenAddOperatorsToOrgSubUnitThenOk() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_UNIT_1";
    List<String> mappedExternalUserIds = List.of("userId1", "userId2");

    OrgSubUnit.OrgSubUnitId orgSubUnitId = new OrgSubUnit.OrgSubUnitId(organizationId, subUnitCode);

    when(orgSubUnitRepositoryMock.existsById(orgSubUnitId))
      .thenReturn(true);

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1"))
      .thenReturn(Optional.empty());

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId2"))
      .thenReturn(Optional.empty());

    assertDoesNotThrow(() -> service.addOperatorsToOrgSubUnit(organizationId, subUnitCode, mappedExternalUserIds));

    verify(orgSubUnitRepositoryMock).existsById(orgSubUnitId);
    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1");
    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId2");

    ArgumentCaptor<OrgSubUnitOperators> captor =
      ArgumentCaptor.forClass(OrgSubUnitOperators.class);

    verify(orgSubUnitOperatorsRepositoryMock, times(2)).save(captor.capture());

    List<OrgSubUnitOperators> savedAssociations = captor.getAllValues();

    assertEquals(
      Set.of("userId1", "userId2"),
      savedAssociations.stream()
        .map(OrgSubUnitOperators::getOperatorExternalUserId)
        .collect(Collectors.toSet())
    );

    savedAssociations.forEach(association -> {
      assertEquals(organizationId, association.getOrganizationId());
      assertEquals(subUnitCode, association.getSubUnitCode());
    });
  }

  @Test
  void givenAlreadyExistingAssociationWhenAddOperatorsToOrgSubUnitThenIgnoreIt() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_UNIT_1";
    List<String> mappedExternalUserIds = List.of("userId1", "userId2");

    OrgSubUnit.OrgSubUnitId orgSubUnitId = new OrgSubUnit.OrgSubUnitId(organizationId, subUnitCode);

    when(orgSubUnitRepositoryMock.existsById(orgSubUnitId))
      .thenReturn(true);

    OrgSubUnitOperators existingAssociation = new OrgSubUnitOperators();
    existingAssociation.setOrganizationId(organizationId);
    existingAssociation.setSubUnitCode(subUnitCode);
    existingAssociation.setOperatorExternalUserId("userId1");

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1"))
      .thenReturn(Optional.of(existingAssociation));

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId2"))
      .thenReturn(Optional.empty());

    assertDoesNotThrow(() -> service.addOperatorsToOrgSubUnit(organizationId, subUnitCode, mappedExternalUserIds));

    verify(orgSubUnitRepositoryMock).existsById(orgSubUnitId);
    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1");
    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId2");

    ArgumentCaptor<OrgSubUnitOperators> captor = ArgumentCaptor.forClass(OrgSubUnitOperators.class);

    verify(orgSubUnitOperatorsRepositoryMock).save(captor.capture());

    OrgSubUnitOperators savedAssociation = captor.getValue();

    assertEquals(organizationId, savedAssociation.getOrganizationId());
    assertEquals(subUnitCode, savedAssociation.getSubUnitCode());
    assertEquals("userId2", savedAssociation.getOperatorExternalUserId());
  }

  @Test
  void givenAllAssociationsAlreadyExistWhenAddOperatorsToOrgSubUnitThenOk() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_UNIT_1";
    List<String> mappedExternalUserIds = List.of("userId1", "userId2");

    OrgSubUnit.OrgSubUnitId orgSubUnitId = new OrgSubUnit.OrgSubUnitId(organizationId, subUnitCode);

    when(orgSubUnitRepositoryMock.existsById(orgSubUnitId))
      .thenReturn(true);

    OrgSubUnitOperators firstAssociation = new OrgSubUnitOperators();
    OrgSubUnitOperators secondAssociation = new OrgSubUnitOperators();

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1"))
      .thenReturn(Optional.of(firstAssociation));

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId2"))
      .thenReturn(Optional.of(secondAssociation));

    assertDoesNotThrow(() -> service.addOperatorsToOrgSubUnit(organizationId, subUnitCode, mappedExternalUserIds));

    verify(orgSubUnitRepositoryMock).existsById(orgSubUnitId);
    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1");
    verify(orgSubUnitOperatorsRepositoryMock).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId2");
    verify(orgSubUnitOperatorsRepositoryMock, never()).save(any(OrgSubUnitOperators.class));
  }

  @Test
  void givenMissingOrgSubUnitWhenAddOperatorsToOrgSubUnitThenBadRequestException() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_UNIT_1";
    List<String> mappedExternalUserIds = List.of("userId1", "userId2");

    OrgSubUnit.OrgSubUnitId orgSubUnitId = new OrgSubUnit.OrgSubUnitId(organizationId, subUnitCode);

    when(orgSubUnitRepositoryMock.existsById(orgSubUnitId))
      .thenReturn(false);

    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class,
      () -> service.addOperatorsToOrgSubUnit(organizationId, subUnitCode, mappedExternalUserIds));

    assertEquals(400, exception.getStatusCode().value());
    verify(orgSubUnitRepositoryMock).existsById(orgSubUnitId);
  }

  @Test
  void givenDuplicatedMappedExternalUserIdsWhenAddOperatorsToOrgSubUnitThenProcessOnce() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_UNIT_1";
    List<String> mappedExternalUserIds = List.of("userId1", "userId1");

    OrgSubUnit.OrgSubUnitId orgSubUnitId = new OrgSubUnit.OrgSubUnitId(organizationId, subUnitCode);

    when(orgSubUnitRepositoryMock.existsById(orgSubUnitId))
      .thenReturn(true);

    when(orgSubUnitOperatorsRepositoryMock
      .findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1"))
      .thenReturn(Optional.empty());

    assertDoesNotThrow(() -> service.addOperatorsToOrgSubUnit(organizationId, subUnitCode, mappedExternalUserIds));

    verify(orgSubUnitRepositoryMock).existsById(orgSubUnitId);
    verify(orgSubUnitOperatorsRepositoryMock, times(1)).findByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1");
    verify(orgSubUnitOperatorsRepositoryMock, times(1)).save(any(OrgSubUnitOperators.class));
  }

  @Test
  void whenDeleteOperatorsFromOrgSubUnitThenOk() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_UNIT_1";
    List<String> mappedExternalUserIds = List.of("userId1", "userId2");

    assertDoesNotThrow(() -> service.deleteOperatorsFromOrgSubUnit(organizationId, subUnitCode, mappedExternalUserIds));
    verify(orgSubUnitOperatorsRepositoryMock).deleteByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1");
    verify(orgSubUnitOperatorsRepositoryMock).deleteByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId2");
  }

  @Test
  void givenDuplicatedMappedExternalUserIdsWhenDeleteOperatorsFromOrgSubUnitThenProcessOnce() {
    Long organizationId = 1L;
    String subUnitCode = "SUB_UNIT_1";
    List<String> mappedExternalUserIds = List.of("userId1", "userId1");

    assertDoesNotThrow(() -> service.deleteOperatorsFromOrgSubUnit(organizationId, subUnitCode, mappedExternalUserIds));
    verify(orgSubUnitOperatorsRepositoryMock, times(1))
      .deleteByOrganizationIdAndSubUnitCodeAndOperatorExternalUserId(organizationId, subUnitCode, "userId1");
  }
}
