package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.model.DistinctOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.service.TaxonomyService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class TaxonomyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private TaxonomyService taxonomyServiceMock;

  @InjectMocks
  private TaxonomyController taxonomyController;

  @Test
  void getDistinctOrgType() {
    List<DistinctOrganizationTypeDTO> orgTypes = new ArrayList<>();
    orgTypes.add(new DistinctOrganizationTypeDTO() {
      @Override
      public String getOrganizationType() {
        return "test";
      }

      @Override
      public String getOrganizationTypeDescription() {
        return "test";
      }
    });

    Mockito.when(taxonomyServiceMock.getDistinctOrganizationType(Sort.by(Sort.Order.asc("organizationType")))).thenReturn(orgTypes);

    ResponseEntity<List<DistinctOrganizationTypeDTO>> orgTypesResult = taxonomyController.getDistinctOrgType(Sort.by(Sort.Order.asc("organizationType")));

    Assertions.assertEquals(200,orgTypesResult.getStatusCode().value());
    Assertions.assertEquals(orgTypes,orgTypesResult.getBody());
  }
}
