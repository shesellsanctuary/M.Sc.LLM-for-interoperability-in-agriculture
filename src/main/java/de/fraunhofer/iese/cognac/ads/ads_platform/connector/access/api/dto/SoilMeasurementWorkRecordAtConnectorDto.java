package de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.geojson.FeatureCollection;

import java.time.Instant;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoilMeasurementWorkRecordAtConnectorDto {
  @NotNull
  private Instant observedAt;
  @NotNull
  @NotEmpty
  private Set<@NotBlank String> measuredProperties;
  @NotNull
  private Integer distanceBetweenSamples;
  @NotNull
  private FeatureCollection data;
}
