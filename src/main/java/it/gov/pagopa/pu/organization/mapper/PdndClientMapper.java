package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.service.organization.OrganizationEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PdndClientMapper {
  private final OrganizationEncryptionService encryptionService;

  public PdndClient toModel(PdndClientDTO pdndClientDTO) {
    if (pdndClientDTO == null) {
      return null;
    }

    PdndClient pdndClient = new PdndClient();
    pdndClient.setClientId(pdndClientDTO.getClientId());
    pdndClient.setOrganizationId(pdndClientDTO.getOrganizationId());
    pdndClient.setSubUnitCode(pdndClientDTO.getSubUnitCode());
    pdndClient.setClientName(pdndClientDTO.getClientName());
    pdndClient.setKid(pdndClientDTO.getKid());
    pdndClient.setPrivateKeyCipher(encryptionService.encrypt(pdndClientDTO.getPrivateKey()));
    pdndClient.setPublicKey(pdndClientDTO.getPublicKey());
    return pdndClient;
  }
}
