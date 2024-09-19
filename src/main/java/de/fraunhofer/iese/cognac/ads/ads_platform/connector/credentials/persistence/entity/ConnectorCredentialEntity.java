package de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("connector-credentials")
public class ConnectorCredentialEntity {
  @Id
  private String id;

  private String ownerId;

  private String processId;

  @ToString.Exclude
  private String apiKey;

  private Boolean singleUse;

  private ConnectorCredentialProcessDetailsEntity processDetails;
}
