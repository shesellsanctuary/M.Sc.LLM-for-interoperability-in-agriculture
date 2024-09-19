package de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.business.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.geojson.Feature;
import org.springframework.lang.Nullable;

import java.util.Set;

@ToString
@EqualsAndHashCode
@Getter
public class SoilMeasurementWorkOrderAtConnector {
  private final String fieldId;
  private final String fieldName;
  private final Set<String> propertiesToMeasure;
  private final Integer distanceBetweenSamples;
  @Nullable
  private final Feature fieldBoundaries;

  public SoilMeasurementWorkOrderAtConnector(final String fieldId, final String fieldName, final Set<String> propertiesToMeasure, final Integer distanceBetweenSamples, @Nullable final Feature fieldBoundaries) {
    this.fieldId = fieldId;
    this.fieldName = fieldName;
    this.propertiesToMeasure = propertiesToMeasure;
    this.distanceBetweenSamples = distanceBetweenSamples;
    this.fieldBoundaries = fieldBoundaries;
  }
}
