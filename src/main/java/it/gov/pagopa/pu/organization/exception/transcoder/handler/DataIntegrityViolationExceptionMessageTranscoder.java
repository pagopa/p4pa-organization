package it.gov.pagopa.pu.organization.exception.transcoder.handler;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationErrorDTO;
import it.gov.pagopa.pu.organization.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.pu.organization.exception.transcoder.ExceptionMessageTranscoder;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

public class DataIntegrityViolationExceptionMessageTranscoder implements ExceptionMessageTranscoder<DataIntegrityViolationException> {

  @Override
  public ExceptionMessageTranscoded transcode(DataIntegrityViolationException dataIntegrityViolationException) {
    String errorMsg = "Conflict.";
    if(dataIntegrityViolationException.getCause() instanceof ConstraintViolationException hibernateConstraintViolationException) {
      errorMsg += " " + hibernateConstraintViolationException.getSQLException().getMessage();
    }
    return new ExceptionMessageTranscoded(
      OrganizationErrorDTO.CategoryEnum.ORGANIZATION_CONFLICT.getValue(),
      errorMsg,
      null) ;
  }
}
