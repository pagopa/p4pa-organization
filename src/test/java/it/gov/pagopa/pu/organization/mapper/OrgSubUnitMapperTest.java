package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrgSubUnitDTO;
import it.gov.pagopa.pu.organization.dto.generated.PagedOrgSubUnit;
import it.gov.pagopa.pu.organization.enums.OrgSubUnitStatus;
import it.gov.pagopa.pu.organization.enums.SubUnitType;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

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
  void mapToPagedOrgAndSubUnit() {
    //GIVEN
    Page<OrgSubUnit> orgSubUnitModelPage = new PageImpl<>(List.of(buildOrgSubUnitModel()), PageRequest.of(0, 10), 1);
    //WHEN
    PagedOrgSubUnit pagedOrgSubUnit = orgSubUnitMapper.mapToPagedOrgAndSubUnit(orgSubUnitModelPage);
    //THEN
    assertNotNull(pagedOrgSubUnit);
    assertEquals(orgSubUnitModelPage.getTotalElements(), pagedOrgSubUnit.getTotalElements());
    assertEquals(orgSubUnitModelPage.getTotalPages(), pagedOrgSubUnit.getTotalPages());
    assertEquals(orgSubUnitModelPage.getSize(), pagedOrgSubUnit.getSize());
    assertEquals(orgSubUnitModelPage.getNumber(), pagedOrgSubUnit.getNumber());
    assertOnOrgSubUnitFields(orgSubUnitModelPage.getContent().getFirst(), pagedOrgSubUnit.getContent().getFirst());
  }

  @Test
  void mapToOrgSubUnitDTOList() {
    //GIVEN
    List<OrgSubUnit> orgSubUnitModelList = List.of(buildOrgSubUnitModel());
    //WHEN
    List<OrgSubUnitDTO> orgSubUnitDTOList = orgSubUnitMapper.mapToOrgSubUnitDTOList(orgSubUnitModelList);
    //THEN
    assertNotNull(orgSubUnitDTOList);
    assertFalse(orgSubUnitDTOList.isEmpty());
    assertEquals(orgSubUnitModelList.size(), orgSubUnitDTOList.size());
    assertOnOrgSubUnitFields(orgSubUnitModelList.getFirst(), orgSubUnitDTOList.getFirst());
  }

  @Test
  void mapToOrgSubUnitDTO() {
    //GIVEN
    OrgSubUnit orgSubUnitModel = buildOrgSubUnitModel();
    //WHEN
    OrgSubUnitDTO orgSubUnitDTO = orgSubUnitMapper.mapToOrgSubUnitDTO(orgSubUnitModel);
    //THEN
    assertNotNull(orgSubUnitDTO);
    checkNotNullFields(orgSubUnitDTO);
    assertOnOrgSubUnitFields(orgSubUnitModel, orgSubUnitDTO);
  }

  @Test
  void mapIdIntoPageable() {
    //GIVEN
    Sort sort = Sort.by(
      Sort.Order.asc(OrgSubUnit.OrgSubUnitId.Fields.organizationId),
      Sort.Order.asc(OrgSubUnit.OrgSubUnitId.Fields.subUnitCode),
      Sort.Order.asc("notOrganizationIdNorSubUnitCode")
    );
    Pageable pageable = PageRequest.of(0, 10, sort);
    //WHEN
    Pageable mappedPageable = orgSubUnitMapper.mapCompositeIdProperties(pageable);
    //THEN
    assertTrue(mappedPageable.getSort().stream().anyMatch(order ->
      "id.%s".formatted(OrgSubUnit.OrgSubUnitId.Fields.organizationId).equals(order.getProperty())
    ));
    assertTrue(mappedPageable.getSort().stream().anyMatch(order ->
      "id.%s".formatted(OrgSubUnit.OrgSubUnitId.Fields.subUnitCode).equals(order.getProperty())
    ));
    assertTrue(mappedPageable.getSort().stream().anyMatch(order ->
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
