package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
  String path;
  String mimeType;
}
