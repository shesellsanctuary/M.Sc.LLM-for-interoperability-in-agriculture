package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class TwinOwnershipStatement {
  @Id
  private String id;
  @Version
  private Long version;

  private String twinId;
  private String ownerId;
}
