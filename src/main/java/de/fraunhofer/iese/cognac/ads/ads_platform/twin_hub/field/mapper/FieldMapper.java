package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.mapper;

import de.fraunhofer.iese.cognac.ads.ads_platform.mapper.MapConfig;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.CropMaturityObservationDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.DataFileDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.FieldDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.NitrogenOutgassingObservationDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.OperationType;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.PlantObservationDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.RecommendationDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.SoilCompositionObservationDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.SoilMeasurementWorkOrderDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.SoilMeasurementWorkRecordDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.SoilNutrientsObservationDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.VegetationIndexObservationDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.WorkRecordDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.CropMaturityObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.DataFile;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.Field;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.NitrogenOutgassingObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.PlantObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.Recommendation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilCompositionObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilMeasurementWorkOrder;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilMeasurementWorkRecord;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilNutrientsObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.VegetationIndexObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.WorkRecord;

import org.geojson.Feature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapConfig.class)
public interface FieldMapper {
  FieldDto mapEntityToDto(Field field);

  Field mapDtoToEntity(FieldDto fieldDto);

  WorkRecordDto mapEntityToDto(WorkRecord workRecord);

  WorkRecord mapDtoToEntity(WorkRecordDto workRecordDto);

  PlantObservationDto mapEntityToDto(PlantObservation plantObservation);

  @Mapping(target = "file", ignore = true)
  PlantObservation mapDtoToEntity(PlantObservationDto plantObservationDto);

  OperationType mapEntityToDto(
      de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.OperationType operationType);

  de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.OperationType mapDtoToEntity(
      OperationType operationType);

  CropMaturityObservationDto mapEntityToDto(CropMaturityObservation cropMaturityObservation);

  CropMaturityObservation mapDtoToEntity(CropMaturityObservationDto cropMaturityObservationDto);

  NitrogenOutgassingObservationDto mapEntityToDto(NitrogenOutgassingObservation nitrogenOutgassingObservation);

  @Mapping(target = "file", ignore = true)
  NitrogenOutgassingObservation mapDtoToEntity(NitrogenOutgassingObservationDto nitrogenOutgassingObservationDto);

  @Mapping(target = "fileMediaType", source = "file.mimeType")
  @Mapping(target = "fileAvailable", expression = "java(recommendation.getFile()!=null)")
  RecommendationDto mapEntityToDto(Recommendation recommendation);

  @Mapping(target = "file", ignore = true)
  Recommendation mapDtoToEntity(RecommendationDto recommendationDto);

  SoilCompositionObservationDto mapEntityToDto(SoilCompositionObservation soilCompositionObservation);

  @Mapping(target = "file", ignore = true)
  SoilCompositionObservation mapDtoToEntity(SoilCompositionObservationDto soilCompositionObservationDto);

  SoilNutrientsObservationDto mapEntityToDto(SoilNutrientsObservation soilNutrientsObservation);

  @Mapping(target = "file", ignore = true)
  SoilNutrientsObservation mapDtoToEntity(SoilNutrientsObservationDto soilNutrientsObservationDto);

  VegetationIndexObservationDto mapEntityToDto(VegetationIndexObservation vegetationIndexObservation);

  @Mapping(target = "file", ignore = true)
  VegetationIndexObservation mapDtoToEntity(VegetationIndexObservationDto vegetationIndexObservationDto);

  SoilMeasurementWorkOrderDto mapEntityToDto(SoilMeasurementWorkOrder soilMeasurementWorkOrder);

  @Mapping(target = "file", ignore = true)
  SoilMeasurementWorkOrder mapDtoToEntity(SoilMeasurementWorkOrderDto soilMeasurementWorkOrderDto);

  SoilMeasurementWorkRecordDto mapEntityToDto(SoilMeasurementWorkRecord soilMeasurementWorkRecord);

  @Mapping(target = "file", ignore = true)
  SoilMeasurementWorkRecord mapDtoToEntity(SoilMeasurementWorkRecordDto soilMeasurementWorkRecordDto);

  @Mapping(target = "filename", expression = "java((null==dataFile.getFile())?null:java.nio.file.Paths.get(dataFile.getFile().getPath()).getFileName().toString())")
  @Mapping(target = "fileMediaType", source = "file.mimeType")
  DataFileDto mapEntityToDto(DataFile dataFile);

  @Mapping(target = "file", ignore = true)
  DataFile mapDtoToEntity(DataFileDto dataFileDto);

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "createdAt", ignore = true),
      @Mapping(target = "createdBy", ignore = true),
      @Mapping(target = "cropSeason", ignore = true),
      @Mapping(target = "description", ignore = true),
      @Mapping(target = "details", ignore = true),
      @Mapping(target = "endTime", ignore = true),
      @Mapping(target = "operationType", ignore = true),
      @Mapping(target = "startTime", ignore = true)
  })
  WorkRecordDto toWorkRecordDto(String workRecordString);

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "geometry", ignore = true),
      @Mapping(target = "crs", ignore = true),
      @Mapping(target = "bbox", ignore = true),
      @Mapping(target = "properties", ignore = true)
  })
  Feature toFeature(String featureString);

}
