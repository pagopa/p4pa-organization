package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.model.DistinctOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.service.TaxonomyService;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaxonomyController {

  TaxonomyService taxonomyService;

  public TaxonomyController(TaxonomyService taxonomyService){
    this.taxonomyService = taxonomyService;
  }

  @GetMapping("/taxonomy/custom/getDistinctOrganizationType")
  public ResponseEntity<List<DistinctOrganizationTypeDTO>> getDistinctOrgType(Sort sort){
    return new ResponseEntity<>(taxonomyService.getDistinctOrganizationType(sort),
      HttpStatusCode.valueOf(200));
  }

}
