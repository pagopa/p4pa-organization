package it.gov.pagopa.pu.organization.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.enums.OrgSilServiceType;
import it.gov.pagopa.pu.organization.model.OrgSilService;
import it.gov.pagopa.pu.organization.model.SilServiceLegacyBasicAuthConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrgSilServiceMapperTest {

  @Mock
  private ObjectMapper objectMapperMock;

  private OrgSilServiceMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new OrgSilServiceMapper(objectMapperMock);
  }

  @Test
  void givenValidEntityWhenFromEntityThenReturnsDTO() {
    // Given
    OrgSilService entity = new OrgSilService();
    entity.setOrgSilServiceId(1L);
    entity.setOrganizationId(100L);
    entity.setServiceType(OrgSilServiceType.ACTUALIZATION);
    entity.setServiceUrl("https://api.example.com");
    entity.setApplicationName("TestApp");
    entity.setFlagLegacy(true);
    entity.setAuthConfig(new SilServiceLegacyBasicAuthConfig());

    // When
    OrgSilServiceDTO result = mapper.fromEntity(entity);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getOrgSilServiceId());
    assertEquals(100L, result.getOrganizationId());
    assertEquals(OrgSilServiceType.ACTUALIZATION, result.getServiceType());
    assertEquals("https://api.example.com", result.getServiceUrl());
    assertEquals("TestApp", result.getApplicationName());
    assertTrue(result.getFlagLegacy());
    assertEquals(SilServiceLegacyBasicAuthConfig.class, result.getAuthConfig().getClass());
  }

  @Test
  void givenNullEntityWhenFromEntityThenReturnsNull() {
    // When
    OrgSilServiceDTO result = mapper.fromEntity(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenValidDTOWhenFromDTOThenReturnsEntity() {
    // Given
    OrgSilServiceDTO dto = new OrgSilServiceDTO();
    dto.setOrgSilServiceId(1L);
    dto.setOrganizationId(100L);
    dto.setServiceType(OrgSilServiceType.ACTUALIZATION);
    dto.setServiceUrl("https://api.example.com");
    dto.setApplicationName("TestApp");
    dto.setFlagLegacy(true);
    dto.setAuthConfig(new SilServiceLegacyBasicAuthConfig());

    // When
    OrgSilService result = mapper.fromDTO(dto);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getOrgSilServiceId());
    assertEquals(100L, result.getOrganizationId());
    assertEquals(OrgSilServiceType.ACTUALIZATION, result.getServiceType());
    assertEquals("https://api.example.com", result.getServiceUrl());
    assertEquals("TestApp", result.getApplicationName());
    assertTrue(result.isFlagLegacy());
    assertEquals(SilServiceLegacyBasicAuthConfig.class, result.getAuthConfig().getClass());
  }

  @Test
  void givenNullDTOWhenFromDTOThenReturnsNull() {
    // When
    OrgSilService result = mapper.fromDTO(null);

    // Then
    assertNull(result);
  }

  @Test
  void givenMinimalEntityWhenFromEntityThenReturnsDTO() {
    // Given
    OrgSilService entity = new OrgSilService();
    entity.setOrgSilServiceId(1L);
    entity.setFlagLegacy(false);

    // When
    OrgSilServiceDTO result = mapper.fromEntity(entity);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getOrgSilServiceId());
    assertFalse(result.getFlagLegacy());
    assertNull(result.getOrganizationId());
    assertNull(result.getServiceType());
    assertNull(result.getServiceUrl());
    assertNull(result.getApplicationName());
    assertNull(result.getAuthConfig());
  }

  @Test
  void givenMinimalDTOWhenFromDTOThenReturnsEntity() {
    // Given
    OrgSilServiceDTO dto = new OrgSilServiceDTO();
    dto.setOrgSilServiceId(1L);
    dto.setFlagLegacy(false);

    // When
    OrgSilService result = mapper.fromDTO(dto);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getOrgSilServiceId());
    assertFalse(result.isFlagLegacy());
    assertNull(result.getOrganizationId());
    assertNull(result.getServiceType());
    assertNull(result.getServiceUrl());
    assertNull(result.getApplicationName());
    assertNull(result.getAuthConfig());
  }

  @Test
  void givenCompleteEntityWhenRoundTripConversionThenDataIsPreserved() {
    // Given
    OrgSilService originalEntity = new OrgSilService();
    originalEntity.setOrgSilServiceId(1L);
    originalEntity.setOrganizationId(100L);
    originalEntity.setServiceType(OrgSilServiceType.ACTUALIZATION);
    originalEntity.setServiceUrl("https://api.example.com");
    originalEntity.setApplicationName("TestApp");
    originalEntity.setFlagLegacy(true);
    originalEntity.setAuthConfig(new SilServiceLegacyBasicAuthConfig());

    // When
    OrgSilServiceDTO dto = mapper.fromEntity(originalEntity);
    OrgSilService resultEntity = mapper.fromDTO(dto);

    // Then
    assertEquals(originalEntity.getOrgSilServiceId(), resultEntity.getOrgSilServiceId());
    assertEquals(originalEntity.getOrganizationId(), resultEntity.getOrganizationId());
    assertEquals(originalEntity.getServiceType(), resultEntity.getServiceType());
    assertEquals(originalEntity.getServiceUrl(), resultEntity.getServiceUrl());
    assertEquals(originalEntity.getApplicationName(), resultEntity.getApplicationName());
    assertEquals(originalEntity.isFlagLegacy(), resultEntity.isFlagLegacy());
    assertEquals(originalEntity.getAuthConfig(), resultEntity.getAuthConfig());
  }

}
