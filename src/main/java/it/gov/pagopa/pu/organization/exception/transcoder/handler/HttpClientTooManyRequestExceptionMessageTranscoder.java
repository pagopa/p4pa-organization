package it.gov.pagopa.pu.organization.exception.transcoder.handler;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationErrorDTO;
import it.gov.pagopa.pu.organization.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.pu.organization.exception.transcoder.ExceptionMessageTranscoder;
import org.springframework.web.client.HttpClientErrorException;

public class HttpClientTooManyRequestExceptionMessageTranscoder implements ExceptionMessageTranscoder<HttpClientErrorException.TooManyRequests> {
  @Override
  public ExceptionMessageTranscoded transcode(HttpClientErrorException.TooManyRequests tooManyRequestsException) {
    return new ExceptionMessageTranscoded(
      OrganizationErrorDTO.CategoryEnum.ORGANIZATION_TOO_MANY_REQUESTS.getValue(),
      tooManyRequestsException.getMessage(),
      null);
  }
}
