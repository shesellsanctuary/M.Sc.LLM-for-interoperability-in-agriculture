package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WorkRecordDto {
  // TODO agree on the contents
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String id;
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Instant createdAt;
  @NotNull
  private Instant startTime;

  @NotNull
  private Instant endTime;

  @NotBlank
  private String cropSeason;

  private String createdBy;

  @NotBlank
  private String description;

  @NotNull
  private OperationType operationType;

  @Nullable
  private Object details;

}
