package it.gov.pagopa.pu.organization.util.faker;

import it.gov.pagopa.pu.organization.model.Taxonomy;

import java.time.OffsetDateTime;

public class TaxonomyFaker {

  public static Taxonomy taxonomyBuilder(Long id) {
    Taxonomy taxonomy = new Taxonomy();
    taxonomy.setTaxonomyCode("taxonomyCode" + id);
    taxonomy.setOrganizationType("organizationType" + id);
    taxonomy.setOrganizationTypeDescription("organizationTypeDescription" + id);
    taxonomy.setMacroAreaCode("macroAreaCode" + id);
    taxonomy.setMacroAreaName("macroAreaName" + id);
    taxonomy.setMacroAreaDescription("macroAreaDescription" + id);
    taxonomy.setServiceTypeCode("serviceTypeCode" + id);
    taxonomy.setServiceType("serviceType" + id);
    taxonomy.setServiceTypeDescription("serviceTypeDescription" + id);
    taxonomy.setCollectionReason("collectionReason" + id);
    taxonomy.setStartDateValidity(OffsetDateTime.now());
    taxonomy.setEndDateOfValidity(OffsetDateTime.now().plusDays(1));
    return taxonomy;
  }

}
