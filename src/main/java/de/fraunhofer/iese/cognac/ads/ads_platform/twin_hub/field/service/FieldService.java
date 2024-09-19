package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.InvalidInputException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.CropMaturityObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.DataFile;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.Field;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.NitrogenOutgassingObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.OperationType;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.PlantObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.Recommendation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilCompositionObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilMeasurementWorkOrder;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilMeasurementWorkRecord;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilNutrientsObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.TypedResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.VegetationIndexObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.WorkRecord;

import org.geojson.Feature;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

public interface FieldService {

  List<Field> getFields(Authentication authentication);

  Field createField(@Valid Field field, Authentication authentication) throws ForbiddenException;

  Optional<Field> getField(String id, Authentication authentication) throws ForbiddenException;

  Optional<Field> getFieldWithoutAuthentication(String id);

  void deleteField(String id, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setName(String id, String name, Authentication authentication) throws DoesNotExistException, InvalidInputException, ForbiddenException;

  Optional<Feature> getArableArea(String id, Authentication authentication) throws ForbiddenException;

  Optional<Feature> getArableAreaWithoutAuthentication(String id);

  void setArableArea(String id, Feature arableArea, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setArableArea(String id, String arableArea, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void deleteArableArea(String id, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<Feature> getTrafficableArea(String id, Authentication authentication) throws ForbiddenException;

  void setTrafficableArea(String id, Feature trafficableArea, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void deleteTrafficableArea(String id, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<Feature> getNonTrafficableArea(String id, Authentication authentication) throws ForbiddenException;

  void setNonTrafficableArea(String id, Feature nonTrafficableArea, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void deleteNonTrafficableArea(String id, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<Feature> getTracks(String id, Authentication authentication) throws ForbiddenException;

  void setTracks(String id, Feature tracks, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void deleteTracks(String id, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<WorkRecord>> getWorkRecords(String id, @Nullable OperationType operationType, @Nullable String cropSeason, Authentication authentication) throws ForbiddenException;

  WorkRecord createWorkRecord(String id, @Valid WorkRecord workRecord, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<WorkRecord> getWorkRecord(String id, String workRecordId, Authentication authentication) throws ForbiddenException;

  void deleteWorkRecord(String id, String workRecordId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setWorkRecordShapefile(String id, String workRecordId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<TypedResource> getWorkRecordShapefile(String id, String workRecordId, Authentication authentication) throws ForbiddenException;

  void deleteWorkRecordShapefile(String id, String workRecordId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<PlantObservation>> getPlantObservations(String id, Authentication authentication) throws ForbiddenException;

  PlantObservation createPlantObservation(String id, @Valid PlantObservation plantObservation, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<PlantObservation> getPlantObservation(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deletePlantObservation(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setPlantObservationFile(String id, String observationId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<TypedResource> getPlantObservationFile(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deletePlantObservationFile(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<NitrogenOutgassingObservation>> getNitrogenOutgassingObservations(String id, Authentication authentication) throws ForbiddenException;

  NitrogenOutgassingObservation createNitrogenOutgassingObservation(String id, @Valid NitrogenOutgassingObservation nitrogenOutgassingObservation, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<NitrogenOutgassingObservation> getNitrogenOutgassingObservation(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deleteNitrogenOutgassingObservation(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setNitrogenOutgassingObservationFile(String id, String observationId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<TypedResource> getNitrogenOutgassingObservationFile(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deleteNitrogenOutgassingObservationFile(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<Recommendation>> getRecommendations(String id, Authentication authentication) throws ForbiddenException;

  Recommendation createRecommendation(String id, @Valid Recommendation recommendation, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<Recommendation> getRecommendation(String id, String recommendationId, Authentication authentication) throws ForbiddenException;

  void deleteRecommendation(String id, String recommendationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setRecommendationFile(String id, String recommendationId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<TypedResource> getRecommendationFile(String id, String recommendationId, Authentication authentication) throws ForbiddenException;

  void deleteRecommendationFile(String id, String recommendationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<SoilCompositionObservation>> getSoilCompositionObservations(String id, Authentication authentication) throws ForbiddenException;

  SoilCompositionObservation createSoilCompositionObservation(String id, @Valid SoilCompositionObservation soilCompositionObservation, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<SoilCompositionObservation> getSoilCompositionObservation(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deleteSoilCompositionObservation(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setSoilCompositionObservationFile(String id, String observationId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<TypedResource> getSoilCompositionObservationFile(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deleteSoilCompositionObservationFile(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<SoilNutrientsObservation>> getSoilNutrientsObservations(String id, Authentication authentication) throws ForbiddenException;

  SoilNutrientsObservation createSoilNutrientsObservation(String id, @Valid SoilNutrientsObservation soilNutrientsObservation, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<SoilNutrientsObservation> getSoilNutrientsObservation(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deleteSoilNutrientsObservation(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setSoilNutrientsObservationFile(String id, String observationId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<TypedResource> getSoilNutrientsObservationFile(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deleteSoilNutrientsObservationFile(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<VegetationIndexObservation>> getVegetationIndexObservations(String id, Authentication authentication) throws ForbiddenException;

  VegetationIndexObservation createVegetationIndexObservation(String id, @Valid VegetationIndexObservation vegetationIndexObservation, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<VegetationIndexObservation> getVegetationIndexObservation(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deleteVegetationIndexObservation(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setVegetationIndexObservationFile(String id, String observationId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<TypedResource> getVegetationIndexObservationFile(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deleteVegetationIndexObservationFile(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<SoilMeasurementWorkOrder>> getSoilMeasurementWorkOrders(String id, Authentication authentication) throws ForbiddenException;

  SoilMeasurementWorkOrder createSoilMeasurementWorkOrder(String id, @Valid SoilMeasurementWorkOrder soilMeasurementWorkOrder, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<SoilMeasurementWorkOrder> getSoilMeasurementWorkOrder(String id, String workOrderId, Authentication authentication) throws ForbiddenException;

  Optional<SoilMeasurementWorkOrder> getSoilMeasurementWorkOrderWithoutAuthentication(String id, String workOrderId);

  void deleteSoilMeasurementWorkOrder(String id, String workOrderId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setSoilMeasurementWorkOrderFile(String id, String workOrderId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<TypedResource> getSoilMeasurementWorkOrderFile(String id, String workOrderId, Authentication authentication) throws ForbiddenException;

  void deleteSoilMeasurementWorkOrderFile(String id, String workOrderId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<SoilMeasurementWorkRecord>> getSoilMeasurementWorkRecords(String id, Authentication authentication) throws ForbiddenException;

  SoilMeasurementWorkRecord createSoilMeasurementWorkRecord(String id, @Valid SoilMeasurementWorkRecord soilMeasurementWorkRecord, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  SoilMeasurementWorkRecord createSoilMeasurementWorkRecordWithoutAuthentication(String id, @Valid SoilMeasurementWorkRecord soilMeasurementWorkRecord) throws DoesNotExistException;

  Optional<SoilMeasurementWorkRecord> getSoilMeasurementWorkRecord(String id, String workRecordId, Authentication authentication) throws ForbiddenException;

  void deleteSoilMeasurementWorkRecord(String id, String workRecordId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setSoilMeasurementWorkRecordFile(String id, String workRecordId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setSoilMeasurementWorkRecordFileWithoutAuthentication(String id, String workRecordId, @Valid TypedResource typedResource) throws DoesNotExistException;

  Optional<TypedResource> getSoilMeasurementWorkRecordFile(String id, String workRecordId, Authentication authentication) throws ForbiddenException;

  void deleteSoilMeasurementWorkRecordFile(String id, String workRecordId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<CropMaturityObservation>> getCropMaturityObservations(String id, Authentication authentication) throws ForbiddenException;

  CropMaturityObservation createCropMaturityObservation(String id, @Valid CropMaturityObservation cropMaturityObservation, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<CropMaturityObservation> getCropMaturityObservation(String id, String observationId, Authentication authentication) throws ForbiddenException;

  void deleteCropMaturityObservation(String id, String observationId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<List<DataFile>> getDataFiles(String id, Authentication authentication) throws ForbiddenException;

  DataFile createDataFile(String id, @Valid DataFile dataFile, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<DataFile> getDataFile(String id, String dataFileId, Authentication authentication) throws ForbiddenException;

  void deleteDataFile(String id, String dataFileId, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  void setDataFileFile(String id, String dataFileId, @Valid TypedResource typedResource, Authentication authentication) throws DoesNotExistException, ForbiddenException;

  Optional<TypedResource> getDataFileFile(String id, String dataFileId, Authentication authentication) throws ForbiddenException;

}
