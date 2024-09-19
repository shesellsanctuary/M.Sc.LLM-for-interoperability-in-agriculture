package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class Field {
  private String id;
  @NotBlank
  private String name;
  @NotBlank
  private String farmId;
}
