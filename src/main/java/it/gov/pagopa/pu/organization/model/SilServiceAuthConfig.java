package it.gov.pagopa.pu.organization.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
  property = "authConfig")
@JsonSubTypes({
  @JsonSubTypes.Type(name = "legacyJwt", value = SilServiceLegacyJwtAuthConfig.class),
})
public interface SilServiceAuthConfig extends Serializable {
}
