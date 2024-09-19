package de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.business.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.business.model.SoilMeasurementWorkOrderAtConnector;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.access.business.model.SoilMeasurementWorkRecordAtConnector;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredential;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredentialAccessType;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredentialProcessDetails;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.service.ConnectorCredentialsService;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.Field;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilMeasurementWorkOrder;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilMeasurementWorkRecord;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.TypedResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.service.FieldService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.geojson.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConnectorDataAccessService {
  private static final Logger logger = LoggerFactory.getLogger(ConnectorDataAccessService.class);

  private final ConnectorCredentialsService connectorCredentialsService;
  private final FieldService fieldService;
  private final ObjectMapper objectMapper;

  public ConnectorDataAccessService(final ConnectorCredentialsService connectorCredentialsService, final FieldService fieldService, final ObjectMapper objectMapper) {
    this.connectorCredentialsService = connectorCredentialsService;
    this.fieldService = fieldService;
    this.objectMapper = objectMapper;
  }


  public Optional<SoilMeasurementWorkOrderAtConnector> retrieveSoilMeasurementWorkOrder(final String processId, final String apiKey) throws ForbiddenException {
    final Optional<ConnectorCredential> connectorCredentialOptional = this.connectorCredentialsService.checkRetrieveAndInvalidateCredential(processId, apiKey);
    if (connectorCredentialOptional.isEmpty()) {
      throw new ForbiddenException("Invalid credential");
    } else {
      final ConnectorCredential connectorCredential = connectorCredentialOptional.get();
      final ConnectorCredentialProcessDetails processDetails = connectorCredential.getProcessDetails();
      if (processDetails.getAccessType() != ConnectorCredentialAccessType.READ) {
        throw new ForbiddenException("Invalid accessType");
      }
      if (StringUtils.isBlank(processDetails.getTwinResourceId())) {
        throw new ForbiddenException("Missing twinResourceId in process");
      }
      final Optional<SoilMeasurementWorkOrder> soilMeasurementWorkOrderOptional = this.fieldService.getSoilMeasurementWorkOrderWithoutAuthentication(processDetails.getTwinId(), processDetails.getTwinResourceId());
      if (soilMeasurementWorkOrderOptional.isEmpty()) {
        return Optional.empty();
      }
      final SoilMeasurementWorkOrder soilMeasurementWorkOrder = soilMeasurementWorkOrderOptional.get();
      final Optional<Field> fieldOptional = this.fieldService.getFieldWithoutAuthentication(processDetails.getTwinId());
      if (fieldOptional.isEmpty()) {
        return Optional.empty();
      }
      final String fieldName = fieldOptional.get().getName();
      final Optional<Feature> fieldBoundariesOptional = this.fieldService.getArableAreaWithoutAuthentication(processDetails.getTwinId());
      final SoilMeasurementWorkOrderAtConnector soilMeasurementWorkOrderAtConnector = new SoilMeasurementWorkOrderAtConnector(processDetails.getTwinId(), fieldName, soilMeasurementWorkOrder.getPropertiesToMeasure(), soilMeasurementWorkOrder.getDistanceBetweenSamples(), fieldBoundariesOptional.orElse(null));
      logger.info("Provided data for process {}", processId);
      return Optional.of(soilMeasurementWorkOrderAtConnector);
    }
  }

  public void acceptSoilMeasurementWorkRecord(final String processId, final String apiKey, final SoilMeasurementWorkRecordAtConnector workRecord) throws ForbiddenException {
    final Optional<ConnectorCredential> connectorCredentialOptional = this.connectorCredentialsService.checkRetrieveAndInvalidateCredential(processId, apiKey);
    if (connectorCredentialOptional.isEmpty()) {
      throw new ForbiddenException("Invalid credential");
    } else {
      final ConnectorCredential connectorCredential = connectorCredentialOptional.get();
      final ConnectorCredentialProcessDetails processDetails = connectorCredential.getProcessDetails();
      if (processDetails.getAccessType() != ConnectorCredentialAccessType.CREATE) {
        throw new ForbiddenException("Invalid accessType");
      }
      logger.info("Accepted data for process {}", processId);
      final SoilMeasurementWorkRecord soilMeasurementWorkRecord = new SoilMeasurementWorkRecord();
      soilMeasurementWorkRecord.setObservedAt(workRecord.getObservedAt());
      soilMeasurementWorkRecord.setMeasuredProperties(workRecord.getMeasuredProperties());
      soilMeasurementWorkRecord.setDistanceBetweenSamples(workRecord.getDistanceBetweenSamples());
      try {
        final SoilMeasurementWorkRecord createdSoilMeasurementWorkRecord = this.fieldService.createSoilMeasurementWorkRecordWithoutAuthentication(processDetails.getTwinId(), soilMeasurementWorkRecord);
        this.fieldService.setSoilMeasurementWorkRecordFileWithoutAuthentication(processDetails.getTwinId(), createdSoilMeasurementWorkRecord.getId(), TypedResource.of(
            new ByteArrayResource(objectMapper.writeValueAsBytes(workRecord.getData())), "application/geo+json"
        ));
      } catch (DoesNotExistException | JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
