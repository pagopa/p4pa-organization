package it.gov.pagopa.pu.organization.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.pu.organization.model.DistinctOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.service.TaxonomyService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = TaxonomyController.class, excludeFilters = {
  @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE)
})
@AutoConfigureMockMvc(addFilters = false)
class TaxonomyControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private TaxonomyService taxonomyServiceMock;
  @InjectMocks
  private TaxonomyController taxonomyController;

  @Test
  void getDistinctOrgType () throws Exception {
    List<DistinctOrganizationTypeDTO> orgTypes = new ArrayList<DistinctOrganizationTypeDTO>();
    orgTypes.add(0, new DistinctOrganizationTypeDTO() {
      @Override
      public String getOrganizationType () {
        return "test";
      }

      @Override
      public String getOrganizationTypeDescription () {
        return "test";
      }
    });

    Mockito.when(taxonomyServiceMock.getDistinctOrganizationType(Sort.by(Order.asc("organizationType")))).thenReturn(orgTypes);

    mockMvc.perform(get("/taxonomy/custom/getDistinctOrganizationType")
        .param("sort","organizationType,ASC")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().json(
        "[{\"organizationType\":\"test\",\"organizationTypeDescription\":\"test\"}]"));
  }
}
