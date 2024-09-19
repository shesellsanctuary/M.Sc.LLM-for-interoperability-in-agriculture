package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

import javax.validation.constraints.NotNull;

@Data
public class NitrogenOutgassingObservationDto {
  // TODO agree on the contents
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String id;
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Instant createdAt;
  @NotNull
  private Instant observedAt;
  private String cropSeason;
  private String description;
}
