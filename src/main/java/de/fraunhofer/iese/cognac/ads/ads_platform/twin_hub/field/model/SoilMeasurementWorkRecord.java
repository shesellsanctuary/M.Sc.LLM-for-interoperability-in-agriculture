package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SoilMeasurementWorkRecord implements IdHolder, FileHolder {

  private String id;

  private Instant createdAt;

  @NotNull
  private Instant observedAt;

  @NotNull
  @NotEmpty
  private Set<@NotBlank String> measuredProperties;

  @NotNull
  private Integer distanceBetweenSamples;

  @Nullable
  private File file;

}
