package it.gov.pagopa.pu.organization.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springdoc.api.ErrorMessage;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorMessage> resourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
    HttpStatus returnStatus = HttpStatus.NOT_FOUND;
    logException(ex, request, returnStatus, Level.INFO, false);
    return ResponseEntity.status(returnStatus)
      .body(new ErrorMessage("resource not found: %s".formatted(ex.getMessage())));
  }

  private void logException(Exception ex, HttpServletRequest request, HttpStatus httpStatus, Level level, boolean printStackTrace) {
    printStackTrace = printStackTrace | log.isTraceEnabled();
    log.atLevel(level)
      .setCause(printStackTrace ? ex : null)
      .log("A {} occurred handling request {} {} - HttpStatus {} - {}",
        ex.getClass().getSimpleName(),
        request.getMethod(),
        request.getRequestURI(),
        httpStatus.value(),
        ex.getMessage()
      );
  }

}
