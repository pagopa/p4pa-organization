package it.gov.pagopa.pu.organization.dto.orgsilservice;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
  property = "authConfig")
@JsonSubTypes({
  @JsonSubTypes.Type(name = "legacyJwt", value = SilServiceLegacyJwtAuthConfigDTO.class),
  @JsonSubTypes.Type(name = "legacyBasic", value = SilServiceLegacyBasicAuthConfigDTO.class),
})
@Schema(
    discriminatorProperty = "authConfig",
    discriminatorMapping = {
            @DiscriminatorMapping(value = "legacyJwt", schema = SilServiceLegacyJwtAuthConfigDTO.class),
            @DiscriminatorMapping(value = "legacyBasic", schema = SilServiceLegacyBasicAuthConfigDTO.class)
    }
)
public interface SilServiceAuthConfigDTO extends Serializable {
}
