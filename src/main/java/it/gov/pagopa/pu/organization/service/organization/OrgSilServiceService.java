package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.exception.custom.OrgSilServiceNotFoundException;
import it.gov.pagopa.pu.organization.mapper.OrgSilServiceMapper;
import it.gov.pagopa.pu.organization.model.orgsilservice.OrgSilService;
import it.gov.pagopa.pu.organization.repository.OrgSilServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrgSilServiceService {

  private final OrgSilServiceRepository orgSilServiceRepository;
  private final OrgSilServiceMapper orgSilServiceMapper;

  public OrgSilServiceDTO getById(Long orgSilServiceId) {
    OrgSilService orgSilService = orgSilServiceRepository.findById(orgSilServiceId).orElseThrow(() ->
      new OrgSilServiceNotFoundException("OrgSilService not found with ID: " + orgSilServiceId));
    return orgSilServiceMapper.fromEntity(orgSilService);
  }

  public OrgSilServiceDTO createOrUpdate(OrgSilServiceDTO orgSilServiceDTO) {
    OrgSilService orgSilService = orgSilServiceMapper.fromDTO(orgSilServiceDTO);
    orgSilServiceRepository.save(orgSilService);

    return orgSilServiceMapper.fromEntity(orgSilService);
  }

}
