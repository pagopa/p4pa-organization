package it.gov.pagopa.pu.organization.exception.transcoder.handler;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationErrorDTO;
import it.gov.pagopa.pu.organization.dto.generated.ErrorFieldDTO;
import it.gov.pagopa.pu.organization.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.pu.organization.exception.transcoder.ExceptionMessageTranscoder;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

public class MissingServletRequestParameterExceptionMessageTranscoder implements ExceptionMessageTranscoder<MissingServletRequestParameterException> {

  @Override
  public ExceptionMessageTranscoded transcode(MissingServletRequestParameterException missingServletRequestParameterException) {
    return new ExceptionMessageTranscoded(
      OrganizationErrorDTO.CategoryEnum.ORGANIZATION_BAD_REQUEST.getValue(),
      missingServletRequestParameterException.getMessage(),
      List.of(new ErrorFieldDTO(missingServletRequestParameterException.getParameterName(), "NotNull", missingServletRequestParameterException.getMessage())));
  }
}
