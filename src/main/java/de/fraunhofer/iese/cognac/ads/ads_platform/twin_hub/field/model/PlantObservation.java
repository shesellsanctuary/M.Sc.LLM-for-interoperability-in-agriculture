package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PlantObservation implements IdHolder, FileHolder {

  private String id;

  private Instant createdAt;

  @NotNull
  private Instant observedAt;

  @NotBlank
  private String cropSeason;

  @NotBlank
  private String description;

  @NotNull
  private PlantType plantType;

  @Nullable
  private File file;

}
