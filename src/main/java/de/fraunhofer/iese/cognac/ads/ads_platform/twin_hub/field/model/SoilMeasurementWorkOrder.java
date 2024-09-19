package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SoilMeasurementWorkOrder implements IdHolder, FileHolder {

  private String id;

  private Instant createdAt;

  @NotNull
  @NotEmpty
  private Set<@NotBlank String> propertiesToMeasure;

  @NotNull
  private Integer distanceBetweenSamples;

  @NotBlank
  private String assignee;

  @Nullable
  private File file;

}
