package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class DataFileMetadataDto {
  // TODO agree on the contents
  @Nullable
  private String usagePolicy;
}
