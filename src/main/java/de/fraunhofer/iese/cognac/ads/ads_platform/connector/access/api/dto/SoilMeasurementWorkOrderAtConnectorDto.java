package de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.api.dto;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.business.model.SoilMeasurementWorkOrderAtConnector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.geojson.Feature;
import org.springframework.lang.Nullable;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoilMeasurementWorkOrderAtConnectorDto {
  private String fieldId;
  private String fieldName;
  private Set<String> propertiesToMeasure;
  private Integer distanceBetweenSamples;
  @Nullable
  private Feature fieldBoundaries;


  public SoilMeasurementWorkOrderAtConnectorDto(final SoilMeasurementWorkOrderAtConnector soilMeasurementWorkOrderAtConnector) {
    this.fieldId = soilMeasurementWorkOrderAtConnector.getFieldId();
    this.fieldName = soilMeasurementWorkOrderAtConnector.getFieldName();
    this.propertiesToMeasure = soilMeasurementWorkOrderAtConnector.getPropertiesToMeasure();
    this.distanceBetweenSamples = soilMeasurementWorkOrderAtConnector.getDistanceBetweenSamples();
    this.fieldBoundaries = soilMeasurementWorkOrderAtConnector.getFieldBoundaries();
  }
}
