package it.gov.pagopa.pu.organization.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationErrorDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class OrganizationExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<OrganizationErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.NOT_FOUND, OrganizationErrorDTO.CodeEnum.NOT_FOUND);
  }

  @ExceptionHandler({DataIntegrityViolationException.class})
  public ResponseEntity<OrganizationErrorDTO> handleDataIntegrityViolationException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.CONFLICT, OrganizationErrorDTO.CodeEnum.CONFLICT);
  }

  @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
  public ResponseEntity<OrganizationErrorDTO> handleViolationException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.BAD_REQUEST, OrganizationErrorDTO.CodeEnum.BAD_REQUEST);
  }

  @ExceptionHandler({ServletException.class, ErrorResponseException.class})
  public ResponseEntity<OrganizationErrorDTO> handleServletException(Exception ex, HttpServletRequest request) {
    HttpStatusCode httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    OrganizationErrorDTO.CodeEnum errorCode = OrganizationErrorDTO.CodeEnum.GENERIC_ERROR;
    if (ex instanceof ErrorResponse errorResponse) {
      httpStatus = errorResponse.getStatusCode();
      if(httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND)) {
        errorCode = OrganizationErrorDTO.CodeEnum.NOT_FOUND;
      } else if (httpStatus.is4xxClientError()) {
        errorCode = OrganizationErrorDTO.CodeEnum.BAD_REQUEST;
      }
    }
    return handleException(ex, request, httpStatus, errorCode);
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<OrganizationErrorDTO> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, OrganizationErrorDTO.CodeEnum.GENERIC_ERROR);
  }

  static ResponseEntity<OrganizationErrorDTO> handleException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus, OrganizationErrorDTO.CodeEnum errorEnum) {
    logException(ex, request, httpStatus);

    String message = buildReturnedMessage(ex);

    return ResponseEntity
      .status(httpStatus)
      .body(new OrganizationErrorDTO(errorEnum, message));
  }

  private static void logException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus) {
    log.info("A {} occurred handling request {}: HttpStatus {} - {}",
      ex.getClass(),
      getRequestDetails(request),
      httpStatus.value(),
      ex.getMessage());
    if(log.isDebugEnabled() && ex.getCause()!=null){
      log.debug("CausedBy: ", ex.getCause());
    }
  }

  private static String buildReturnedMessage(Exception ex) {
    if (ex instanceof HttpMessageNotReadableException) {
      if(ex.getCause() instanceof JsonMappingException jsonMappingException){
        return "Cannot parse body: " +
          jsonMappingException.getPath().stream()
            .map(JsonMappingException.Reference::getFieldName)
            .collect(Collectors.joining(".")) +
          ": " + jsonMappingException.getOriginalMessage();
      }
      return "Required request body is missing";
    } else if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
      return "Invalid request content:" +
        methodArgumentNotValidException.getBindingResult()
          .getAllErrors().stream()
          .map(e -> " " +
            (e instanceof FieldError fieldError? fieldError.getField(): e.getObjectName()) +
            ": " + e.getDefaultMessage())
          .sorted()
          .collect(Collectors.joining(";"));
    } else {
      return ex.getMessage();
    }
  }

  static String getRequestDetails(HttpServletRequest request) {
    return "%s %s".formatted(request.getMethod(), request.getRequestURI());
  }

}
