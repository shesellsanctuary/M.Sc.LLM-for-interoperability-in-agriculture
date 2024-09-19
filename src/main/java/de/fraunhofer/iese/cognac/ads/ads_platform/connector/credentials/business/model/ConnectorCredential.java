package de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.persistence.entity.ConnectorCredentialEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@EqualsAndHashCode
@Getter
public class ConnectorCredential {
  private final String id;
  private final String ownerId;
  private final String processId;
  @ToString.Exclude
  private final String apiKey;
  private final Boolean singleUse;
  private final ConnectorCredentialProcessDetails processDetails;

  public ConnectorCredential(ConnectorCredentialEntity entity) {
    this.id = entity.getId();
    this.ownerId = entity.getOwnerId();
    this.processId = entity.getProcessId();
    this.apiKey = entity.getApiKey();
    this.singleUse = entity.getSingleUse();
    this.processDetails = new ConnectorCredentialProcessDetails(entity.getProcessDetails());
  }

  public ConnectorCredential(String ownerId, Boolean singleUse, ConnectorCredentialProcessDetails processDetails) {
    this.id = null;
    this.ownerId = ownerId;
    this.processId = UUID.randomUUID().toString();
    this.apiKey = UUID.randomUUID().toString();
    this.singleUse = singleUse;
    this.processDetails = processDetails;
  }

  public ConnectorCredentialEntity toEntity() {
    return new ConnectorCredentialEntity(id, ownerId, processId, apiKey, singleUse, processDetails.toEntity());
  }
}
