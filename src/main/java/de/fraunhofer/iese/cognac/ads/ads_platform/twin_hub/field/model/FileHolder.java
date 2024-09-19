package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import org.springframework.lang.Nullable;

public interface FileHolder {
  @Nullable
  File getFile();

  void setFile(@Nullable File file);
}
