package it.gov.pagopa.pu.organization.mapper;

import it.gov.pagopa.pu.organization.dto.generated.PdndClientDTO;
import it.gov.pagopa.pu.organization.dto.generated.PdndClientNoSecretDTO;
import it.gov.pagopa.pu.organization.model.PdndClient;
import it.gov.pagopa.pu.organization.service.pdnd.PdndClientEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PdndClientMapper {
  private final PdndClientEncryptionService encryptionService;

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

  public PdndClientDTO toDTO(PdndClient pdndClient) {
    if (pdndClient == null) {
      return null;
    }

    PdndClientDTO pdndClientDTO = new PdndClientDTO();
    pdndClientDTO.setClientId(pdndClient.getClientId());
    pdndClientDTO.setOrganizationId(pdndClient.getOrganizationId());
    pdndClientDTO.setSubUnitCode(pdndClient.getSubUnitCode());
    pdndClientDTO.setClientName(pdndClient.getClientName());
    pdndClientDTO.setKid(pdndClient.getKid());
    pdndClientDTO.setPrivateKey(encryptionService.decryptKey(pdndClient.getPrivateKeyCipher()));
    pdndClientDTO.setPublicKey(pdndClient.getPublicKey());
    return pdndClientDTO;
  }

  public PdndClientNoSecretDTO mapToPdndClientNoSecretDTO(PdndClient pdndClient) {
    if (pdndClient == null) {
      return null;
    }

    return PdndClientNoSecretDTO.builder()
      .clientId(pdndClient.getClientId())
      .organizationId(pdndClient.getOrganizationId())
      .subUnitCode(pdndClient.getSubUnitCode())
      .clientName(pdndClient.getClientName())
      .kid(pdndClient.getKid())
      .publicKey(pdndClient.getPublicKey())
      .build();
  }
}
