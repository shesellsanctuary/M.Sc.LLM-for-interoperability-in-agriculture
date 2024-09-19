package de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.business.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.geojson.FeatureCollection;

import java.time.Instant;
import java.util.Set;

@ToString
@EqualsAndHashCode
@Getter
public class SoilMeasurementWorkRecordAtConnector {
  private final Instant observedAt;
  private final Set<String> measuredProperties;
  private final Integer distanceBetweenSamples;
  private final FeatureCollection data;

  public SoilMeasurementWorkRecordAtConnector(final Instant observedAt, final Set<String> measuredProperties, final Integer distanceBetweenSamples, final FeatureCollection data) {
    this.observedAt = observedAt;
    this.measuredProperties = measuredProperties;
    this.distanceBetweenSamples = distanceBetweenSamples;
    this.data = data;
  }
}
