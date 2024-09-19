package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotBlank;

@Data
public class DataFileDto {
  // TODO agree on the contents
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String id;
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Instant createdAt;
  private String createdBy;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String filename;
  @NotBlank
  private String description;
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String fileMediaType;
  private List<String> tags;
  private DataFileMetadataDto metadata;

}
