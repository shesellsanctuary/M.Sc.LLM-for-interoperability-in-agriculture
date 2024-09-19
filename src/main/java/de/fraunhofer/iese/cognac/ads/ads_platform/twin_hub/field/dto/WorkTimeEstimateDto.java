package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto;

import lombok.Value;

import java.time.Duration;

@Value(staticConstructor = "of")
public class WorkTimeEstimateDto {
  // TODO agree on the contents
  OperationType operationType;
  Duration duration;
}
