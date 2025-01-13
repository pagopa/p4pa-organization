package it.gov.pagopa.pu.organization.service;

import it.gov.pagopa.pu.organization.model.DistinctOrganizationTypeDTO;
import it.gov.pagopa.pu.organization.repository.TaxonomyRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyServiceImpl implements TaxonomyService{
  private TaxonomyRepository taxonomyRepository;
  public TaxonomyServiceImpl(TaxonomyRepository taxonomyRepository){
    this.taxonomyRepository = taxonomyRepository;
  }

  public List<DistinctOrganizationTypeDTO> getDistinctOrganizationType(Sort sort){
    return taxonomyRepository.findDistinctOrganizationTypes(sort);
  }
}
