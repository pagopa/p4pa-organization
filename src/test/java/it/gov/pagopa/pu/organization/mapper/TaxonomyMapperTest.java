package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.model.Taxonomy;
import it.gov.pagopa.pu.organization.util.TestUtils;
import it.gov.pagopa.pu.pagopapayments.dto.generated.TaxonomyDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaxonomyMapperTest {

  private final TaxonomyMapper mapper = new TaxonomyMapper();

  @Test
  void toModel_withValidTaxonomyDTO_returnsTaxonomy() {
    TaxonomyDTO dto = new TaxonomyDTO();
    dto.setOrganizationType("orgType");
    dto.setOrganizationTypeDescription("orgDescription");
    dto.setMacroAreaCode("macroAreaCode");
    dto.setMacroAreaName("macroAreaName");
    dto.setMacroAreaDescription("macroAreaDescription");
    dto.setServiceTypeCode("serviceCode");
    dto.setServiceType("service");
    dto.setServiceTypeDescription("serviceDescription");
    dto.setCollectionReason("reason");
    dto.setStartDateValidity(OffsetDateTime.now());
    dto.setEndDateOfValidity(OffsetDateTime.now().plusDays(1));
    dto.setTaxonomyCode("taxonomyCode");

    Taxonomy result = mapper.toModel(dto);

    assertNotNull(result);
    assertEquals("orgType", result.getOrganizationType());
    assertEquals("orgDescription", result.getOrganizationTypeDescription());
    assertEquals("macroAreaCode", result.getMacroAreaCode());
    assertEquals("macroAreaName", result.getMacroAreaName());
    assertEquals("macroAreaDescription", result.getMacroAreaDescription());
    assertEquals("serviceCode", result.getServiceTypeCode());
    assertEquals("service", result.getServiceType());
    assertEquals("serviceDescription", result.getServiceTypeDescription());
    assertEquals("reason", result.getCollectionReason());
    assertEquals(dto.getStartDateValidity(), result.getStartDateValidity());
    assertEquals(dto.getEndDateOfValidity(), result.getEndDateOfValidity());
    assertEquals("taxonomyCode", result.getTaxonomyCode());

    TestUtils.checkNotNullFields(result, "taxonomyId", "creationDate", "updateDate", "updateOperatorExternalId");
  }

  @Test
  void toModel_withNullTaxonomyDTO_returnsNull() {
    Taxonomy result = mapper.toModel(null);
    assertNull(result);
  }

  @Test
  void toModel_withEmptyTaxonomyDTO_returnsTaxonomyWithNullFields() {
    TaxonomyDTO dto = new TaxonomyDTO();
    Taxonomy result = mapper.toModel(dto);

    assertNotNull(result);
    assertNull(result.getOrganizationType());
    assertNull(result.getOrganizationTypeDescription());
    assertNull(result.getMacroAreaCode());
    assertNull(result.getMacroAreaName());
    assertNull(result.getMacroAreaDescription());
    assertNull(result.getServiceTypeCode());
    assertNull(result.getServiceType());
    assertNull(result.getServiceTypeDescription());
    assertNull(result.getCollectionReason());
    assertNull(result.getStartDateValidity());
    assertNull(result.getEndDateOfValidity());
    assertNull(result.getTaxonomyCode());
  }
}
