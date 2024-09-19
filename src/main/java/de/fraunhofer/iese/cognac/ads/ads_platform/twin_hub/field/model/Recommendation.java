package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.OperationType;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;

@Data
public class Recommendation implements IdHolder, FileHolder {

  private String id;

  private Instant createdAt;

  private String headline;

  private String body;

  @Nullable
  private OperationType operationType;

  @Nullable
  private Object details;

  @Nullable
  private File file;

}
