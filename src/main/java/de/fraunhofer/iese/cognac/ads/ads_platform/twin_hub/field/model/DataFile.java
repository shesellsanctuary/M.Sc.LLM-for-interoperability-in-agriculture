package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotBlank;

@Data
public class DataFile implements IdHolder, FileHolder {
  private String id;

  private Instant createdAt;

  @Nullable
  private String createdBy;

  @NotBlank
  private String description;

  @Nullable
  private DataFileMetadata metadata;

  @Nullable
  private List<String> tags;

  @Nullable
  private File file;

}
