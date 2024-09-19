package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
public class CropMaturityForecastDto {
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Instant createdAt;
  private String crop;
  private String cropSeason;
  private Object details;
}
