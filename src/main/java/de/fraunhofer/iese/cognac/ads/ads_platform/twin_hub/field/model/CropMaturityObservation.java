package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.Data;

import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CropMaturityObservation implements IdHolder {
  private String id;
  private Instant createdAt;

  @NotNull
  private Instant observedAt;

  @NotBlank
  private String cropSeason;

  private String crop;

  private Object dryMatter;

}
