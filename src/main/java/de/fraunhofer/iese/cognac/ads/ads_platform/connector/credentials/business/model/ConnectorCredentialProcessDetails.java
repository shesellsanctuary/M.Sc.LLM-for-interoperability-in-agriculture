package de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.persistence.entity.ConnectorCredentialProcessDetailsEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@ToString
@EqualsAndHashCode
@Getter
public class ConnectorCredentialProcessDetails {
  private final String twinId;
  private final ConnectorCredentialAccessType accessType;
  @Nullable
  private final String twinResourceId;

  public ConnectorCredentialProcessDetails(ConnectorCredentialProcessDetailsEntity entity) {
    this.twinId = entity.getTwinId();
    this.accessType = entity.getAccessType();
    this.twinResourceId = entity.getTwinResourceId();
  }

  public ConnectorCredentialProcessDetails(String twinId, ConnectorCredentialAccessType accessType, @Nullable String twinResourceId) {
    this.twinId = twinId;
    this.accessType = accessType;
    this.twinResourceId = twinResourceId;
  }

  public ConnectorCredentialProcessDetailsEntity toEntity() {
    return new ConnectorCredentialProcessDetailsEntity(twinId, accessType, twinResourceId);
  }
}

