package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;

@Data
public class RecommendationDto {
  // TODO agree on the contents
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String id;
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Instant createdAt;

  private String headline;
  private String body;

  @Nullable
  private OperationType operationType;

  @Nullable
  private Object details;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Boolean fileAvailable;
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String fileMediaType;

}
