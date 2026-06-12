package it.gov.pagopa.pu.organization.repository;

import it.gov.pagopa.pu.organization.model.PdndClient;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "pdnd-client")
public interface PdndClientRepository extends JpaRepository<PdndClient, String> {

  @RestResource(exported = false)
  @Nonnull
  @Override
  <S extends PdndClient> S save(@Nonnull S entity);
}
