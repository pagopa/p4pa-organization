package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PagedPdndServiceView;
import it.gov.pagopa.pu.organization.model.view.PdndServiceView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class PdndServiceViewMapper {

  public PagedPdndServiceView mapToPagedPdndServiceView(Page<PdndServiceView> pdndServiceViewPage) {
    PagedPdndServiceView mappedPdndServiceView = new PagedPdndServiceView();
    if (pdndServiceViewPage != null) {
      if (!pdndServiceViewPage.getContent().isEmpty()) {
        mappedPdndServiceView.setContent(pdndServiceViewPage.getContent());
      } else {
        mappedPdndServiceView.setContent(Collections.emptyList());
      }

      if (pdndServiceViewPage.getPageable().isPaged()) {
        mappedPdndServiceView.setTotalPages((long) pdndServiceViewPage.getTotalPages());
        mappedPdndServiceView.setSize((long) pdndServiceViewPage.getSize());
        mappedPdndServiceView.setNumber((long) pdndServiceViewPage.getNumber());
        mappedPdndServiceView.setTotalElements(pdndServiceViewPage.getTotalElements());
      }
    }
    return mappedPdndServiceView;
  }
}
