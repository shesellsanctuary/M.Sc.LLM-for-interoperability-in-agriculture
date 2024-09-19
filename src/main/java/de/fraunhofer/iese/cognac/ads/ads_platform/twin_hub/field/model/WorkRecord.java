package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;


import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WorkRecord {

  private String id;

  private Instant createdAt;

  @NotNull
  private Instant startTime;

  @NotNull
  private Instant endTime;

  @NotBlank
  private String cropSeason;

  @Nullable
  private String createdBy;

  @NotBlank
  private String description;

  @NotNull
  private OperationType operationType;

  @Nullable
  private Object details;

}
