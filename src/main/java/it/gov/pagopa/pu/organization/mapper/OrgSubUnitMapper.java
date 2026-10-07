package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.OrgSubUnitDTO;
import it.gov.pagopa.pu.organization.dto.generated.PagedOrgSubUnit;
import it.gov.pagopa.pu.organization.model.OrgSubUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrgSubUnitMapper {

  public PagedOrgSubUnit mapToPagedOrgAndSubUnit(Page<OrgSubUnit> orgSubUnitModelPage) {
    PagedOrgSubUnit pagedOrgSubUnit = new PagedOrgSubUnit();
    if (orgSubUnitModelPage != null) {
      if (!orgSubUnitModelPage.getContent().isEmpty()) {
        pagedOrgSubUnit.setContent(mapToOrgSubUnitDTOList(orgSubUnitModelPage.getContent()));
      } else {
        pagedOrgSubUnit.setContent(Collections.emptyList());
      }

      if (orgSubUnitModelPage.getPageable().isPaged()) {
        pagedOrgSubUnit.setTotalPages((long) orgSubUnitModelPage.getTotalPages());
        pagedOrgSubUnit.setSize((long) orgSubUnitModelPage.getSize());
        pagedOrgSubUnit.setNumber((long) orgSubUnitModelPage.getNumber());
        pagedOrgSubUnit.setTotalElements(orgSubUnitModelPage.getTotalElements());
      }
    }
    return pagedOrgSubUnit;
  }

  public List<OrgSubUnitDTO> mapToOrgSubUnitDTOList(List<OrgSubUnit> orgSubUnitModelList) {
    return orgSubUnitModelList.stream().map(this::mapToOrgSubUnitDTO).toList();
  }

  public OrgSubUnitDTO mapToOrgSubUnitDTO(OrgSubUnit orgSubUnitModel) {
    return OrgSubUnitDTO.builder()
      .organizationId(orgSubUnitModel.getId().getOrganizationId())
      .subUnitCode(orgSubUnitModel.getId().getSubUnitCode())
      .subUnitType(orgSubUnitModel.getSubUnitType())
      .subUnitName(orgSubUnitModel.getSubUnitName())
      .build();
  }

  public Pageable mapCompositeIdProperties(Pageable pageable) {
    Sort mappedSort = Sort.by(pageable.getSort().map(order -> {
      String orderProperty = order.getProperty();
      if(OrgSubUnit.OrgSubUnitId.Fields.organizationId.equals(orderProperty)
        || OrgSubUnit.OrgSubUnitId.Fields.subUnitCode.equals(orderProperty)) {
        orderProperty = "id.%s".formatted(orderProperty);
      }
      return order.withProperty(orderProperty);
    }).toList());
    return PageRequest.of(
      pageable.getPageNumber(),
      pageable.getPageSize(),
      mappedSort
    );
  }

}
