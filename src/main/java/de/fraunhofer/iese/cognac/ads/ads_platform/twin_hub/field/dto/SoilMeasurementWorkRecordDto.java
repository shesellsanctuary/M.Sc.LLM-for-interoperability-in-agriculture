package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class SoilMeasurementWorkRecordDto {
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String id;
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Instant createdAt;
  private Instant observedAt;
  private Set<String> measuredProperties;
  private Integer distanceBetweenSamples;
}
