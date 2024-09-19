package de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredential;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredentialAccessType;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredentialProcessDetails;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.persistence.entity.ConnectorCredentialEntity;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.persistence.repositories.ConnectorCredentialRepository;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.InvalidInputException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.service.TwinOwnershipService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConnectorCredentialsService {
  private static final Logger logger = LoggerFactory.getLogger(ConnectorCredentialsService.class);

  private final ConnectorCredentialRepository connectorCredentialRepository;
  private final TwinOwnershipService twinOwnershipService;

  public ConnectorCredentialsService(final ConnectorCredentialRepository connectorCredentialRepository, final TwinOwnershipService twinOwnershipService) {
    this.connectorCredentialRepository = connectorCredentialRepository;
    this.twinOwnershipService = twinOwnershipService;
  }

  public ConnectorCredential createCredential(final Authentication authentication, final String twinId, final ConnectorCredentialAccessType accessType, @Nullable final String twinResourceId, final Boolean singleUse) throws ForbiddenException, InvalidInputException {
    final Boolean currentUserIsOwnerOfTwin = twinOwnershipService.getOwnerOfTwin(twinId).map(owner -> owner.equals(authentication.getSubject())).orElse(Boolean.FALSE);
    if (!currentUserIsOwnerOfTwin) {
      throw new ForbiddenException();
    }
    if (accessType == ConnectorCredentialAccessType.READ && StringUtils.isBlank(twinResourceId)) {
      throw new InvalidInputException();
    }
    final ConnectorCredentialEntity newEntity = new ConnectorCredential(authentication.getSubject(), singleUse, new ConnectorCredentialProcessDetails(twinId, accessType, twinResourceId)).toEntity();
    final ConnectorCredentialEntity persistedEntity = this.connectorCredentialRepository.save(newEntity);
    return new ConnectorCredential(persistedEntity);
  }

  public Optional<ConnectorCredential> checkRetrieveAndInvalidateCredential(final String processId, final String apiKey) {
    final Optional<ConnectorCredentialEntity> connectorCredentialEntityOptional = this.connectorCredentialRepository.findByProcessIdAndApiKey(processId, apiKey);
    if (connectorCredentialEntityOptional.isEmpty()) {
      logger.info("credential not found in database for process {}", processId);
      return Optional.empty();
    } else {
      logger.info("credential found for process {}", processId);
      final ConnectorCredentialEntity connectorCredentialEntity = connectorCredentialEntityOptional.get();
      final ConnectorCredential connectorCredential = new ConnectorCredential(connectorCredentialEntity);
      if (connectorCredential.getSingleUse()) {
        this.connectorCredentialRepository.deleteById(connectorCredentialEntity.getId());
      }
      return Optional.of(connectorCredential);
    }
  }

  public List<ConnectorCredential> getOwnConnectorCredentials(final Authentication authentication) {
    return this.connectorCredentialRepository.findByOwnerId(authentication.getSubject()).stream()
        .map(ConnectorCredential::new)
        .collect(Collectors.toList());
  }

  public void deleteCredential(final String id, final Authentication authentication) {
    final Optional<ConnectorCredentialEntity> connectorCredentialEntityOptional = this.connectorCredentialRepository.findById(id)
        .filter(connectorCredentialEntity -> connectorCredentialEntity.getOwnerId().equals(authentication.getSubject()));
    if (connectorCredentialEntityOptional.isPresent()) {
      final ConnectorCredentialEntity connectorCredentialEntity = connectorCredentialEntityOptional.get();
      this.connectorCredentialRepository.deleteById(connectorCredentialEntity.getId());
    }
  }
}
