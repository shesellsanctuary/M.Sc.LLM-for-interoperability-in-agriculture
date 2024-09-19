package de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.api.dto;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredentialAccessType;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredentialProcessDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorCredentialProcessDetailsDto {
  @NotBlank
  private String twinId;
  @NotNull
  private ConnectorCredentialAccessType accessType;
  @Nullable
  private String twinResourceId;

  public ConnectorCredentialProcessDetailsDto(ConnectorCredentialProcessDetails connectorCredentialProcessDetails) {
    this.twinId = connectorCredentialProcessDetails.getTwinId();
    this.accessType = connectorCredentialProcessDetails.getAccessType();
    this.twinResourceId = connectorCredentialProcessDetails.getTwinResourceId();
  }
}
