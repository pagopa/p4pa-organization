package it.gov.pagopa.pu.organization.config;

import static org.mockito.Mockito.times;

import it.gov.pagopa.pu.organization.model.Broker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@ExtendWith(MockitoExtension.class)
class RepositoryRestCustomConfigurationTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private Metamodel metamodel;

  @Mock
  private EntityType<?> entityType;

  @InjectMocks
  private RepositoryRestCustomConfiguration config;

  @BeforeEach
  void setUp(){
    Mockito.when(entityManager.getMetamodel()).thenReturn(metamodel);
    Mockito.when(metamodel.getEntities()).thenReturn(Collections.singleton(entityType));
    Mockito.when(entityType.getJavaType()).thenReturn((Class) Broker.class);
  }

  @Test
  void givenRepositoryRestConfigurerThenOk() {
    RepositoryRestConfiguration repositoryRestConfiguration = Mockito.mock(RepositoryRestConfiguration.class);
    // Act
    RepositoryRestConfigurer configurer = config.repositoryRestConfigurer();
    configurer.configureRepositoryRestConfiguration(repositoryRestConfiguration,null);

    // Assert
    Assertions.assertNotNull(configurer);
    Mockito.verify(entityManager, times(1)).getMetamodel();
    Mockito.verify(metamodel, times(1)).getEntities();
    Mockito.verify(entityType, times(1)).getJavaType();
  }
}
