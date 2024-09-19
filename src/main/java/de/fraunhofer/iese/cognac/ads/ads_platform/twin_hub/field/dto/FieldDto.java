package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FieldDto {
  // TODO agree on the contents
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String id;

  private String name;


  private String farmId;
}
