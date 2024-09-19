package de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.api.dto;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredential;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorCredentialDto {
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String id;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String ownerId;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String processId;

  @ToString.Exclude
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String apiKey;

  @NotNull
  private Boolean singleUse;

  @NotNull
  @Valid
  private ConnectorCredentialProcessDetailsDto processDetails;

  public ConnectorCredentialDto(ConnectorCredential connectorCredential) {
    this.id = connectorCredential.getId();
    this.ownerId = connectorCredential.getOwnerId();
    this.processId = connectorCredential.getProcessId();
    this.apiKey = connectorCredential.getApiKey();
    this.singleUse = connectorCredential.getSingleUse();
    this.processDetails = new ConnectorCredentialProcessDetailsDto(connectorCredential.getProcessDetails());
  }
}
