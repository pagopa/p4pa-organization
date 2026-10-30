package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PagedPdndClientView;
import it.gov.pagopa.pu.organization.model.view.PdndClientView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class PdndClientViewMapper {

  public PagedPdndClientView mapToPagedPdndClientView(Page<PdndClientView> pdndClientViewPage) {
    PagedPdndClientView mappedPdndClientView = new PagedPdndClientView();
    if (pdndClientViewPage != null) {
      if (!pdndClientViewPage.getContent().isEmpty()) {
        mappedPdndClientView.setContent(pdndClientViewPage.getContent());
      } else {
        mappedPdndClientView.setContent(Collections.emptyList());
      }

      if (pdndClientViewPage.getPageable().isPaged()) {
        mappedPdndClientView.setTotalPages((long) pdndClientViewPage.getTotalPages());
        mappedPdndClientView.setSize((long) pdndClientViewPage.getSize());
        mappedPdndClientView.setNumber((long) pdndClientViewPage.getNumber());
        mappedPdndClientView.setTotalElements(pdndClientViewPage.getTotalElements());
      }
    }
    return mappedPdndClientView;
  }
}
