package de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.persistence.entity;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredentialAccessType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorCredentialProcessDetailsEntity {
  private String twinId;
  private ConnectorCredentialAccessType accessType;

  /**
   * in case of accessType==READ, twinResourceId is the id of the work-order
   */
  @Nullable
  private String twinResourceId;

}
