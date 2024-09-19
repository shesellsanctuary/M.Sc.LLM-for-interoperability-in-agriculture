package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class DataFileMetadata {
  @Nullable
  private String usagePolicy;
}
