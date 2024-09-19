package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;

@Data
public class AccessLogEntryDto {

  @Schema(
      description = "The ID of the access log entry",
      example = "1"
  )
  private String id;

  @Schema(
      description = "The point in time when the action was done",
      example = "2022-04-28T10:11:12.00Z"
  )
  private Instant date;

  @Schema(
      description = "Information about the identity of the requestor"
  )
  private RequestorIdentityDto requestorIdentity;

  @Schema(
      description = "The Id of the twin that got accessed",
      example = "fieldIdOfBergkoppel"
  )
  private String twinId;

  @Schema(
      description = "The name of the twin that got accessed",
      example = "Bergkoppel"
  )
  @Nullable
  private String twinName;

  @Schema(
      description = "The accessed twin resource",
      example = "GEOMETRIES"
  )
  @Nullable
  private TwinResourceDto twinResource;

  @Schema(
      description = "The twin resource path of the accessed twin resource",
      example = "/GEOMETRIES/arable-data"
  )
  @Nullable
  private String twinResourcePath;

  @Schema(
      description = "The action that was performed",
      example = "CREATE"
  )
  private ActionDto action;
}
