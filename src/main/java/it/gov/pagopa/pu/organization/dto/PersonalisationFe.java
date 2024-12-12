package it.gov.pagopa.pu.organization.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class PersonalisationFe implements Serializable {

  private String headerAssistanceUrl;
  private String logoFooterImg;
  private String footerDescText;
  private String footerPrivacyInfoUrl;
  private String footerGDPRUrl;
  private String footerTermsCondUrl;
  private String footerAccessibilityUrl;

}
