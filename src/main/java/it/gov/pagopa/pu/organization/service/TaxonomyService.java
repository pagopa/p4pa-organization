package it.gov.pagopa.pu.organization.service;

import it.gov.pagopa.pu.organization.dto.DistinctOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyService {
  private TaxonomyRepository taxonomyRepository;
  public TaxonomyService(TaxonomyRepository taxonomyRepository){
    this.taxonomyRepository = taxonomyRepository;
  }

  public List<DistinctOrganizationTypeDTO> getDistinctOrganizationType(){
    return null;//taxonomyRepository.findDistinctOrganizationTypeBy();
  }

}
