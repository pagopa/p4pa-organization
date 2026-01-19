package it.gov.pagopa.pu.organization.exception;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationErrorDTO;
import it.gov.pagopa.pu.organization.exception.custom.InvalidValueException;
import it.gov.pagopa.pu.organization.exception.custom.OrgSilServiceNotFoundException;
import it.gov.pagopa.pu.organization.exception.custom.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.util.Utilities;
import jakarta.persistence.RollbackException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.slf4j.event.Level;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;

import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OrganizationExceptionHandler {
  private static final String ERROR_MESSAGE_FORMAT = "[%s] %s";

  @ExceptionHandler({InvalidValueException.class})
  public ResponseEntity<OrganizationErrorDTO> handleInvalidValueException(RuntimeException ex, HttpServletRequest request){
    return handleException(ex, request, HttpStatus.BAD_REQUEST, OrganizationErrorDTO.CodeEnum.ORGANIZATION_BAD_REQUEST);
  }

  @ExceptionHandler({ResourceNotFoundException.class, OrganizationNotFoundException.class, OrgSilServiceNotFoundException.class})
  public ResponseEntity<OrganizationErrorDTO> handleResourceNotFoundException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.NOT_FOUND, OrganizationErrorDTO.CodeEnum.ORGANIZATION_NOT_FOUND);
  }

  @ExceptionHandler({DataIntegrityViolationException.class})
  public ResponseEntity<OrganizationErrorDTO> handleDataIntegrityViolationException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.CONFLICT, OrganizationErrorDTO.CodeEnum.ORGANIZATION_CONFLICT);
  }

  @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
  public ResponseEntity<OrganizationErrorDTO> handleViolationException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.BAD_REQUEST, OrganizationErrorDTO.CodeEnum.ORGANIZATION_BAD_REQUEST);
  }

  @ExceptionHandler({ServletException.class, ErrorResponseException.class})
  public ResponseEntity<OrganizationErrorDTO> handleServletException(Exception ex, HttpServletRequest request) {
    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    OrganizationErrorDTO.CodeEnum errorCode = OrganizationErrorDTO.CodeEnum.ORGANIZATION_GENERIC_ERROR;
    if (ex instanceof ErrorResponse errorResponse) {
      httpStatus = HttpStatus.valueOf((errorResponse.getStatusCode().value()));
      if (httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND)) {
        errorCode = OrganizationErrorDTO.CodeEnum.ORGANIZATION_NOT_FOUND;
      } else if (httpStatus.is4xxClientError()) {
        errorCode = OrganizationErrorDTO.CodeEnum.ORGANIZATION_BAD_REQUEST;
      }
    }
    return handleException(ex, request, httpStatus, errorCode);
  }

  @ExceptionHandler({TransactionException.class})
  public ResponseEntity<OrganizationErrorDTO> handleTransactionException(TransactionException ex, HttpServletRequest request) {
    if (ex.getCause() instanceof RollbackException rollbackException && rollbackException.getCause() instanceof ValidationException validationException) {
      return handleViolationException(validationException, request);
    } else {
      return handleRuntimeException(ex, request);
    }
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<OrganizationErrorDTO> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, OrganizationErrorDTO.CodeEnum.ORGANIZATION_GENERIC_ERROR);
  }

  static ResponseEntity<OrganizationErrorDTO> handleException(Exception ex, HttpServletRequest request, HttpStatus httpStatus, OrganizationErrorDTO.CodeEnum errorEnum) {
    logException(ex, request, httpStatus);

    String message = Optional.of(request.getRequestURI())
      .filter(path -> path.contains("/crud/"))
      .map(path -> buildCrudErrorMessage(path, httpStatus, ex))
      .orElseGet(() -> buildReturnedMessage(ex));

    return ResponseEntity
      .status(httpStatus)
      .contentType(MediaType.APPLICATION_JSON)
      .body(new OrganizationErrorDTO(errorEnum, message, Utilities.getTraceId()));
  }

  private static void logException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus) {
    boolean printStackTrace = httpStatus.is5xxServerError();
    Level logLevel = printStackTrace ? Level.ERROR : Level.INFO;
    log.makeLoggingEventBuilder(logLevel)
      .log("A {} occurred handling request {}: HttpStatus {} - {}",
        ex.getClass(),
        getRequestDetails(request),
        httpStatus.value(),
        ex.getMessage(),
        printStackTrace ? ex : null
      );
    if (!printStackTrace && log.isDebugEnabled() && ex.getCause() != null) {
      log.debug("CausedBy: ", ex.getCause());
    }
  }

  private static String buildReturnedMessage(Exception ex) {
    switch (ex) {
      case HttpMessageNotReadableException httpMessageNotReadableException -> {
        if (httpMessageNotReadableException.getCause() instanceof DatabindException jsonMappingException) {
          return String.format(ERROR_MESSAGE_FORMAT, OrganizationErrorDTO.CodeEnum.ORGANIZATION_BAD_REQUEST.name(),
            "Cannot parse body. " +
            jsonMappingException.getPath().stream()
              .map(JacksonException.Reference::getPropertyName)
              .collect(Collectors.joining(".")) +
            ": " + jsonMappingException.getOriginalMessage());
        }
        return String.format(ERROR_MESSAGE_FORMAT, OrganizationErrorDTO.CodeEnum.ORGANIZATION_BAD_REQUEST.name(), "Required request body is missing");
      }
      case MethodArgumentNotValidException methodArgumentNotValidException -> {
        return String.format(ERROR_MESSAGE_FORMAT, OrganizationErrorDTO.CodeEnum.ORGANIZATION_BAD_REQUEST.name(),
          "Invalid request content." +
          methodArgumentNotValidException.getBindingResult()
            .getAllErrors().stream()
            .map(e -> " " +
              (e instanceof FieldError fieldError ? fieldError.getField() : e.getObjectName()) +
              ": " + e.getDefaultMessage())
            .sorted()
            .collect(Collectors.joining(";")));
      }
      case ConstraintViolationException constraintViolationException -> {
        return String.format(ERROR_MESSAGE_FORMAT, OrganizationErrorDTO.CodeEnum.ORGANIZATION_BAD_REQUEST.name(),
          "Invalid request content." +
          constraintViolationException.getConstraintViolations()
            .stream()
            .map(e -> " " + e.getPropertyPath() + ": " + e.getMessage())
            .sorted()
            .collect(Collectors.joining(";")));
      }
      default -> {
        if (ex.getCause() instanceof HttpHostConnectException) {
          return String.format(ERROR_MESSAGE_FORMAT, "ORGANIZATION_CONNECTION_ERROR",
            ex.getMessage());
        }
        return ex.getMessage();
      }
    }
  }

  private static String buildCrudErrorMessage(String requestPath, HttpStatus httpStatus, Exception ex) {
    String entity = requestPath.split("/crud/")[1].split("/")[0].replaceAll("s$", "");
    String entityCode = entity.replace("-", "_").toUpperCase();
    return String.format(ERROR_MESSAGE_FORMAT, entityCode + "_" + httpStatus.name(), ex.getMessage());
  }

  static String getRequestDetails(HttpServletRequest request) {
    return "%s %s".formatted(request.getMethod(), request.getRequestURI());
  }

}
