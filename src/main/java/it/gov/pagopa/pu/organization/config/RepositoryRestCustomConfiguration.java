package it.gov.pagopa.pu.organization.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class RepositoryRestCustomConfiguration {

  private EntityManager entityManager;
  public RepositoryRestCustomConfiguration(EntityManager entityManager){
    this.entityManager = entityManager;
  }
  @Bean
  public RepositoryRestConfigurer repositoryRestConfigurer() {
    return RepositoryRestConfigurer.withConfig( config -> {
      Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
      config.exposeIdsFor(entities.stream()
        .map(EntityType::getJavaType)
        .toArray(Class[]::new));
    } );
  }

}
