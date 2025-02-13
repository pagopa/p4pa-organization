package it.gov.pagopa.pu.organization.controller;

import it.gov.pagopa.pu.organization.controller.generated.TaxonomyApi;
import it.gov.pagopa.pu.organization.service.taxonomy.TaxonomyService;
import it.gov.pagopa.pu.organization.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class TaxonomyController implements TaxonomyApi {

  private final TaxonomyService taxonomyService;

  public TaxonomyController(TaxonomyService taxonomyService){
    this.taxonomyService = taxonomyService;
  }


  @Override
  public ResponseEntity<Void> syncTaxonomies() {
    log.info("invoking synchTaxonomies");
    taxonomyService.synchTaxonomies(SecurityUtils.getAccessToken());
    //todo to be implemented in P4ADEV-2150
    return ResponseEntity.ofNullable(null);
  }

}
