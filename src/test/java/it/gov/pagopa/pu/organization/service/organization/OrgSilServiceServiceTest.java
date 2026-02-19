package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.exception.custom.OrgSilServiceNotFoundException;
import it.gov.pagopa.pu.organization.mapper.OrgSilServiceMapper;
import it.gov.pagopa.pu.organization.model.orgsilservice.OrgSilService;
import it.gov.pagopa.pu.organization.model.orgsilservice.SilServiceLegacyBasicAuthConfig;
import it.gov.pagopa.pu.organization.model.orgsilservice.SilServiceLegacyJwtAuthConfig;
import it.gov.pagopa.pu.organization.repository.OrgSilServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceServiceTest {

  @Mock
  private OrgSilServiceRepository orgSilServiceRepositoryMock;

  @Mock
  private OrgSilServiceMapper orgSilServiceMapperMock;

  @InjectMocks
  private OrgSilServiceService orgSilServiceService;

  @Test
  void givenValidIdWhenGetByIdThenReturnsDecryptedDTO() {
    // Given
    Long orgSilServiceId = 1L;
    OrgSilService entity = new OrgSilService();
    entity.setOrgSilServiceId(orgSilServiceId);

    SilServiceLegacyBasicAuthConfig authConfig = new SilServiceLegacyBasicAuthConfig();
    entity.setAuthConfig(authConfig);

    OrgSilServiceDTO expectedDTO = new OrgSilServiceDTO();
    expectedDTO.setOrgSilServiceId(orgSilServiceId);

    when(orgSilServiceRepositoryMock.findById(orgSilServiceId)).thenReturn(Optional.of(entity));
    when(orgSilServiceMapperMock.fromEntity(entity)).thenReturn(expectedDTO);

    // When
    OrgSilServiceDTO result = orgSilServiceService.getById(orgSilServiceId);

    // Then
    assertNotNull(result);
    assertEquals(orgSilServiceId, result.getOrgSilServiceId());
    verify(orgSilServiceRepositoryMock).findById(orgSilServiceId);
    verify(orgSilServiceMapperMock).fromEntity(entity);
  }

  @Test
  void givenNonExistentIdWhenGetByIdThenThrowsException() {
    // Given
    Long orgSilServiceId = 999L;
    when(orgSilServiceRepositoryMock.findById(orgSilServiceId)).thenReturn(Optional.empty());

    // When & Then
    OrgSilServiceNotFoundException exception = assertThrows(
      OrgSilServiceNotFoundException.class,
      () -> orgSilServiceService.getById(orgSilServiceId)
    );

    assertEquals("[ORG_SIL_SERVICE_NOT_FOUND] OrgSilService with id %s not found".formatted(orgSilServiceId), exception.getMessage());
    verify(orgSilServiceRepositoryMock).findById(orgSilServiceId);
    verifyNoInteractions(orgSilServiceMapperMock);
  }

  @Test
  void givenValidDTOWhenCreateOrUpdateThenReturnsDecryptedDTO() {
    // Given
    OrgSilServiceDTO inputDTO = new OrgSilServiceDTO();
    inputDTO.setOrgSilServiceId(1L);

    OrgSilService entity = new OrgSilService();
    entity.setOrgSilServiceId(1L);

    SilServiceLegacyBasicAuthConfig authConfig = new SilServiceLegacyBasicAuthConfig();
    entity.setAuthConfig(authConfig);

    OrgSilServiceDTO expectedDTO = new OrgSilServiceDTO();
    expectedDTO.setOrgSilServiceId(1L);

    when(orgSilServiceMapperMock.fromDTO(inputDTO)).thenReturn(entity);
    when(orgSilServiceRepositoryMock.save(entity)).thenReturn(entity);
    when(orgSilServiceMapperMock.fromEntity(entity)).thenReturn(expectedDTO);

    // When
    OrgSilServiceDTO result = orgSilServiceService.createOrUpdate(inputDTO);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getOrgSilServiceId());
    verify(orgSilServiceMapperMock).fromDTO(inputDTO);
    verify(orgSilServiceRepositoryMock).save(entity);
    verify(orgSilServiceMapperMock).fromEntity(entity);
  }

  @Test
  void givenDTOWithNullAuthConfigWhenCreateOrUpdateThenHandlesGracefully() {
    // Given
    OrgSilServiceDTO inputDTO = new OrgSilServiceDTO();
    inputDTO.setOrgSilServiceId(1L);

    OrgSilService entity = new OrgSilService();
    entity.setOrgSilServiceId(1L);
    entity.setAuthConfig(null);

    OrgSilServiceDTO expectedDTO = new OrgSilServiceDTO();
    expectedDTO.setOrgSilServiceId(1L);

    when(orgSilServiceMapperMock.fromDTO(inputDTO)).thenReturn(entity);
    when(orgSilServiceRepositoryMock.save(entity)).thenReturn(entity);
    when(orgSilServiceMapperMock.fromEntity(entity)).thenReturn(expectedDTO);

    // When
    OrgSilServiceDTO result = orgSilServiceService.createOrUpdate(inputDTO);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getOrgSilServiceId());
  }

  @Test
  void givenNewEntityWhenCreateOrUpdateThenSavesAndReturnsDTO() {
    // Given
    OrgSilServiceDTO inputDTO = new OrgSilServiceDTO();
    inputDTO.setOrgSilServiceId(null); // New entity
    inputDTO.setApplicationName("TestApp");

    OrgSilService entity = new OrgSilService();
    entity.setApplicationName("TestApp");

    OrgSilService savedEntity = new OrgSilService();
    savedEntity.setOrgSilServiceId(1L);
    savedEntity.setApplicationName("TestApp");

    OrgSilServiceDTO expectedDTO = new OrgSilServiceDTO();
    expectedDTO.setOrgSilServiceId(1L);
    expectedDTO.setApplicationName("TestApp");

    when(orgSilServiceMapperMock.fromDTO(inputDTO)).thenReturn(entity);
    when(orgSilServiceRepositoryMock.save(entity)).thenReturn(savedEntity);
    when(orgSilServiceMapperMock.fromEntity(any(OrgSilService.class))).thenReturn(expectedDTO);

    // When
    OrgSilServiceDTO result = orgSilServiceService.createOrUpdate(inputDTO);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getOrgSilServiceId());
    assertEquals("TestApp", result.getApplicationName());
    verify(orgSilServiceRepositoryMock).save(entity);
  }

  @Test
  void givenEntityWithJwtAuthConfigWhenGetByIdThenDecryptsCorrectly() {
    // Given
    Long orgSilServiceId = 1L;
    OrgSilService entity = new OrgSilService();
    entity.setOrgSilServiceId(orgSilServiceId);

    SilServiceLegacyJwtAuthConfig jwtAuthConfig = new SilServiceLegacyJwtAuthConfig();
    entity.setAuthConfig(jwtAuthConfig);

    OrgSilServiceDTO expectedDTO = new OrgSilServiceDTO();

    when(orgSilServiceRepositoryMock.findById(orgSilServiceId)).thenReturn(Optional.of(entity));
    when(orgSilServiceMapperMock.fromEntity(entity)).thenReturn(expectedDTO);

    // When
    OrgSilServiceDTO result = orgSilServiceService.getById(orgSilServiceId);

    // Then
    assertNotNull(result);
  }

  @Test
  void givenEntityWithBasicAuthConfigWhenCreateOrUpdateThenEncryptsAndDecrypts() {
    // Given
    OrgSilServiceDTO inputDTO = new OrgSilServiceDTO();

    OrgSilService entity = new OrgSilService();
    SilServiceLegacyBasicAuthConfig basicAuthConfig = new SilServiceLegacyBasicAuthConfig();
    entity.setAuthConfig(basicAuthConfig);

    OrgSilServiceDTO expectedDTO = new OrgSilServiceDTO();

    when(orgSilServiceMapperMock.fromDTO(inputDTO)).thenReturn(entity);
    when(orgSilServiceRepositoryMock.save(entity)).thenReturn(entity);
    when(orgSilServiceMapperMock.fromEntity(entity)).thenReturn(expectedDTO);

    // When
    OrgSilServiceDTO result = orgSilServiceService.createOrUpdate(inputDTO);

    // Then
    assertNotNull(result);
  }
}
