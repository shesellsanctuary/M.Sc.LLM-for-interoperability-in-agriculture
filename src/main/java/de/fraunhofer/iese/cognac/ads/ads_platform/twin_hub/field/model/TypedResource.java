package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.Value;
import org.springframework.core.io.Resource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value(staticConstructor = "of")
public class TypedResource {
  @NotNull
  Resource resource;
  @NotBlank
  String mediaType;
}
