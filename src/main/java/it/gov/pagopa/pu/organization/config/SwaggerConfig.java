package it.gov.pagopa.pu.organization.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * The Class SwaggerConfig.
 */
@Configuration
@OpenAPIDefinition(
  info =@Info(
    title = "${spring.application.name}",
    version = "${spring.application.version}",
    description = "Api and Models"
  )
)
public class SwaggerConfig {
}
