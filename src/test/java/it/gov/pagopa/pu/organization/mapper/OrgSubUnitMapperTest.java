package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrgSubUnitDTO;
import it.gov.pagopa.pu.organization.dto.generated.PagedOrgSubUnit;
import it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus;
import it.gov.pagopa.pu.organization.enums.SubUnitType;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.pu.organization.util.TestUtils.checkNotNullFields;
import static org.junit.jupiter.api.Assertions.*;

class OrgSubUnitMapperTest {

  private OrgSubUnitMapper orgSubUnitMapper;

  private static final long ORGANIZATION_ID = 1L;
  private static final String ORG_SUB_UNIT_CODE = "orgSubUnitCode";
  private static final String ORG_SUB_UNIT_NAME = "orgSubUnitName";

  @BeforeEach
  void setUp() {
    orgSubUnitMapper = new OrgSubUnitMapper();
  }

  @Test
  void givenOrgAndSubUnitPageWhenMapToPagedOrgAndSubUnitThenOk() {
    //GIVEN
    Page<OrgSubUnit> orgSubUnitModelPage = new PageImpl<>(List.of(buildOrgSubUnitModel()), PageRequest.of(0, 10), 1);
    //WHEN
    PagedOrgSubUnit result = orgSubUnitMapper.mapToPagedOrgAndSubUnit(orgSubUnitModelPage);
    //THEN
    assertNotNull(result);
    assertEquals(orgSubUnitModelPage.getTotalElements(), result.getTotalElements());
    assertEquals(orgSubUnitModelPage.getTotalPages(), result.getTotalPages());
    assertEquals(orgSubUnitModelPage.getSize(), result.getSize());
    assertEquals(orgSubUnitModelPage.getNumber(), result.getNumber());
    assertOnOrgSubUnitFields(orgSubUnitModelPage.getContent().getFirst(), result.getContent().getFirst());
  }

  @Test
  void givenPageWithNoPaginationInformationWhenMapToPagedOrgAndSubUnitThenOk() {
    //GIVEN
    Page<OrgSubUnit> orgSubUnitModelPage = new PageImpl<>(List.of(buildOrgSubUnitModel()));
    //WHEN
    PagedOrgSubUnit result = orgSubUnitMapper.mapToPagedOrgAndSubUnit(orgSubUnitModelPage);
    //THEN
    assertNotNull(result);
    assertNull(result.getTotalElements());
    assertNull(result.getTotalPages());
    assertNull(result.getSize());
    assertNull(result.getNumber());
    assertOnOrgSubUnitFields(orgSubUnitModelPage.getContent().getFirst(), result.getContent().getFirst());
  }

  @Test
  void givenEmptyPageWhenMapToPagedOrgAndSubUnitThenEmptyPagedResult() {
    //GIVEN
    Pageable pageable = PageRequest.of(0, 10);
    Page<OrgSubUnit> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
    //WHEN
    PagedOrgSubUnit result = orgSubUnitMapper.mapToPagedOrgAndSubUnit(emptyPage);
    //THEN
    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
    assertEquals(0L, result.getTotalElements());
    assertEquals(0L, result.getTotalPages());
    assertEquals(10L, result.getSize());
    assertEquals(0L, result.getNumber());
  }

  @Test
  void givenNullPageWhenMapToPagedOrgAndSubUnitThenReturnEmptyPaged() {
    //WHEN
    PagedOrgSubUnit result = orgSubUnitMapper.mapToPagedOrgAndSubUnit(null);
    //THEN
    assertNotNull(result);
    assertNull(result.getTotalElements());
    assertNull(result.getTotalPages());
    assertNull(result.getSize());
    assertNull(result.getNumber());
    assertTrue(result.getContent().isEmpty());
  }

  @Test
  void whenMapToOrgSubUnitDTOListThenOk() {
    //GIVEN
    List<OrgSubUnit> orgSubUnitModelList = List.of(buildOrgSubUnitModel());
    //WHEN
    List<OrgSubUnitDTO> result = orgSubUnitMapper.mapToOrgSubUnitDTOList(orgSubUnitModelList);
    //THEN
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(orgSubUnitModelList.size(), result.size());
    assertOnOrgSubUnitFields(orgSubUnitModelList.getFirst(), result.getFirst());
  }

  @Test
  void whenMapToOrgSubUnitDTOThenOk() {
    //GIVEN
    OrgSubUnit orgSubUnitModel = buildOrgSubUnitModel();
    //WHEN
    OrgSubUnitDTO result = orgSubUnitMapper.mapToOrgSubUnitDTO(orgSubUnitModel);
    //THEN
    assertNotNull(result);
    checkNotNullFields(result);
    assertOnOrgSubUnitFields(orgSubUnitModel, result);
  }

  @Test
  void whenMapCompositeIdPropertiesThenOk() {
    //GIVEN
    Sort sort = Sort.by(
      Sort.Order.asc(OrgSubUnit.OrgSubUnitId.Fields.organizationId),
      Sort.Order.asc(OrgSubUnit.OrgSubUnitId.Fields.subUnitCode),
      Sort.Order.asc("notOrganizationIdNorSubUnitCode")
    );
    Pageable pageable = PageRequest.of(0, 10, sort);
    //WHEN
    Pageable result = orgSubUnitMapper.mapCompositeIdProperties(pageable);
    //THEN
    assertTrue(result.getSort().stream().anyMatch(order ->
      "id.%s".formatted(OrgSubUnit.OrgSubUnitId.Fields.organizationId).equals(order.getProperty())
    ));
    assertTrue(result.getSort().stream().anyMatch(order ->
      "id.%s".formatted(OrgSubUnit.OrgSubUnitId.Fields.subUnitCode).equals(order.getProperty())
    ));
    assertTrue(result.getSort().stream().anyMatch(order ->
      "notOrganizationIdNorSubUnitCode".equals(order.getProperty())
    ));
  }

  private void assertOnOrgSubUnitFields(OrgSubUnit orgSubUnitModel, OrgSubUnitDTO orgSubUnitDTO) {
    assertEquals(orgSubUnitModel.getId().getOrganizationId(), orgSubUnitDTO.getOrganizationId());
    assertEquals(orgSubUnitModel.getId().getSubUnitCode(), orgSubUnitDTO.getSubUnitCode());
    assertEquals(orgSubUnitModel.getSubUnitName(), orgSubUnitDTO.getSubUnitName());
    assertEquals(orgSubUnitModel.getSubUnitType(), orgSubUnitDTO.getSubUnitType());
  }

  private OrgSubUnit buildOrgSubUnitModel() {
    OrgSubUnit orgSubUnitModel = new OrgSubUnit();
    orgSubUnitModel.setId(new OrgSubUnit.OrgSubUnitId(ORGANIZATION_ID, ORG_SUB_UNIT_CODE));
    orgSubUnitModel.setSubUnitName(ORG_SUB_UNIT_NAME);
    orgSubUnitModel.setSubUnitType(SubUnitType.UO);
    orgSubUnitModel.setStatus(OrgSubUnitStatus.ACTIVE);
    return orgSubUnitModel;
  }

}
