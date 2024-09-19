package de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.api.controller;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.api.dto.SoilMeasurementWorkOrderAtConnectorDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.api.dto.SoilMeasurementWorkRecordAtConnectorDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.business.model.SoilMeasurementWorkRecordAtConnector;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.business.service.ConnectorDataAccessService;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/v2/connector/access")
public class ConnectorDataAccessController {
  private static final Logger logger = LoggerFactory.getLogger(ConnectorDataAccessController.class);
  private final ConnectorDataAccessService connectorDataAccessService;
  private final ObjectMapper objectMapper;

  public ConnectorDataAccessController(final ConnectorDataAccessService connectorDataAccessService, final ObjectMapper objectMapper) {
    this.connectorDataAccessService = connectorDataAccessService;
    this.objectMapper = objectMapper;
  }


  @GetMapping("data-consumption/soil-measurement-work-order/{processId}")
  public ResponseEntity<SoilMeasurementWorkOrderAtConnectorDto> retrieveSoilMeasurementWorkOrder(@PathVariable final String processId, @RequestHeader(value = "x-api-key", required = false) final Optional<String> apiKey) {
    if (apiKey.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    try {
      return ResponseEntity.of(this.connectorDataAccessService.retrieveSoilMeasurementWorkOrder(processId, apiKey.get()).map(SoilMeasurementWorkOrderAtConnectorDto::new));
    } catch (ForbiddenException e) {
      logger.info(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }


  @PostMapping(value = "data-provision/soil-measurement-work-record/{processId}", consumes = "application/json")
  public ResponseEntity<Void> acceptSoilMeasurementWorkRecord(@PathVariable final String processId,
      @RequestHeader(value = "x-api-key", required = false) final Optional<String> apiKey,
      @RequestBody @Valid final SoilMeasurementWorkRecordAtConnectorDto payload) {
    if (apiKey.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    try {
      final SoilMeasurementWorkRecordAtConnector soilMeasurementWorkRecordAtConnector = new SoilMeasurementWorkRecordAtConnector(payload.getObservedAt(), payload.getMeasuredProperties(), payload.getDistanceBetweenSamples(), payload.getData());
      this.connectorDataAccessService.acceptSoilMeasurementWorkRecord(processId, apiKey.get(), soilMeasurementWorkRecordAtConnector);
      return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    } catch (ForbiddenException e) {
      logger.info(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PostMapping(value = "data-provision/soil-measurement-work-record/{processId}", consumes = "application/octet-stream")
  public ResponseEntity<Void> acceptSoilMeasurementWorkRecordOctetStream(
      @PathVariable final String processId,
      @RequestHeader(value = "x-api-key", required = false) final Optional<String> apiKey,
      @RequestBody final byte[] payload
  ) throws IOException {
    if (apiKey.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    try {
      final SoilMeasurementWorkRecordAtConnectorDto dto = objectMapper.readValue(payload, SoilMeasurementWorkRecordAtConnectorDto.class);
      final SoilMeasurementWorkRecordAtConnector soilMeasurementWorkRecordAtConnector = new SoilMeasurementWorkRecordAtConnector(dto.getObservedAt(), dto.getMeasuredProperties(), dto.getDistanceBetweenSamples(), dto.getData());
      this.connectorDataAccessService.acceptSoilMeasurementWorkRecord(processId, apiKey.get(), soilMeasurementWorkRecordAtConnector);
      return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    } catch (ForbiddenException e) {
      logger.info(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }


}
