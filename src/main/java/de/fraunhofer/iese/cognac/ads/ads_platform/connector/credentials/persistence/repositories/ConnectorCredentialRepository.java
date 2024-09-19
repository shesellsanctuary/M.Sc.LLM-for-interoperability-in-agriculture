package de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.persistence.repositories;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.persistence.entity.ConnectorCredentialEntity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectorCredentialRepository extends CrudRepository<ConnectorCredentialEntity, String> {
  Optional<ConnectorCredentialEntity> findByProcessIdAndApiKey(final String processId, final String apiKey);

  List<ConnectorCredentialEntity> findByOwnerId(final String ownerId);

}
