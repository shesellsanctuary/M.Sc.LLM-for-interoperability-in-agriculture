package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SoilMeasurementWorkOrderDto {
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String id;
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Instant createdAt;
  @NotNull
  @NotEmpty
  private Set<@NotBlank String> propertiesToMeasure;
  @NotNull
  private Integer distanceBetweenSamples;

  @NotBlank
  private String assignee;
}
