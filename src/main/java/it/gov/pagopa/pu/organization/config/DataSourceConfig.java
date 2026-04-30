package it.gov.pagopa.pu.organization.config;

import it.gov.pagopa.pu.organization.util.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class DataSourceConfig {

  @Bean
  public AuditorAware<String> auditorProvider() {
    return () -> Optional.ofNullable(SecurityUtils.getCurrentUserExternalId());
  }

}
