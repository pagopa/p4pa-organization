package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.model.Taxonomy;
import it.gov.pagopa.pu.pagopapayments.dto.generated.TaxonomyDTO;
import org.springframework.stereotype.Component;

@Component
public class TaxonomyMapper {

  public Taxonomy toModel(TaxonomyDTO taxonomyDTO) {
    if (taxonomyDTO == null) {
      return null;
    }

    Taxonomy taxonomy = new Taxonomy();
    taxonomy.setOrganizationType(taxonomyDTO.getOrganizationType());
    taxonomy.setOrganizationTypeDescription(taxonomyDTO.getOrganizationTypeDescription());
    taxonomy.setMacroAreaCode(taxonomyDTO.getMacroAreaCode());
    taxonomy.setMacroAreaName(taxonomyDTO.getMacroAreaName());
    taxonomy.setMacroAreaDescription(taxonomyDTO.getMacroAreaDescription());
    taxonomy.setServiceTypeCode(taxonomyDTO.getServiceTypeCode());
    taxonomy.setServiceType(taxonomyDTO.getServiceType());
    taxonomy.setServiceTypeDescription(taxonomyDTO.getServiceTypeDescription());
    taxonomy.setCollectionReason(taxonomyDTO.getCollectionReason());
    taxonomy.setStartDateValidity(taxonomyDTO.getStartDateValidity());
    taxonomy.setEndDateOfValidity(taxonomyDTO.getEndDateOfValidity());
    taxonomy.setTaxonomyCode(taxonomyDTO.getTaxonomyCode());

    return taxonomy;
  }
}
