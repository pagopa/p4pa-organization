package it.gov.pagopa.pu.organization.config.rest;

import it.gov.pagopa.pu.organization.dto.generated.ErrorFieldDTO;

import java.util.List;

public record PuErrorDTO(
  String category,
  String code,
  String message,
  List<ErrorFieldDTO> fields
) {
}
