package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SoilNutrientsObservation implements IdHolder, FileHolder {

  private String id;

  private Instant createdAt;

  @NotNull
  private Instant observedAt;

  @NotBlank
  private String description;

  @NotBlank
  private String cropSeason;

  @Nullable
  private String unit;

  @NotEmpty
  private List<String> nutrients;

  @Nullable
  private File file;

}
