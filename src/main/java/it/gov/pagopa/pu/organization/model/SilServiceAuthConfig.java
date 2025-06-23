package it.gov.pagopa.pu.organization.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
  property = "authConfig")
@JsonSubTypes({
  @JsonSubTypes.Type(name = "legacyJwt", value = SilServiceLegacyJwtAuthConfig.class),
  @JsonSubTypes.Type(name = "legacyBasic", value = SilServiceLegacyBasicAuthConfig.class),
})
@Schema(
        discriminatorProperty = "authConfig",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "legacyJwt", schema = SilServiceLegacyJwtAuthConfig.class),
                @DiscriminatorMapping(value = "legacyBasic", schema = SilServiceLegacyBasicAuthConfig.class)
        },
        oneOf = {SilServiceLegacyJwtAuthConfig.class, SilServiceLegacyBasicAuthConfig.class}
)
public interface SilServiceAuthConfig extends Serializable {
}
