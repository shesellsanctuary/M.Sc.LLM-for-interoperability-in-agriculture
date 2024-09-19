package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SoilNutrientsObservationDto {
  // TODO agree on the contents
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String id;
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Instant createdAt;

  @NotNull
  private Instant observedAt;

  private String description;
  private String cropSeason;

  @Nullable
  private String unit;
  @NotEmpty
  private List<String> nutrients;
}
