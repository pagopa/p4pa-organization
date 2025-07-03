package it.gov.pagopa.pu.organization.service.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.exception.custom.OrgSilServiceNotFoundException;
import it.gov.pagopa.pu.organization.mapper.OrgSilServiceMapper;
import it.gov.pagopa.pu.organization.model.OrgSilService;
import it.gov.pagopa.pu.organization.repository.OrgSilServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationSilService {

  private final OrgSilServiceRepository orgSilServiceRepository;
  private final OrgSilServiceMapper orgSilServiceMapper;
  private final OrgSilServiceEncryptionService encryptionService;

  public OrgSilServiceDTO getById(Long orgSilServiceId) {
    log.debug("Retrieving OrgSilService with ID: {}", orgSilServiceId);
    OrgSilService orgSilService = orgSilServiceRepository.findById(orgSilServiceId).orElseThrow(() ->
      new OrgSilServiceNotFoundException("OrgSilService not found with ID: " + orgSilServiceId));
    encryptionService.decryptAuthConfig(orgSilService.getAuthConfig());
    return orgSilServiceMapper.fromEntity(orgSilService);
  }

  public OrgSilServiceDTO createOrUpdate(OrgSilServiceDTO orgSilServiceDTO) {
    log.debug("Creating OrgSilService: {}", orgSilServiceDTO);
    OrgSilService orgSilService = orgSilServiceMapper.fromDTO(orgSilServiceDTO);
    encryptionService.encryptAuthConfig(orgSilService.getAuthConfig());
    orgSilServiceRepository.save(orgSilService);
    encryptionService.decryptAuthConfig(orgSilService.getAuthConfig());

    return orgSilServiceMapper.fromEntity(orgSilService);
  }

}
