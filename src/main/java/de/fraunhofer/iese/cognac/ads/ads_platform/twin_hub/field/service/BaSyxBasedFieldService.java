package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.InvalidInputException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.FileStorageException;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.FileStorageService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.basyx.service.BaSyxAccessService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.service.TwinOwnershipService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service.TwinHubEnforcementPoint;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.CropMaturityObservation;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.DataFile;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.Field;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.FileHolder;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.IdHolder;
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
import de.fraunhofer.iese.cognac.ads.ads_platform.util.basyx.BaSyxIdUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.geojson.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

@Service
@Validated
public class BaSyxBasedFieldService implements FieldService {
  private static final Logger logger = LoggerFactory.getLogger(BaSyxBasedFieldService.class);
  private static final String ELEMENT_ID = "field";

  static final String BASIC_INFORMATION_SUBMODEL_ID_SHORT = "field_basic_information";
  static final String BASIC_INFORMATION_SUBMODEL_NAME_PROPERTY_ID_SHORT = "name";
  private static final String BASIC_INFORMATION_SUBMODEL_FARM_ID_PROPERTY_ID_SHORT = "farmId";

  private static final String GEOGRAPHY_SUBMODEL_ID_SHORT = "field_geography";
  private static final String GEOGRAPHY_SUBMODEL_ARABLE_AREA_PROPERTY_ID_SHORT = "arableArea";
  private static final String GEOGRAPHY_SUBMODEL_TRAFFICABLE_AREA_PROPERTY_ID_SHORT = "trafficableArea";
  private static final String GEOGRAPHY_SUBMODEL_NON_TRAFFICABLE_AREA_PROPERTY_ID_SHORT = "nonTrafficableArea";
  private static final String GEOGRAPHY_SUBMODEL_TRACKS_PROPERTY_ID_SHORT = "tracks";

  private static final String WORK_RECORDS_SUBMODEL_ID_SHORT = "field_work_records";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT = "workRecords";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_CREATED_AT_PROPERTY_ID_SHORT = "createdAt";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_CREATED_BY_PROPERTY_ID_SHORT = "createdBy";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_CROP_SEASON_PROPERTY_ID_SHORT = "cropSeason";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_DESCRIPTION_PROPERTY_ID_SHORT = "description";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_DETAILS_PROPERTY_ID_SHORT = "details";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_END_TIME_PROPERTY_ID_SHORT = "endTime";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_START_TIME_PROPERTY_ID_SHORT = "startTime";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_OPERATION_TYPE_PROPERTY_ID_SHORT = "operationType";
  private static final String WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_SHAPEFILE_FILE_ID_SHORT = "shapefile";

  private static final String PLANT_OBSERVATIONS_SUBMODEL_ID_SHORT = "field_plant_observations";
  private static final String PLANT_OBSERVATIONS_SUBMODEL_PLANT_OBSERVATIONS_SMEC_ID_SHORT = "plantObservations";

  private static final String NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_ID_SHORT = "field_nitrogen_outgassing_observations";
  private static final String NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_NITROGEN_OUTGASSING_OBSERVATIONS_SMEC_ID_SHORT = "nitrogenOutgassingObservations";

  private static final String RECOMMENDATIONS_SUBMODEL_ID_SHORT = "field_recommendations";
  private static final String RECOMMENDATIONS_SUBMODEL_RECOMMENDATIONS_SMEC_ID_SHORT = "recommendations";

  private static final String SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_ID_SHORT = "field_soil_composition_observations";
  private static final String SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_SOIL_COMPOSITION_OBSERVATIONS_SMEC_ID_SHORT = "soilCompositionObservations";

  private static final String SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_ID_SHORT = "field_soil_nutrients_observations";
  private static final String SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT = "soilNutrientsObservations";

  private static final String VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_ID_SHORT = "field_vegetation_index_observations";
  private static final String VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT = "vegetationIndexObservations";


  private static final String SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_ID_SHORT = "field_soil_measurement_work_orders";
  private static final String SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_SOIL_MEASUREMENT_WORK_ORDERS_SMEC_ID_SHORT = "soilMeasurementWorkOrders";

  private static final String SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT = "field_soil_measurement_work_orders";
  private static final String SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT = "soilMeasurementWorkRecords";
  private static final String CROP_MATURITY_OBSERVATIONS_SUBMODEL_ID_SHORT = "field_crop_maturity_observations";
  private static final String CROP_MATURITY_OBSERVATIONS_SUBMODEL_CROP_MATURITY_OBSERVATIONS_SMEC_ID_SHORT = "cropMaturityObservations";

  private static final String DATA_FILES_SUBMODEL_ID_SHORT = "data_files";
  private static final String DATA_FILES_SUBMODEL_DATA_FILES_SMEC_ID_SHORT = "dataFiles";

  private final BaSyxAccessService baSyxAccessService;
  private final TwinOwnershipService twinOwnershipService;
  private final FileStorageService fileStorageService;
  private final ObjectMapper objectMapper;
  private final TwinHubEnforcementPoint twinHubEnforcementPoint;

  @Autowired
  public BaSyxBasedFieldService(final BaSyxAccessService baSyxAccessService, final TwinOwnershipService twinOwnershipService, final FileStorageService fileStorageService, final ObjectMapper objectMapper, final TwinHubEnforcementPoint twinHubEnforcementPoint) {
    this.baSyxAccessService = baSyxAccessService;
    this.twinOwnershipService = twinOwnershipService;
    this.fileStorageService = fileStorageService;
    this.objectMapper = objectMapper;
    this.twinHubEnforcementPoint = twinHubEnforcementPoint;
  }

  @Override
  public List<Field> getFields(final Authentication authentication) {
    // TODO improve?
    //final List<String> twinsOwnedByRequestor = this.twinOwnershipService.getTwinIdsByOwner(authentication.getSubject());
    return this.baSyxAccessService.doLookupAll().parallelStream()
        .filter(aasDescriptor -> ELEMENT_ID.equals(BaSyxIdUtil.extractElementId(aasDescriptor.getIdentifier())))
        //.filter(aasDescriptor -> twinsOwnedByRequestor.contains(aasDescriptor.getIdentifier().getId()))
        .filter(aasDescriptor -> {
          try {
            this.twinHubEnforcementPoint.enforceTwinEntityAccess(aasDescriptor.getIdentifier().getId(), Action.READ, authentication);
            return true;
          } catch (ForbiddenException e) {
            return false;
          }
        })
        .map(aasDescriptor -> {
          final Field field = new Field();
          field.setId(aasDescriptor.getIdentifier().getId());
          baSyxAccessService.doGetSubmodel(aasDescriptor.getIdentifier(), BASIC_INFORMATION_SUBMODEL_ID_SHORT)
              .ifPresent(submodel -> {
                final Map<String, IProperty> properties = submodel.getProperties();
                field.setName(Optional.ofNullable(properties.get(BASIC_INFORMATION_SUBMODEL_NAME_PROPERTY_ID_SHORT)).map(ISubmodelElement::getValue).map(String.class::cast).orElse(null));
                field.setFarmId(Optional.ofNullable(properties.get(BASIC_INFORMATION_SUBMODEL_FARM_ID_PROPERTY_ID_SHORT)).map(ISubmodelElement::getValue).map(String.class::cast).orElse(null));
              });
          return field;
        })
        .collect(Collectors.toList());
  }

  @Override
  public Field createField(@Valid final Field field, final Authentication authentication) throws ForbiddenException {
    final String id = generateRandomId();

    final Asset asset = new Asset("asset", BaSyxIdUtil.constructAssetInstanceIdentifier(ELEMENT_ID, id), AssetKind.INSTANCE);
    final IIdentifier aasId = BaSyxIdUtil.constructAASInstanceIdentifier(ELEMENT_ID, id);
    field.setId(aasId.getId());
    final AssetAdministrationShell aas = new AssetAdministrationShell("aas", aasId, asset);

    twinHubEnforcementPoint.enforceTwinEntityCreation(field.getId(), field.getName(), authentication);

    // TODO clarify whether to store owner information in AAS or the TwinHub's own database
    this.twinOwnershipService.setOwnerOfTwin(aas.getIdentification().getId(), authentication.getSubject());

    this.baSyxAccessService.doCreateAAS(aas);
    {
      final Submodel submodel = new Submodel(BASIC_INFORMATION_SUBMODEL_ID_SHORT, BaSyxIdUtil.constructSubmodelInstanceIdentifier(BASIC_INFORMATION_SUBMODEL_ID_SHORT, ELEMENT_ID, id));
      final Property nameProperty = new Property(BASIC_INFORMATION_SUBMODEL_NAME_PROPERTY_ID_SHORT, field.getName());
      submodel.addSubmodelElement(nameProperty);
      final Property farmIdProperty = new Property(BASIC_INFORMATION_SUBMODEL_FARM_ID_PROPERTY_ID_SHORT, field.getFarmId());
      submodel.addSubmodelElement(farmIdProperty);
      try {
        this.baSyxAccessService.doCreateSubmodel(aasId, submodel);
      } catch (DoesNotExistException e) {
        throw new RuntimeException("cannot create submodel");
      }
    }
    return field;
  }

  @Override
  public Optional<Field> getField(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinEntityAccess(id, Action.READ, authentication);

    return this.baSyxAccessService.doLookupAASById(BaSyxIdUtil.fromString(id))
        .map(aasDescriptor -> {
          final Field field = new Field();
          field.setId(aasDescriptor.getIdentifier().getId());
          baSyxAccessService.doGetSubmodel(aasDescriptor.getIdentifier(), BASIC_INFORMATION_SUBMODEL_ID_SHORT)
              .ifPresent(submodel -> {
                final Map<String, IProperty> properties = submodel.getProperties();
                field.setName(Optional.ofNullable(properties.get(BASIC_INFORMATION_SUBMODEL_NAME_PROPERTY_ID_SHORT)).map(ISubmodelElement::getValue).map(String.class::cast).orElse(null));
                field.setFarmId(Optional.ofNullable(properties.get(BASIC_INFORMATION_SUBMODEL_FARM_ID_PROPERTY_ID_SHORT)).map(ISubmodelElement::getValue).map(String.class::cast).orElse(null));
              });
          return field;
        });
  }

  @Override
  public Optional<Field> getFieldWithoutAuthentication(final String id) {
    return this.baSyxAccessService.doLookupAASById(BaSyxIdUtil.fromString(id))
        .map(aasDescriptor -> {
          final Field field = new Field();
          field.setId(aasDescriptor.getIdentifier().getId());
          baSyxAccessService.doGetSubmodel(aasDescriptor.getIdentifier(), BASIC_INFORMATION_SUBMODEL_ID_SHORT)
              .ifPresent(submodel -> {
                final Map<String, IProperty> properties = submodel.getProperties();
                field.setName(Optional.ofNullable(properties.get(BASIC_INFORMATION_SUBMODEL_NAME_PROPERTY_ID_SHORT)).map(ISubmodelElement::getValue).map(String.class::cast).orElse(null));
                field.setFarmId(Optional.ofNullable(properties.get(BASIC_INFORMATION_SUBMODEL_FARM_ID_PROPERTY_ID_SHORT)).map(ISubmodelElement::getValue).map(String.class::cast).orElse(null));
              });
          return field;
        });
  }

  @Override
  public void deleteField(final String id, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinEntityAccess(id, Action.DELETE, authentication);

    this.baSyxAccessService.doDeleteAAS(BaSyxIdUtil.fromString(id));
    this.twinOwnershipService.unsetOwnerOfTwin(id);
    final String path = constructPathForFieldRelatedFile(id, null);
    try {
      this.fileStorageService.deleteDirectory(path);
    } catch (FileStorageException e) {
      logger.warn("Unable to delete files for field {}", id);
    } catch (DoesNotExistException e) {
      // okay
    }
  }

  @Override
  public void setName(final String id, final String name, final Authentication authentication) throws DoesNotExistException, InvalidInputException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinEntityAccess(id, Action.UPDATE, authentication);
    if (StringUtils.isBlank(name)) {
      throw new InvalidInputException();
    }
    final IIdentifier aasId = BaSyxIdUtil.fromString(id);
    this.baSyxAccessService.doCreateSubmodelIfNotExistsAndPutSubmodelElements(BaSyxIdUtil.extractElementId(aasId), BaSyxIdUtil.extractElementInstance(aasId), BASIC_INFORMATION_SUBMODEL_ID_SHORT, Collections.singletonList(
        new Property(BASIC_INFORMATION_SUBMODEL_NAME_PROPERTY_ID_SHORT, name)
    ));
  }

  @Override
  public Optional<Feature> getArableArea(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.ARABLE_AREA, "/GEOMETRIES/arable_area", Action.READ, authentication);
    return this.baSyxAccessService.doReadSimplePropertyValueFromSubmodel(BaSyxIdUtil.fromString(id), GEOGRAPHY_SUBMODEL_ID_SHORT, GEOGRAPHY_SUBMODEL_ARABLE_AREA_PROPERTY_ID_SHORT, String.class)
        .map(s -> {
          try {
            return this.objectMapper.readValue(s, Feature.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public Optional<Feature> getArableAreaWithoutAuthentication(final String id) {
    return this.baSyxAccessService.doReadSimplePropertyValueFromSubmodel(BaSyxIdUtil.fromString(id), GEOGRAPHY_SUBMODEL_ID_SHORT, GEOGRAPHY_SUBMODEL_ARABLE_AREA_PROPERTY_ID_SHORT, String.class)
        .map(s -> {
          try {
            return this.objectMapper.readValue(s, Feature.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public void setArableArea(final String id, final Feature arableArea, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.ARABLE_AREA, "/GEOMETRIES/arable_area", Action.UPDATE, authentication);
    final String serializedArableArea;
    try {
      serializedArableArea = this.objectMapper.writeValueAsString(arableArea);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
    final IIdentifier aasId = BaSyxIdUtil.fromString(id);

    this.baSyxAccessService.doCreateSubmodelIfNotExistsAndPutSubmodelElements(BaSyxIdUtil.extractElementId(aasId), BaSyxIdUtil.extractElementInstance(aasId), GEOGRAPHY_SUBMODEL_ID_SHORT, Collections.singletonList(
        new Property(GEOGRAPHY_SUBMODEL_ARABLE_AREA_PROPERTY_ID_SHORT, serializedArableArea)
    ));
  }

  @Override
  public void setArableArea(final String id, final String arableArea, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.ARABLE_AREA, "/GEOMETRIES/arable_area", Action.UPDATE, authentication);

    final IIdentifier aasId = BaSyxIdUtil.fromString(id);
    System.out.println("OK1");
    this.baSyxAccessService.doCreateSubmodelIfNotExistsAndPutSubmodelElements(BaSyxIdUtil.extractElementId(aasId), BaSyxIdUtil.extractElementInstance(aasId), GEOGRAPHY_SUBMODEL_ID_SHORT, Collections.singletonList(
        new Property(GEOGRAPHY_SUBMODEL_ARABLE_AREA_PROPERTY_ID_SHORT, arableArea)
    ));
    System.out.println("OK2");
  }

  @Override
  public void deleteArableArea(final String id, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.ARABLE_AREA, "/GEOMETRIES/arable_area", Action.DELETE, authentication);
    this.baSyxAccessService.doDeleteSubmodelElementFromSubmodel(BaSyxIdUtil.fromString(id), GEOGRAPHY_SUBMODEL_ID_SHORT, GEOGRAPHY_SUBMODEL_ARABLE_AREA_PROPERTY_ID_SHORT);
  }

  @Override
  public Optional<Feature> getTrafficableArea(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.TRAFFICABLE_AREA, "/GEOMETRIES/trafficable_area", Action.READ, authentication);
    return this.baSyxAccessService.doReadSimplePropertyValueFromSubmodel(BaSyxIdUtil.fromString(id), GEOGRAPHY_SUBMODEL_ID_SHORT, GEOGRAPHY_SUBMODEL_TRAFFICABLE_AREA_PROPERTY_ID_SHORT, String.class)
        .map(s -> {
          try {
            return this.objectMapper.readValue(s, Feature.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public void setTrafficableArea(final String id, final Feature trafficableArea, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.TRAFFICABLE_AREA, "/GEOMETRIES/trafficable_area", Action.UPDATE, authentication);
    final String serializedTrafficableArea;
    try {
      serializedTrafficableArea = this.objectMapper.writeValueAsString(trafficableArea);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
    final IIdentifier aasId = BaSyxIdUtil.fromString(id);

    this.baSyxAccessService.doCreateSubmodelIfNotExistsAndPutSubmodelElements(BaSyxIdUtil.extractElementId(aasId), BaSyxIdUtil.extractElementInstance(aasId), GEOGRAPHY_SUBMODEL_ID_SHORT, Collections.singletonList(
        new Property(GEOGRAPHY_SUBMODEL_TRAFFICABLE_AREA_PROPERTY_ID_SHORT, serializedTrafficableArea)
    ));
  }

  @Override
  public void deleteTrafficableArea(final String id, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.TRAFFICABLE_AREA, "/GEOMETRIES/trafficable_area", Action.DELETE, authentication);
    this.baSyxAccessService.doDeleteSubmodelElementFromSubmodel(BaSyxIdUtil.fromString(id), GEOGRAPHY_SUBMODEL_ID_SHORT, GEOGRAPHY_SUBMODEL_TRAFFICABLE_AREA_PROPERTY_ID_SHORT);
  }

  @Override
  public Optional<Feature> getNonTrafficableArea(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NON_TRAFFICABLE_AREA, "/GEOMETRIES/non_trafficable_area", Action.READ, authentication);
    return this.baSyxAccessService.doReadSimplePropertyValueFromSubmodel(BaSyxIdUtil.fromString(id), GEOGRAPHY_SUBMODEL_ID_SHORT, GEOGRAPHY_SUBMODEL_NON_TRAFFICABLE_AREA_PROPERTY_ID_SHORT, String.class)
        .map(s -> {
          try {
            return this.objectMapper.readValue(s, Feature.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public void setNonTrafficableArea(final String id, final Feature nonTrafficableArea, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NON_TRAFFICABLE_AREA, "/GEOMETRIES/non_trafficable_area", Action.UPDATE, authentication);
    final String serializedNonTrafficableArea;
    try {
      serializedNonTrafficableArea = this.objectMapper.writeValueAsString(nonTrafficableArea);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
    final IIdentifier aasId = BaSyxIdUtil.fromString(id);

    this.baSyxAccessService.doCreateSubmodelIfNotExistsAndPutSubmodelElements(BaSyxIdUtil.extractElementId(aasId), BaSyxIdUtil.extractElementInstance(aasId), GEOGRAPHY_SUBMODEL_ID_SHORT, Collections.singletonList(
        new Property(GEOGRAPHY_SUBMODEL_NON_TRAFFICABLE_AREA_PROPERTY_ID_SHORT, serializedNonTrafficableArea)
    ));
  }

  @Override
  public void deleteNonTrafficableArea(final String id, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NON_TRAFFICABLE_AREA, "/GEOMETRIES/non_trafficable_area", Action.DELETE, authentication);
    this.baSyxAccessService.doDeleteSubmodelElementFromSubmodel(BaSyxIdUtil.fromString(id), GEOGRAPHY_SUBMODEL_ID_SHORT, GEOGRAPHY_SUBMODEL_NON_TRAFFICABLE_AREA_PROPERTY_ID_SHORT);
  }

  @Override
  public Optional<Feature> getTracks(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.TRACKS, "/GEOMETRIES/tracks", Action.READ, authentication);
    return this.baSyxAccessService.doReadSimplePropertyValueFromSubmodel(BaSyxIdUtil.fromString(id), GEOGRAPHY_SUBMODEL_ID_SHORT, GEOGRAPHY_SUBMODEL_TRACKS_PROPERTY_ID_SHORT, String.class)
        .map(s -> {
          try {
            return this.objectMapper.readValue(s, Feature.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public void setTracks(final String id, final Feature tracks, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.TRACKS, "/GEOMETRIES/tracks", Action.UPDATE, authentication);
    final String serializedTracks;
    try {
      serializedTracks = this.objectMapper.writeValueAsString(tracks);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
    final IIdentifier aasId = BaSyxIdUtil.fromString(id);

    this.baSyxAccessService.doCreateSubmodelIfNotExistsAndPutSubmodelElements(BaSyxIdUtil.extractElementId(aasId), BaSyxIdUtil.extractElementInstance(aasId), GEOGRAPHY_SUBMODEL_ID_SHORT, Collections.singletonList(
        new Property(GEOGRAPHY_SUBMODEL_TRACKS_PROPERTY_ID_SHORT, serializedTracks)
    ));
  }

  @Override
  public void deleteTracks(final String id, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.TRACKS, "/GEOMETRIES/tracks", Action.DELETE, authentication);
    this.baSyxAccessService.doDeleteSubmodelElementFromSubmodel(BaSyxIdUtil.fromString(id), GEOGRAPHY_SUBMODEL_ID_SHORT, GEOGRAPHY_SUBMODEL_TRACKS_PROPERTY_ID_SHORT);
  }

  @Override
  public Optional<List<WorkRecord>> getWorkRecords(final String id, @Nullable final OperationType operationType, @Nullable final String cropSeason, final Authentication authentication) {
    final Optional<AASDescriptor> aasDescriptorOptional = this.baSyxAccessService.doLookupAASById(BaSyxIdUtil.fromString(id));
    if (aasDescriptorOptional.isEmpty()) {
      return Optional.empty();
    } else {
      final AASDescriptor aasDescriptor = aasDescriptorOptional.get();
      return Optional.of(
          this.baSyxAccessService.doGetSubmodel(aasDescriptor.getIdentifier(), WORK_RECORDS_SUBMODEL_ID_SHORT)
              .map(submodel -> submodel.getSubmodelElements().get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT))
              .map(ISubmodelElement::getLocalCopy)
              .map(ISubmodelElementCollection.class::cast)
              .map(ISubmodelElementCollection::getSubmodelElements)
              .map(Map::values)
              .map(
                  submodelElements -> submodelElements.stream()
                      .map(ISubmodelElementCollection.class::cast)
                      .map(this::extractWorkRecordFromSubmodelElementCollection)
                      .filter(workRecord -> Optional.ofNullable(operationType).map(ot -> Objects.equals(ot, workRecord.getOperationType())).orElse(true))
                      .filter(workRecord -> Optional.ofNullable(cropSeason).map(cs -> Objects.equals(cs, workRecord.getCropSeason())).orElse(true))
                      .filter(workRecord -> {
                        try {
                          this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, mapOperationTypeToTwinResource(workRecord.getOperationType()), "/" + mapOperationTypeToTwinResource(workRecord.getOperationType()).toString() + "/" + workRecord.getId(), Action.READ, authentication);
                          return true;
                        } catch (ForbiddenException e) {
                          return false;
                        }
                      })
                      .collect(Collectors.toList())
              ).orElseGet(Collections::emptyList)
      );
    }
  }

  private TwinResource mapOperationTypeToTwinResource(OperationType operationType) throws ForbiddenException {
    if (operationType == OperationType.FERTILIZATION) {
      return TwinResource.WORK_RECORDS_FERTILIZATION;
    } else if (operationType == OperationType.WEED_CONTROL) {
      return TwinResource.WORK_RECORDS_WEED_CONTROL;
    } else if (operationType == OperationType.SEEDING) {
      return TwinResource.WORK_RECORDS_SEEDING;
    } else if (operationType == OperationType.HARVESTING) {
      return TwinResource.WORK_RECORDS_HARVESTING;
    }
    logger.debug("Cannot map OperationType {} to TwinResource", operationType);
    throw new ForbiddenException();
  }

  private String generateRandomId() {
    return "i" + UUID.randomUUID().toString().replaceAll("-", "");
  }

  @Override
  public WorkRecord createWorkRecord(final String id, @Valid final WorkRecord workRecord, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    workRecord.setId(generateRandomId());
    workRecord.setCreatedAt(Instant.now());
    final IIdentifier aasId = BaSyxIdUtil.fromString(id);

    final TwinResource twinResource = mapOperationTypeToTwinResource(workRecord.getOperationType());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, twinResource, "/" + twinResource.toString() + "/" + workRecord.getId(), Action.CREATE, authentication);

    final Optional<ISubmodel> submodelOptional = this.baSyxAccessService.doGetSubmodel(aasId, WORK_RECORDS_SUBMODEL_ID_SHORT);
    if (submodelOptional.isEmpty()) {
      final Submodel submodel = new Submodel(WORK_RECORDS_SUBMODEL_ID_SHORT, BaSyxIdUtil.constructSubmodelInstanceIdentifier(WORK_RECORDS_SUBMODEL_ID_SHORT, ELEMENT_ID, BaSyxIdUtil.extractElementInstance(aasId)));
      final SubmodelElementCollection submodelElementCollection = new SubmodelElementCollection(WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT);
      submodel.addSubmodelElement(submodelElementCollection);
      this.baSyxAccessService.doCreateSubmodel(aasId, submodel);
    } else {
      final ISubmodel submodel = submodelOptional.get();
      if (!submodel.getSubmodelElements().containsKey(WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT)) {
        final SubmodelElementCollection submodelElementCollection = new SubmodelElementCollection(WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT);
        submodel.addSubmodelElement(submodelElementCollection);
      }
    }
    final SubmodelElementCollection workRecordEntry = this.generateSubmodelElementCollectionFromWorkRecord(workRecord);

    final ISubmodelElementCollection submodelElementCollection = this.baSyxAccessService.doReadSubmodelElementFromSubmodel(aasId, WORK_RECORDS_SUBMODEL_ID_SHORT, WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT, ISubmodelElementCollection.class).orElseThrow();
    submodelElementCollection.addSubmodelElement(workRecordEntry);
    return workRecord;
  }

  @Override
  public Optional<WorkRecord> getWorkRecord(final String id, final String workRecordId, final Authentication authentication) throws ForbiddenException {
    //TODO: check is afterwards
    Optional<WorkRecord> workRecord = this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), WORK_RECORDS_SUBMODEL_ID_SHORT)
        .map(submodel -> submodel.getSubmodelElements().get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT))
        .map(ISubmodelElement::getLocalCopy)
        .map(ISubmodelElementCollection.class::cast)
        .map(ISubmodelElementCollection::getSubmodelElements)
        .map(workRecordEntries -> workRecordEntries.get(workRecordId))
        .map(ISubmodelElementCollection.class::cast)
        .map(this::extractWorkRecordFromSubmodelElementCollection);

    if (workRecord.isEmpty()) {
      return Optional.empty();
    }
    final TwinResource twinResource = mapOperationTypeToTwinResource(workRecord.get().getOperationType());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(
        id,
        twinResource,
        "/" + twinResource.toString() + "/" + workRecord.get().getId(),
        Action.READ,
        authentication);
    return workRecord;
  }

  @Override
  public void deleteWorkRecord(final String id, final String workRecordId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    final ISubmodelElementCollection workRecordsSubmodelElementCollection = this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), WORK_RECORDS_SUBMODEL_ID_SHORT)
        .map(submodel -> submodel.getSubmodelElements().get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT))
        .map(ISubmodelElementCollection.class::cast).orElseThrow(DoesNotExistException::new);

    final Map<String, ISubmodelElement> workRecordsSubmodelElementCollectionSubmodelElements = ((ISubmodelElementCollection) workRecordsSubmodelElementCollection.getLocalCopy()).getSubmodelElements();
    if (workRecordsSubmodelElementCollectionSubmodelElements.containsKey(workRecordId)) {
      final WorkRecord workRecord = extractWorkRecordFromSubmodelElementCollection((ISubmodelElementCollection) workRecordsSubmodelElementCollectionSubmodelElements.get(workRecordId));
      final TwinResource twinResource = mapOperationTypeToTwinResource(workRecord.getOperationType());
      this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, twinResource, "/" + twinResource.toString() + "/" + workRecord.getId(), Action.DELETE, authentication);

      workRecordsSubmodelElementCollection.deleteSubmodelElement(workRecordId);

      final String shapefilePath = String.format("%s/%s/%s", WORK_RECORDS_SUBMODEL_ID_SHORT, WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT, workRecordId);
      final String path = constructPathForFieldRelatedFile(id, shapefilePath);
      try {
        this.fileStorageService.deleteDirectory(path);
      } catch (FileStorageException e) {
        logger.warn("Unable to delete files for work-record (id={}, workRecordId={})", id, workRecordId, e);
      } catch (DoesNotExistException e) {
        // okay
      }
    } else {
      throw new DoesNotExistException();
    }

  }

  @Override
  public void setWorkRecordShapefile(final String id, final String workRecordId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    final Optional<ISubmodelElementCollection> workRecordEntryOptional = this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), WORK_RECORDS_SUBMODEL_ID_SHORT)
        .map(submodel -> submodel.getSubmodelElements().get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT))
        .map(ISubmodelElementCollection.class::cast)
        .map(ISubmodelElementCollection::getSubmodelElements)
        .map(workRecordEntries -> workRecordEntries.get(workRecordId))
        .map(ISubmodelElementCollection.class::cast);

    if (workRecordEntryOptional.isEmpty()) {
      throw new DoesNotExistException(); // work record does not exist
    } else {
      final ISubmodelElementCollection workRecordEntry = workRecordEntryOptional.get();
      WorkRecord workRecord = extractWorkRecordFromSubmodelElementCollection(workRecordEntry);
      this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, mapOperationTypeToTwinResource(workRecord.getOperationType()), "/" + mapOperationTypeToTwinResource(workRecord.getOperationType()).toString() + "/" + workRecord.getId() + "/shapefile", Action.UPDATE, authentication);

      // store new file
      final Resource resource = typedResource.getResource();
      final String mediaType = typedResource.getMediaType();
      final String newShapefilePath = String.format("%s/%s/%s/%s/%s", WORK_RECORDS_SUBMODEL_ID_SHORT, WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT, workRecordId, "shapefiles", encodePathSegment(Optional.ofNullable(resource.getFilename()).orElse("data")));
      final String newPath = constructPathForFieldRelatedFile(id, newShapefilePath);
      try {
        this.fileStorageService.storeFile(newPath, resource);
      } catch (FileStorageException e) {
        throw new RuntimeException(e);
      }

      // clean up old file if necessary
      final Map<String, ISubmodelElement> workRecordEntrySubmodelElements = workRecordEntry.getSubmodelElements();
      if (workRecordEntrySubmodelElements.containsKey(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_SHAPEFILE_FILE_ID_SHORT)) {
        final IFile oldShapefile = (IFile) workRecordEntrySubmodelElements.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_SHAPEFILE_FILE_ID_SHORT);
        final String oldShapefilePath = oldShapefile.getValue();
        final String oldPath = constructPathForFieldRelatedFile(id, oldShapefilePath);
        if (!oldPath.equals(newPath)) {
          // delete old file
          try {
            this.fileStorageService.deleteFile(oldPath);
          } catch (FileStorageException e) {
            logger.warn("unable to delete old file " + oldPath, e);
          } catch (DoesNotExistException e) {
            // okay, already gone
          }
        }
      }

      // take note of file reference
      final File newShapefile = new File(newShapefilePath, mediaType);
      newShapefile.setIdShort(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_SHAPEFILE_FILE_ID_SHORT);
      workRecordEntry.addSubmodelElement(newShapefile);
    }
  }

  @Override
  public Optional<TypedResource> getWorkRecordShapefile(final String id, final String workRecordId, final Authentication authentication) throws ForbiddenException {
    final Optional<ISubmodelElementCollection> workRecordEntryOptional = this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), WORK_RECORDS_SUBMODEL_ID_SHORT)
        .map(submodel -> submodel.getSubmodelElements().get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT))
        .map(ISubmodelElement::getLocalCopy)
        .map(ISubmodelElementCollection.class::cast)
        .map(ISubmodelElementCollection::getSubmodelElements)
        .map(workRecordEntries -> workRecordEntries.get(workRecordId))
        .map(ISubmodelElementCollection.class::cast);
    final Optional<IFile> shapefileOptional = workRecordEntryOptional
        .map(workRecordEntry -> workRecordEntry.getSubmodelElements().get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_SHAPEFILE_FILE_ID_SHORT))
        .map(IFile.class::cast);

    if (workRecordEntryOptional.isEmpty()) {
      return Optional.empty();
    }
    final WorkRecord workRecord = extractWorkRecordFromSubmodelElementCollection(workRecordEntryOptional.get());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, mapOperationTypeToTwinResource(workRecord.getOperationType()), "/" + mapOperationTypeToTwinResource(workRecord.getOperationType()).toString() + "/" + workRecord.getId() + "/shapefile", Action.READ, authentication);


    if (shapefileOptional.isEmpty()) {
      return Optional.empty(); // there is no file reference
    } else {
      final IFile shapefile = shapefileOptional.get();
      final String shapefilePath = shapefile.getValue();
      final String path = constructPathForFieldRelatedFile(id, shapefilePath);
      try {
        final Optional<Resource> resourceOptional = this.fileStorageService.loadFile(path);
        if (resourceOptional.isEmpty()) {
          // there is a file reference but the file is missing from storage
          logger.warn("File missing: {}", path);
          throw new RuntimeException("file missing");
        } else {
          return resourceOptional
              .map(resource -> TypedResource.of(resource, shapefile.getMimeType()));
        }
      } catch (FileStorageException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void deleteWorkRecordShapefile(final String id, final String workRecordId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {

    final Optional<ISubmodelElementCollection> workRecordEntryOptional = this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), WORK_RECORDS_SUBMODEL_ID_SHORT)
        .map(submodel -> submodel.getSubmodelElements().get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_SMEC_ID_SHORT))
        .map(ISubmodelElementCollection.class::cast)
        .map(ISubmodelElementCollection::getSubmodelElements)
        .map(workRecordEntries -> workRecordEntries.get(workRecordId))
        .map(ISubmodelElementCollection.class::cast);

    if (workRecordEntryOptional.isEmpty()) {
      throw new DoesNotExistException(); // workRecord does not exist
    } else {
      final ISubmodelElementCollection workRecordEntry = workRecordEntryOptional.get();
      WorkRecord workRecord = extractWorkRecordFromSubmodelElementCollection(workRecordEntry);
      this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, mapOperationTypeToTwinResource(workRecord.getOperationType()), "/" + mapOperationTypeToTwinResource(workRecord.getOperationType()).toString() + "/" + workRecord.getId() + "/shapefile", Action.UPDATE, authentication);
      final Map<String, ISubmodelElement> workRecordEntrySubmodelElements = workRecordEntry.getSubmodelElements();
      if (!workRecordEntrySubmodelElements.containsKey(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_SHAPEFILE_FILE_ID_SHORT)) {
        throw new DoesNotExistException(); // there is no file reference
      } else {
        final IFile shapefile = (IFile) workRecordEntrySubmodelElements.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_SHAPEFILE_FILE_ID_SHORT);
        final String shapefilePath = shapefile.getValue();
        final String path = constructPathForFieldRelatedFile(id, shapefilePath);
        try {
          this.fileStorageService.deleteFile(path);
        } catch (FileStorageException e) {
          throw new RuntimeException(e);
        } catch (DoesNotExistException e) {
          // okay, already gone
        }
        workRecordEntry.deleteSubmodelElement(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_SHAPEFILE_FILE_ID_SHORT);
      }
    }
  }

  @Override
  public Optional<List<PlantObservation>> getPlantObservations(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.PLANT_OBSERVATIONS, "/PLANT_OBSERVATIONS", Action.READ, authentication);
    return getEntries(id, PLANT_OBSERVATIONS_SUBMODEL_ID_SHORT, PLANT_OBSERVATIONS_SUBMODEL_PLANT_OBSERVATIONS_SMEC_ID_SHORT, PlantObservation.class);
  }

  @Override
  public PlantObservation createPlantObservation(final String id, @Valid final PlantObservation plantObservation, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    plantObservation.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.PLANT_OBSERVATIONS, "/PLANT_OBSERVATIONS/" + plantObservation.getId(), Action.CREATE, authentication);
    plantObservation.setCreatedAt(Instant.now());
    return createEntry(id, PLANT_OBSERVATIONS_SUBMODEL_ID_SHORT, PLANT_OBSERVATIONS_SUBMODEL_PLANT_OBSERVATIONS_SMEC_ID_SHORT, plantObservation);
  }

  @Override
  public Optional<PlantObservation> getPlantObservation(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.PLANT_OBSERVATIONS, "/PLANT_OBSERVATIONS/" + observationId, Action.READ, authentication);
    return getEntry(id, PLANT_OBSERVATIONS_SUBMODEL_ID_SHORT, PLANT_OBSERVATIONS_SUBMODEL_PLANT_OBSERVATIONS_SMEC_ID_SHORT, PlantObservation.class, observationId);
  }

  @Override
  public void deletePlantObservation(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.PLANT_OBSERVATIONS, "/PLANT_OBSERVATIONS/" + observationId, Action.DELETE, authentication);
    deleteEntry(id, PLANT_OBSERVATIONS_SUBMODEL_ID_SHORT, PLANT_OBSERVATIONS_SUBMODEL_PLANT_OBSERVATIONS_SMEC_ID_SHORT, observationId);
  }

  @Override
  public void setPlantObservationFile(final String id, final String observationId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.PLANT_OBSERVATIONS, "/PLANT_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    setEntryFile(id, PLANT_OBSERVATIONS_SUBMODEL_ID_SHORT, PLANT_OBSERVATIONS_SUBMODEL_PLANT_OBSERVATIONS_SMEC_ID_SHORT, PlantObservation.class, observationId, typedResource);
  }

  @Override
  public Optional<TypedResource> getPlantObservationFile(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.PLANT_OBSERVATIONS, "/PLANT_OBSERVATIONS/" + observationId + "/file", Action.READ, authentication);
    return getEntryFile(id, PLANT_OBSERVATIONS_SUBMODEL_ID_SHORT, PLANT_OBSERVATIONS_SUBMODEL_PLANT_OBSERVATIONS_SMEC_ID_SHORT, PlantObservation.class, observationId);
  }

  @Override
  public void deletePlantObservationFile(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.PLANT_OBSERVATIONS, "/PLANT_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    deleteEntryFile(id, PLANT_OBSERVATIONS_SUBMODEL_ID_SHORT, PLANT_OBSERVATIONS_SUBMODEL_PLANT_OBSERVATIONS_SMEC_ID_SHORT, PlantObservation.class, observationId);
  }

  @Override
  public Optional<List<NitrogenOutgassingObservation>> getNitrogenOutgassingObservations(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NITROGEN_OUTGASSING_OBSERVATIONS, "/NITROGEN_OUTGASSING_OBSERVATIONS", Action.READ, authentication);
    return getEntries(id, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_ID_SHORT, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_NITROGEN_OUTGASSING_OBSERVATIONS_SMEC_ID_SHORT, NitrogenOutgassingObservation.class);
  }

  @Override
  public NitrogenOutgassingObservation createNitrogenOutgassingObservation(final String id, @Valid final NitrogenOutgassingObservation nitrogenOutgassingObservation, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    nitrogenOutgassingObservation.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NITROGEN_OUTGASSING_OBSERVATIONS, "/NITROGEN_OUTGASSING_OBSERVATIONS/" + nitrogenOutgassingObservation.getId(), Action.CREATE, authentication);
    nitrogenOutgassingObservation.setCreatedAt(Instant.now());
    return createEntry(id, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_ID_SHORT, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_NITROGEN_OUTGASSING_OBSERVATIONS_SMEC_ID_SHORT, nitrogenOutgassingObservation);
  }

  @Override
  public Optional<NitrogenOutgassingObservation> getNitrogenOutgassingObservation(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NITROGEN_OUTGASSING_OBSERVATIONS, "/NITROGEN_OUTGASSING_OBSERVATIONS/" + observationId, Action.READ, authentication);
    return getEntry(id, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_ID_SHORT, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_NITROGEN_OUTGASSING_OBSERVATIONS_SMEC_ID_SHORT, NitrogenOutgassingObservation.class, observationId);
  }

  @Override
  public void deleteNitrogenOutgassingObservation(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NITROGEN_OUTGASSING_OBSERVATIONS, "/NITROGEN_OUTGASSING_OBSERVATIONS/" + observationId, Action.DELETE, authentication);
    deleteEntry(id, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_ID_SHORT, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_NITROGEN_OUTGASSING_OBSERVATIONS_SMEC_ID_SHORT, observationId);
  }

  @Override
  public void setNitrogenOutgassingObservationFile(final String id, final String observationId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NITROGEN_OUTGASSING_OBSERVATIONS, "/NITROGEN_OUTGASSING_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    setEntryFile(id, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_ID_SHORT, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_NITROGEN_OUTGASSING_OBSERVATIONS_SMEC_ID_SHORT, NitrogenOutgassingObservation.class, observationId, typedResource);
  }

  @Override
  public Optional<TypedResource> getNitrogenOutgassingObservationFile(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NITROGEN_OUTGASSING_OBSERVATIONS, "/NITROGEN_OUTGASSING_OBSERVATIONS/" + observationId + "/file", Action.READ, authentication);
    return getEntryFile(id, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_ID_SHORT, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_NITROGEN_OUTGASSING_OBSERVATIONS_SMEC_ID_SHORT, NitrogenOutgassingObservation.class, observationId);
  }

  @Override
  public void deleteNitrogenOutgassingObservationFile(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.NITROGEN_OUTGASSING_OBSERVATIONS, "/NITROGEN_OUTGASSING_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    deleteEntryFile(id, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_ID_SHORT, NITROGEN_OUTGASSING_OBSERVATIONS_SUBMODEL_NITROGEN_OUTGASSING_OBSERVATIONS_SMEC_ID_SHORT, NitrogenOutgassingObservation.class, observationId);
  }

  @Override
  public Optional<List<Recommendation>> getRecommendations(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.RECOMMENDATIONS, "/RECOMMENDATIONS", Action.READ, authentication);
    return getEntries(id, RECOMMENDATIONS_SUBMODEL_ID_SHORT, RECOMMENDATIONS_SUBMODEL_RECOMMENDATIONS_SMEC_ID_SHORT, Recommendation.class);
  }

  @Override
  public Recommendation createRecommendation(final String id, @Valid final Recommendation recommendation, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    recommendation.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.RECOMMENDATIONS, "/RECOMMENDATIONS/" + recommendation.getId(), Action.CREATE, authentication);
    recommendation.setCreatedAt(Instant.now());
    return createEntry(id, RECOMMENDATIONS_SUBMODEL_ID_SHORT, RECOMMENDATIONS_SUBMODEL_RECOMMENDATIONS_SMEC_ID_SHORT, recommendation);
  }

  @Override
  public Optional<Recommendation> getRecommendation(final String id, final String recommendationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.RECOMMENDATIONS, "/RECOMMENDATIONS/" + recommendationId, Action.READ, authentication);
    return getEntry(id, RECOMMENDATIONS_SUBMODEL_ID_SHORT, RECOMMENDATIONS_SUBMODEL_RECOMMENDATIONS_SMEC_ID_SHORT, Recommendation.class, recommendationId);
  }

  @Override
  public void deleteRecommendation(final String id, final String recommendationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.RECOMMENDATIONS, "/RECOMMENDATIONS/" + recommendationId, Action.DELETE, authentication);
    deleteEntry(id, RECOMMENDATIONS_SUBMODEL_ID_SHORT, RECOMMENDATIONS_SUBMODEL_RECOMMENDATIONS_SMEC_ID_SHORT, recommendationId);
  }

  @Override
  public void setRecommendationFile(final String id, final String recommendationId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.RECOMMENDATIONS, "/RECOMMENDATIONS/" + recommendationId + "/file", Action.UPDATE, authentication);
    setEntryFile(id, RECOMMENDATIONS_SUBMODEL_ID_SHORT, RECOMMENDATIONS_SUBMODEL_RECOMMENDATIONS_SMEC_ID_SHORT, Recommendation.class, recommendationId, typedResource);
  }

  @Override
  public Optional<TypedResource> getRecommendationFile(final String id, final String recommendationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.RECOMMENDATIONS, "/RECOMMENDATIONS/" + recommendationId + "/file", Action.READ, authentication);
    return getEntryFile(id, RECOMMENDATIONS_SUBMODEL_ID_SHORT, RECOMMENDATIONS_SUBMODEL_RECOMMENDATIONS_SMEC_ID_SHORT, Recommendation.class, recommendationId);
  }

  @Override
  public void deleteRecommendationFile(final String id, final String recommendationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.RECOMMENDATIONS, "/RECOMMENDATIONS/" + recommendationId + "/file", Action.UPDATE, authentication);
    deleteEntryFile(id, RECOMMENDATIONS_SUBMODEL_ID_SHORT, RECOMMENDATIONS_SUBMODEL_RECOMMENDATIONS_SMEC_ID_SHORT, Recommendation.class, recommendationId);
  }

  @Override
  public Optional<List<SoilCompositionObservation>> getSoilCompositionObservations(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_COMPOSITION_OBSERVATIONS, "/SOIL_COMPOSITION_OBSERVATIONS", Action.READ, authentication);
    return getEntries(id, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_SOIL_COMPOSITION_OBSERVATIONS_SMEC_ID_SHORT, SoilCompositionObservation.class);
  }

  @Override
  public SoilCompositionObservation createSoilCompositionObservation(final String id, @Valid final SoilCompositionObservation soilCompositionObservation, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    soilCompositionObservation.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_COMPOSITION_OBSERVATIONS, "/SOIL_COMPOSITION_OBSERVATIONS/" + soilCompositionObservation.getId(), Action.CREATE, authentication);
    soilCompositionObservation.setCreatedAt(Instant.now());
    return createEntry(id, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_SOIL_COMPOSITION_OBSERVATIONS_SMEC_ID_SHORT, soilCompositionObservation);
  }

  @Override
  public Optional<SoilCompositionObservation> getSoilCompositionObservation(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_COMPOSITION_OBSERVATIONS, "/SOIL_COMPOSITION_OBSERVATIONS/" + observationId, Action.READ, authentication);
    return getEntry(id, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_SOIL_COMPOSITION_OBSERVATIONS_SMEC_ID_SHORT, SoilCompositionObservation.class, observationId);
  }

  @Override
  public void deleteSoilCompositionObservation(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_COMPOSITION_OBSERVATIONS, "/SOIL_COMPOSITION_OBSERVATIONS/" + observationId, Action.DELETE, authentication);
    deleteEntry(id, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_SOIL_COMPOSITION_OBSERVATIONS_SMEC_ID_SHORT, observationId);
  }

  @Override
  public void setSoilCompositionObservationFile(final String id, final String observationId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_COMPOSITION_OBSERVATIONS, "/SOIL_COMPOSITION_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    setEntryFile(id, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_SOIL_COMPOSITION_OBSERVATIONS_SMEC_ID_SHORT, SoilCompositionObservation.class, observationId, typedResource);
  }

  @Override
  public Optional<TypedResource> getSoilCompositionObservationFile(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_COMPOSITION_OBSERVATIONS, "/SOIL_COMPOSITION_OBSERVATIONS/" + observationId + "/file", Action.READ, authentication);
    return getEntryFile(id, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_SOIL_COMPOSITION_OBSERVATIONS_SMEC_ID_SHORT, SoilCompositionObservation.class, observationId);
  }

  @Override
  public void deleteSoilCompositionObservationFile(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_COMPOSITION_OBSERVATIONS, "/SOIL_COMPOSITION_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    deleteEntryFile(id, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_COMPOSITION_OBSERVATIONS_SUBMODEL_SOIL_COMPOSITION_OBSERVATIONS_SMEC_ID_SHORT, SoilCompositionObservation.class, observationId);
  }

  @Override
  public Optional<List<SoilNutrientsObservation>> getSoilNutrientsObservations(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_NUTRIENTS_OBSERVATIONS, "/SOIL_NUTRIENTS_OBSERVATIONS", Action.READ, authentication);
    return getEntries(id, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, SoilNutrientsObservation.class);
  }

  @Override
  public SoilNutrientsObservation createSoilNutrientsObservation(final String id, @Valid final SoilNutrientsObservation soilNutrientsObservation, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    soilNutrientsObservation.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_NUTRIENTS_OBSERVATIONS, "/SOIL_NUTRIENTS_OBSERVATIONS/" + soilNutrientsObservation.getId(), Action.CREATE, authentication);
    soilNutrientsObservation.setCreatedAt(Instant.now());
    return createEntry(id, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, soilNutrientsObservation);
  }

  @Override
  public Optional<SoilNutrientsObservation> getSoilNutrientsObservation(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_NUTRIENTS_OBSERVATIONS, "/SOIL_NUTRIENTS_OBSERVATIONS/" + observationId, Action.READ, authentication);
    return getEntry(id, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, SoilNutrientsObservation.class, observationId);
  }

  @Override
  public void deleteSoilNutrientsObservation(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_NUTRIENTS_OBSERVATIONS, "/SOIL_NUTRIENTS_OBSERVATIONS/" + observationId, Action.DELETE, authentication);
    deleteEntry(id, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, observationId);
  }

  @Override
  public void setSoilNutrientsObservationFile(final String id, final String observationId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_NUTRIENTS_OBSERVATIONS, "/SOIL_NUTRIENTS_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    setEntryFile(id, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, SoilNutrientsObservation.class, observationId, typedResource);
  }

  @Override
  public Optional<TypedResource> getSoilNutrientsObservationFile(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_NUTRIENTS_OBSERVATIONS, "/SOIL_NUTRIENTS_OBSERVATIONS/" + observationId + "/file", Action.READ, authentication);
    return getEntryFile(id, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, SoilNutrientsObservation.class, observationId);
  }

  @Override
  public void deleteSoilNutrientsObservationFile(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_NUTRIENTS_OBSERVATIONS, "/SOIL_NUTRIENTS_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    deleteEntryFile(id, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_ID_SHORT, SOIL_NUTRIENTS_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, SoilNutrientsObservation.class, observationId);
  }

  @Override
  public Optional<List<VegetationIndexObservation>> getVegetationIndexObservations(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.VEGETATION_INDEX_OBSERVATIONS, "/VEGETATION_INDEX_OBSERVATIONS", Action.READ, authentication);
    return getEntries(id, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_ID_SHORT, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, VegetationIndexObservation.class);
  }

  @Override
  public VegetationIndexObservation createVegetationIndexObservation(final String id, @Valid final VegetationIndexObservation vegetationIndexObservation, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    vegetationIndexObservation.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.VEGETATION_INDEX_OBSERVATIONS, "/VEGETATION_INDEX_OBSERVATIONS/" + vegetationIndexObservation.getId(), Action.CREATE, authentication);
    vegetationIndexObservation.setCreatedAt(Instant.now());
    return createEntry(id, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_ID_SHORT, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, vegetationIndexObservation);
  }

  @Override
  public Optional<VegetationIndexObservation> getVegetationIndexObservation(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.VEGETATION_INDEX_OBSERVATIONS, "/VEGETATION_INDEX_OBSERVATIONS/" + observationId, Action.READ, authentication);
    return getEntry(id, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_ID_SHORT, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, VegetationIndexObservation.class, observationId);
  }

  @Override
  public void deleteVegetationIndexObservation(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.VEGETATION_INDEX_OBSERVATIONS, "/VEGETATION_INDEX_OBSERVATIONS/" + observationId, Action.DELETE, authentication);
    deleteEntry(id, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_ID_SHORT, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, observationId);
  }

  @Override
  public void setVegetationIndexObservationFile(final String id, final String observationId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.VEGETATION_INDEX_OBSERVATIONS, "/VEGETATION_INDEX_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    setEntryFile(id, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_ID_SHORT, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, VegetationIndexObservation.class, observationId, typedResource);
  }

  @Override
  public Optional<TypedResource> getVegetationIndexObservationFile(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.VEGETATION_INDEX_OBSERVATIONS, "/VEGETATION_INDEX_OBSERVATIONS/" + observationId + "/file", Action.READ, authentication);
    return getEntryFile(id, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_ID_SHORT, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, VegetationIndexObservation.class, observationId);
  }

  @Override
  public void deleteVegetationIndexObservationFile(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.VEGETATION_INDEX_OBSERVATIONS, "/VEGETATION_INDEX_OBSERVATIONS/" + observationId + "/file", Action.UPDATE, authentication);
    deleteEntryFile(id, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_ID_SHORT, VEGETATION_INDEX_OBSERVATIONS_SUBMODEL_SOIL_NUTRIENTS_OBSERVATIONS_SMEC_ID_SHORT, VegetationIndexObservation.class, observationId);
  }

////////////////////////////

  @Override
  public Optional<List<SoilMeasurementWorkOrder>> getSoilMeasurementWorkOrders(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_ORDERS, "/SOIL_MEASUREMENT_WORK_ORDERS", Action.READ, authentication);
    return getEntries(id, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_SOIL_MEASUREMENT_WORK_ORDERS_SMEC_ID_SHORT, SoilMeasurementWorkOrder.class);
  }

  @Override
  public SoilMeasurementWorkOrder createSoilMeasurementWorkOrder(final String id, @Valid final SoilMeasurementWorkOrder soilMeasurementWorkOrder, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    soilMeasurementWorkOrder.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_ORDERS, "/SOIL_MEASUREMENT_WORK_ORDERS/" + soilMeasurementWorkOrder.getId(), Action.CREATE, authentication);
    soilMeasurementWorkOrder.setCreatedAt(Instant.now());
    return createEntry(id, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_SOIL_MEASUREMENT_WORK_ORDERS_SMEC_ID_SHORT, soilMeasurementWorkOrder);
  }

  @Override
  public Optional<SoilMeasurementWorkOrder> getSoilMeasurementWorkOrder(final String id, final String workOrderId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_ORDERS, "/SOIL_MEASUREMENT_WORK_ORDERS/" + workOrderId, Action.READ, authentication);
    return getEntry(id, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_SOIL_MEASUREMENT_WORK_ORDERS_SMEC_ID_SHORT, SoilMeasurementWorkOrder.class, workOrderId);
  }

  @Override
  public Optional<SoilMeasurementWorkOrder> getSoilMeasurementWorkOrderWithoutAuthentication(final String id, final String workOrderId) {
    return getEntry(id, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_SOIL_MEASUREMENT_WORK_ORDERS_SMEC_ID_SHORT, SoilMeasurementWorkOrder.class, workOrderId);
  }

  @Override
  public void deleteSoilMeasurementWorkOrder(final String id, final String workOrderId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_ORDERS, "/SOIL_MEASUREMENT_WORK_ORDERS/" + workOrderId, Action.DELETE, authentication);
    deleteEntry(id, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_SOIL_MEASUREMENT_WORK_ORDERS_SMEC_ID_SHORT, workOrderId);
  }

  @Override
  public void setSoilMeasurementWorkOrderFile(final String id, final String workOrderId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_ORDERS, "/SOIL_MEASUREMENT_WORK_ORDERS/" + workOrderId + "/file", Action.UPDATE, authentication);
    setEntryFile(id, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_SOIL_MEASUREMENT_WORK_ORDERS_SMEC_ID_SHORT, SoilMeasurementWorkOrder.class, workOrderId, typedResource);
  }

  @Override
  public Optional<TypedResource> getSoilMeasurementWorkOrderFile(final String id, final String workOrderId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_ORDERS, "/SOIL_MEASUREMENT_WORK_ORDERS/" + workOrderId + "/file", Action.READ, authentication);
    return getEntryFile(id, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_SOIL_MEASUREMENT_WORK_ORDERS_SMEC_ID_SHORT, SoilMeasurementWorkOrder.class, workOrderId);
  }

  @Override
  public void deleteSoilMeasurementWorkOrderFile(final String id, final String workOrderId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_ORDERS, "/SOIL_MEASUREMENT_WORK_ORDERS/" + workOrderId + "/file", Action.UPDATE, authentication);
    deleteEntryFile(id, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_ORDERS_SUBMODEL_SOIL_MEASUREMENT_WORK_ORDERS_SMEC_ID_SHORT, SoilMeasurementWorkOrder.class, workOrderId);
  }

  ////////////////////////////


  @Override
  public Optional<List<SoilMeasurementWorkRecord>> getSoilMeasurementWorkRecords(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_RECORDS, "/SOIL_MEASUREMENT_WORK_RECORDS", Action.READ, authentication);
    return getEntries(id, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT, SoilMeasurementWorkRecord.class);
  }

  @Override
  public SoilMeasurementWorkRecord createSoilMeasurementWorkRecord(final String id, @Valid final SoilMeasurementWorkRecord soilMeasurementWorkRecord, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    soilMeasurementWorkRecord.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_RECORDS, "/SOIL_MEASUREMENT_WORK_RECORDS/" + soilMeasurementWorkRecord.getId(), Action.CREATE, authentication);
    soilMeasurementWorkRecord.setCreatedAt(Instant.now());
    return createEntry(id, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT, soilMeasurementWorkRecord);
  }

  @Override
  public SoilMeasurementWorkRecord createSoilMeasurementWorkRecordWithoutAuthentication(final String id, final SoilMeasurementWorkRecord soilMeasurementWorkRecord) throws DoesNotExistException {
    soilMeasurementWorkRecord.setId(generateRandomId());
    soilMeasurementWorkRecord.setCreatedAt(Instant.now());
    return createEntry(id, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT, soilMeasurementWorkRecord);
  }

  @Override
  public Optional<SoilMeasurementWorkRecord> getSoilMeasurementWorkRecord(final String id, final String workRecordId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_RECORDS, "/SOIL_MEASUREMENT_WORK_RECORDS/" + workRecordId, Action.READ, authentication);
    return getEntry(id, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT, SoilMeasurementWorkRecord.class, workRecordId);
  }

  @Override
  public void deleteSoilMeasurementWorkRecord(final String id, final String workRecordId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_RECORDS, "/SOIL_MEASUREMENT_WORK_RECORDS/" + workRecordId, Action.DELETE, authentication);
    deleteEntry(id, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT, workRecordId);
  }

  @Override
  public void setSoilMeasurementWorkRecordFile(final String id, final String workRecordId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_RECORDS, "/SOIL_MEASUREMENT_WORK_RECORDS/" + workRecordId + "/file", Action.UPDATE, authentication);
    setEntryFile(id, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT, SoilMeasurementWorkRecord.class, workRecordId, typedResource);
  }

  @Override
  public void setSoilMeasurementWorkRecordFileWithoutAuthentication(final String id, final String workRecordId, final TypedResource typedResource) throws DoesNotExistException {
    setEntryFile(id, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT, SoilMeasurementWorkRecord.class, workRecordId, typedResource);
  }

  @Override
  public Optional<TypedResource> getSoilMeasurementWorkRecordFile(final String id, final String workRecordId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_RECORDS, "/SOIL_MEASUREMENT_WORK_RECORDS/" + workRecordId + "/file", Action.READ, authentication);
    return getEntryFile(id, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT, SoilMeasurementWorkRecord.class, workRecordId);
  }

  @Override
  public void deleteSoilMeasurementWorkRecordFile(final String id, final String workRecordId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.SOIL_MEASUREMENT_WORK_RECORDS, "/SOIL_MEASUREMENT_WORK_RECORDS/" + workRecordId + "/file", Action.UPDATE, authentication);
    deleteEntryFile(id, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_ID_SHORT, SOIL_MEASUREMENT_WORK_RECORDS_SUBMODEL_SOIL_MEASUREMENT_WORK_RECORDS_SMEC_ID_SHORT, SoilMeasurementWorkRecord.class, workRecordId);
  }

  ////////////////////////////

  @Override
  public Optional<List<CropMaturityObservation>> getCropMaturityObservations(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.CROP_MATURITY_OBSERVATIONS, "/CROP_MATURITY_OBSERVATIONS", Action.READ, authentication);
    return getEntries(id, CROP_MATURITY_OBSERVATIONS_SUBMODEL_ID_SHORT, CROP_MATURITY_OBSERVATIONS_SUBMODEL_CROP_MATURITY_OBSERVATIONS_SMEC_ID_SHORT, CropMaturityObservation.class);
  }

  @Override
  public CropMaturityObservation createCropMaturityObservation(final String id, @Valid final CropMaturityObservation cropMaturityObservation, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    cropMaturityObservation.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.CROP_MATURITY_OBSERVATIONS, "/CROP_MATURITY_OBSERVATIONS/" + cropMaturityObservation.getId(), Action.CREATE, authentication);
    cropMaturityObservation.setCreatedAt(Instant.now());
    return createEntry(id, CROP_MATURITY_OBSERVATIONS_SUBMODEL_ID_SHORT, CROP_MATURITY_OBSERVATIONS_SUBMODEL_CROP_MATURITY_OBSERVATIONS_SMEC_ID_SHORT, cropMaturityObservation);
  }

  @Override
  public Optional<CropMaturityObservation> getCropMaturityObservation(final String id, final String observationId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.CROP_MATURITY_OBSERVATIONS, "/CROP_MATURITY_OBSERVATIONS/" + observationId, Action.READ, authentication);
    return getEntry(id, CROP_MATURITY_OBSERVATIONS_SUBMODEL_ID_SHORT, CROP_MATURITY_OBSERVATIONS_SUBMODEL_CROP_MATURITY_OBSERVATIONS_SMEC_ID_SHORT, CropMaturityObservation.class, observationId);
  }

  @Override
  public void deleteCropMaturityObservation(final String id, final String observationId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.CROP_MATURITY_OBSERVATIONS, "/CROP_MATURITY_OBSERVATIONS/" + observationId, Action.DELETE, authentication);
    deleteEntry(id, CROP_MATURITY_OBSERVATIONS_SUBMODEL_ID_SHORT, CROP_MATURITY_OBSERVATIONS_SUBMODEL_CROP_MATURITY_OBSERVATIONS_SMEC_ID_SHORT, observationId);
  }

  @Override
  public Optional<List<DataFile>> getDataFiles(final String id, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.DATA_FILES, "/DATA_FILES", Action.READ, authentication);
    return getEntries(id, DATA_FILES_SUBMODEL_ID_SHORT, DATA_FILES_SUBMODEL_DATA_FILES_SMEC_ID_SHORT, DataFile.class);
  }

  @Override
  public DataFile createDataFile(final String id, @Valid final DataFile dataFile, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    dataFile.setId(generateRandomId());
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.DATA_FILES, "/DATA_FILES/" + dataFile.getId(), Action.CREATE, authentication);
    dataFile.setCreatedAt(Instant.now());
    return createEntry(id, DATA_FILES_SUBMODEL_ID_SHORT, DATA_FILES_SUBMODEL_DATA_FILES_SMEC_ID_SHORT, dataFile);
  }

  @Override
  public Optional<DataFile> getDataFile(final String id, final String dataFileId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.DATA_FILES, "/DATA_FILES/" + dataFileId, Action.READ, authentication);
    return getEntry(id, DATA_FILES_SUBMODEL_ID_SHORT, DATA_FILES_SUBMODEL_DATA_FILES_SMEC_ID_SHORT, DataFile.class, dataFileId);
  }

  @Override
  public void deleteDataFile(final String id, final String dataFileId, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.DATA_FILES, "/DATA_FILES/" + dataFileId, Action.DELETE, authentication);
    deleteEntry(id, DATA_FILES_SUBMODEL_ID_SHORT, DATA_FILES_SUBMODEL_DATA_FILES_SMEC_ID_SHORT, dataFileId);
  }

  @Override
  public void setDataFileFile(final String id, final String dataFileId, @Valid final TypedResource typedResource, final Authentication authentication) throws DoesNotExistException, ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.DATA_FILES, "/DATA_FILES/" + dataFileId + "/file", Action.UPDATE, authentication);
    setEntryFile(id, DATA_FILES_SUBMODEL_ID_SHORT, DATA_FILES_SUBMODEL_DATA_FILES_SMEC_ID_SHORT, DataFile.class, dataFileId, typedResource);
  }

  @Override
  public Optional<TypedResource> getDataFileFile(final String id, final String dataFileId, final Authentication authentication) throws ForbiddenException {
    this.twinHubEnforcementPoint.enforceTwinResourceAccess(id, TwinResource.DATA_FILES, "/DATA_FILES/" + dataFileId + "/file", Action.READ, authentication);
    return getEntryFile(id, DATA_FILES_SUBMODEL_ID_SHORT, DATA_FILES_SUBMODEL_DATA_FILES_SMEC_ID_SHORT, DataFile.class, dataFileId);
  }

  private <T> Optional<List<T>> getEntries(final String id, final String submodelIdShort, final String submodelSmecIdShort, Class<T> entryClass) {
    final Optional<AASDescriptor> aasDescriptorOptional = this.baSyxAccessService.doLookupAASById(BaSyxIdUtil.fromString(id));
    if (aasDescriptorOptional.isEmpty()) {
      return Optional.empty();
    } else {
      final AASDescriptor aasDescriptor = aasDescriptorOptional.get();
      return Optional.of(
          this.baSyxAccessService.doGetSubmodel(aasDescriptor.getIdentifier(), submodelIdShort)
              .map(submodel -> submodel.getSubmodelElements().get(submodelSmecIdShort))
              .map(ISubmodelElement::getLocalCopy)
              .map(ISubmodelElementCollection.class::cast)
              .map(ISubmodelElementCollection::getSubmodelElements)
              .map(Map::values)
              .map(
                  submodelElements -> submodelElements.stream()
                      .map(IProperty.class::cast)
                      .map(ISubmodelElement::getValue)
                      .map(String.class::cast)
                      .map(str -> {
                        try {
                          return this.objectMapper.readValue(str, entryClass);
                        } catch (JsonProcessingException e) {
                          throw new RuntimeException(e);
                        }
                      })
                      .collect(Collectors.toList())
              ).orElseGet(Collections::emptyList)
      );
    }
  }

  private <T extends IdHolder> T createEntry(final String id, final String submodelIdShort, final String submodelSmecIdShort, final T entry) throws DoesNotExistException {
    final IIdentifier aasId = BaSyxIdUtil.fromString(id);

    final Optional<ISubmodel> submodelOptional = this.baSyxAccessService.doGetSubmodel(aasId, submodelIdShort);
    if (submodelOptional.isEmpty()) {
      final Submodel submodel = new Submodel(submodelIdShort, BaSyxIdUtil.constructSubmodelInstanceIdentifier(submodelIdShort, ELEMENT_ID, BaSyxIdUtil.extractElementInstance(aasId)));
      final SubmodelElementCollection submodelElementCollection = new SubmodelElementCollection(submodelSmecIdShort);
      submodel.addSubmodelElement(submodelElementCollection);
      this.baSyxAccessService.doCreateSubmodel(aasId, submodel);
    } else {
      final ISubmodel submodel = submodelOptional.get();
      if (!submodel.getSubmodelElements().containsKey(submodelSmecIdShort)) {
        final SubmodelElementCollection submodelElementCollection = new SubmodelElementCollection(submodelSmecIdShort);
        submodel.addSubmodelElement(submodelElementCollection);
      }
    }
    try {
      final Property entryProperty = new Property(entry.getId(), this.objectMapper.writeValueAsString(entry));
      final ISubmodelElementCollection submodelElementCollection = this.baSyxAccessService.doReadSubmodelElementFromSubmodel(aasId, submodelIdShort, submodelSmecIdShort, ISubmodelElementCollection.class).orElseThrow();
      submodelElementCollection.addSubmodelElement(entryProperty);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
    return entry;
  }

  private <T> Optional<T> getEntry(final String id, final String submodelIdShort, final String submodelSmecIdShort, Class<T> entryClass, final String entryId) {
    return this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), submodelIdShort)
        .map(submodel -> submodel.getSubmodelElements().get(submodelSmecIdShort))
        .map(ISubmodelElement::getLocalCopy)
        .map(ISubmodelElementCollection.class::cast)
        .map(ISubmodelElementCollection::getSubmodelElements)
        .map(workRecordEntries -> workRecordEntries.get(entryId))
        .map(IProperty.class::cast)
        .map(ISubmodelElement::getValue)
        .map(String.class::cast)
        .map(str -> {
          try {
            return this.objectMapper.readValue(str, entryClass);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private void deleteEntry(final String id, final String submodelIdShort, final String submodelSmecIdShort, final String entryId) throws DoesNotExistException {
    final ISubmodelElementCollection entriesSubmodelElementCollection = this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), submodelIdShort)
        .map(submodel -> submodel.getSubmodelElements().get(submodelSmecIdShort))
        .map(ISubmodelElementCollection.class::cast).orElseThrow(DoesNotExistException::new);

    if (entriesSubmodelElementCollection.getSubmodelElements().containsKey(entryId)) {
      entriesSubmodelElementCollection.deleteSubmodelElement(entryId);

      final String filePath = String.format("%s/%s/%s", submodelIdShort, submodelSmecIdShort, entryId);
      final String path = constructPathForFieldRelatedFile(id, filePath);
      try {
        this.fileStorageService.deleteDirectory(path);
      } catch (FileStorageException e) {
        logger.warn("Unable to delete files for entry (id={}, submodelIdShort={}, submodelSmecIdShort={}, entryId={})", id, submodelIdShort, submodelSmecIdShort, entryId, e);
      } catch (DoesNotExistException e) {
        // okay
      }
    } else {
      throw new DoesNotExistException();
    }
  }

  private <T extends FileHolder> void setEntryFile(final String id, final String submodelIdShort, final String submodelSmecIdShort, Class<T> entryClass, final String entryId, final TypedResource typedResource) throws DoesNotExistException {
    final Optional<IProperty> entryPropertyOptional = this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), submodelIdShort)
        .map(submodel -> submodel.getSubmodelElements().get(submodelSmecIdShort))
        .map(ISubmodelElementCollection.class::cast)
        .map(ISubmodelElementCollection::getSubmodelElements)
        .map(entries -> entries.get(entryId))
        .map(IProperty.class::cast);

    if (entryPropertyOptional.isEmpty()) {
      throw new DoesNotExistException(); // entry does not exist
    } else {
      final IProperty entryProperty = entryPropertyOptional.get();
      final T entry;
      try {
        entry = this.objectMapper.readValue((String) entryProperty.getValue(), entryClass);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }

      // store new file
      final Resource resource = typedResource.getResource();
      final String mediaType = typedResource.getMediaType();
      final String newFilePath = String.format("%s/%s/%s/%s/%s", submodelIdShort, submodelSmecIdShort, entryId, "files", encodePathSegment(Optional.ofNullable(resource.getFilename()).orElse("data")));
      final String newPath = constructPathForFieldRelatedFile(id, newFilePath);
      try {
        this.fileStorageService.storeFile(newPath, resource);
      } catch (FileStorageException e) {
        throw new RuntimeException(e);
      }

      // clean up old file if necessary
      if (entry.getFile() != null) {
        final String oldFilePath = entry.getFile().getPath();
        final String oldPath = constructPathForFieldRelatedFile(id, oldFilePath);
        if (!oldPath.equals(newPath)) {
          // delete old file
          try {
            this.fileStorageService.deleteFile(oldPath);
          } catch (FileStorageException e) {
            logger.warn("unable to delete old file " + oldPath, e);
          } catch (DoesNotExistException e) {
            // okay, already gone
          }
        }
      }

      // take note of file reference
      final de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.File newFile = new de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.File(newFilePath, mediaType);
      entry.setFile(newFile);
      try {
        entryProperty.setValue(this.objectMapper.writeValueAsString(entry));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private <T extends FileHolder> Optional<TypedResource> getEntryFile(final String id, final String submodelIdShort, final String submodelSmecIdShort, Class<T> entryClass, final String entryId) {
    final Optional<de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.File> fileOptional = this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), submodelIdShort)
        .map(submodel -> submodel.getSubmodelElements().get(submodelSmecIdShort))
        .map(ISubmodelElement::getLocalCopy)
        .map(ISubmodelElementCollection.class::cast)
        .map(ISubmodelElementCollection::getSubmodelElements)
        .map(entries -> entries.get(entryId))
        .map(IProperty.class::cast)
        .map(ISubmodelElement::getValue)
        .map(String.class::cast)
        .map(str -> {
          try {
            return this.objectMapper.readValue(str, entryClass);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        })
        .map(FileHolder::getFile);

    if (fileOptional.isEmpty()) {
      return Optional.empty(); // there is no file reference
    } else {
      final de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.File file = fileOptional.get();
      final String filePath = file.getPath();
      final String path = constructPathForFieldRelatedFile(id, filePath);
      try {
        final Optional<Resource> resourceOptional = this.fileStorageService.loadFile(path);
        if (resourceOptional.isEmpty()) {
          // there is a file reference but the file is missing from storage
          logger.warn("File missing: {}", path);
          throw new RuntimeException("file missing");
        } else {
          return resourceOptional
              .map(resource -> TypedResource.of(resource, file.getMimeType()));
        }
      } catch (FileStorageException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private <T extends FileHolder> void deleteEntryFile(final String id, final String submodelIdShort, final String submodelSmecIdShort, Class<T> entryClass, final String entryId) throws DoesNotExistException {
    final Optional<IProperty> entryPropertyOptional = this.baSyxAccessService.doGetSubmodel(BaSyxIdUtil.fromString(id), submodelIdShort)
        .map(submodel -> submodel.getSubmodelElements().get(submodelSmecIdShort))
        .map(ISubmodelElementCollection.class::cast)
        .map(ISubmodelElementCollection::getSubmodelElements)
        .map(entries -> entries.get(entryId))
        .map(IProperty.class::cast);

    if (entryPropertyOptional.isEmpty()) {
      throw new DoesNotExistException(); // entry does not exist
    } else {
      final IProperty entryProperty = entryPropertyOptional.get();
      final T entry;
      try {
        entry = this.objectMapper.readValue((String) entryProperty.getValue(), entryClass);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      if (entry.getFile() == null) {
        throw new DoesNotExistException(); // there is no file reference
      } else {
        final de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.File file = entry.getFile();
        final String filePath = file.getPath();
        final String path = constructPathForFieldRelatedFile(id, filePath);
        try {
          this.fileStorageService.deleteFile(path);
        } catch (FileStorageException e) {
          throw new RuntimeException(e);
        } catch (DoesNotExistException e) {
          // okay, already gone
        }
        entry.setFile(null);
        try {
          entryProperty.setValue(this.objectMapper.writeValueAsString(entry));
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private WorkRecord extractWorkRecordFromSubmodelElementCollection(final ISubmodelElementCollection workRecordEntry) {
    final WorkRecord workRecord = new WorkRecord();
    final Map<String, IProperty> properties = workRecordEntry.getProperties();
    workRecord.setId(workRecordEntry.getIdShort());
    workRecord.setCreatedAt(
        Optional.ofNullable(properties.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_CREATED_AT_PROPERTY_ID_SHORT))
            .map(ISubmodelElement::getValue)
            .map(String.class::cast)
            .map(text -> {
              try {
                return Instant.parse(text);
              } catch (DateTimeParseException e) {
                return null;
              }
            })
            .orElse(null)
    );
    workRecord.setCreatedBy(
        Optional.ofNullable(properties.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_CREATED_BY_PROPERTY_ID_SHORT))
            .map(ISubmodelElement::getValue)
            .map(String.class::cast)
            .orElse(null)
    );
    workRecord.setCropSeason(
        Optional.ofNullable(properties.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_CROP_SEASON_PROPERTY_ID_SHORT))
            .map(ISubmodelElement::getValue)
            .map(String.class::cast)
            .orElse(null)
    );
    workRecord.setDescription(
        Optional.ofNullable(properties.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_DESCRIPTION_PROPERTY_ID_SHORT))
            .map(ISubmodelElement::getValue)
            .map(String.class::cast)
            .orElse(null)
    );
    workRecord.setDetails(
        Optional.ofNullable(properties.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_DETAILS_PROPERTY_ID_SHORT))
            .map(ISubmodelElement::getValue)
            .map(String.class::cast) // ODO json decode
            .map(string -> {
              try {
                return objectMapper.readTree(string);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            })
            .orElse(null)
    );
    workRecord.setEndTime(
        Optional.ofNullable(properties.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_END_TIME_PROPERTY_ID_SHORT))
            .map(ISubmodelElement::getValue)
            .map(String.class::cast)
            .map(text -> {
              try {
                return Instant.parse(text);
              } catch (DateTimeParseException e) {
                return null;
              }
            })
            .orElse(null)
    );
    workRecord.setStartTime(
        Optional.ofNullable(properties.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_START_TIME_PROPERTY_ID_SHORT))
            .map(ISubmodelElement::getValue)
            .map(String.class::cast)
            .map(text -> {
              try {
                return Instant.parse(text);
              } catch (DateTimeParseException e) {
                return null;
              }
            })
            .orElse(null)
    );
    workRecord.setOperationType(
        Optional.ofNullable(properties.get(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_OPERATION_TYPE_PROPERTY_ID_SHORT))
            .map(ISubmodelElement::getValue)
            .map(String.class::cast)
            .map(OperationType::valueOf)
            .orElse(null)
    );
    return workRecord;
  }

  private SubmodelElementCollection generateSubmodelElementCollectionFromWorkRecord(final WorkRecord workRecord) {
    final SubmodelElementCollection workRecordEntry = new SubmodelElementCollection();
    workRecordEntry.setIdShort(workRecord.getId());
    if (workRecord.getCreatedAt() != null) {
      workRecordEntry.addSubmodelElement(new Property(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_CREATED_AT_PROPERTY_ID_SHORT, workRecord.getCreatedAt().toString()));
    }
    if (workRecord.getCreatedBy() != null) {
      workRecordEntry.addSubmodelElement(new Property(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_CREATED_BY_PROPERTY_ID_SHORT, workRecord.getCreatedBy()));
    }
    if (workRecord.getCropSeason() != null) {
      workRecordEntry.addSubmodelElement(new Property(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_CROP_SEASON_PROPERTY_ID_SHORT, workRecord.getCropSeason()));
    }
    if (workRecord.getDescription() != null) {
      workRecordEntry.addSubmodelElement(new Property(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_DESCRIPTION_PROPERTY_ID_SHORT, workRecord.getDescription()));
    }
    if (workRecord.getDetails() != null) {
      try {
        workRecordEntry.addSubmodelElement(new Property(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_DETAILS_PROPERTY_ID_SHORT, objectMapper.writeValueAsString(workRecord.getDetails())));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
    if (workRecord.getEndTime() != null) {
      workRecordEntry.addSubmodelElement(new Property(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_END_TIME_PROPERTY_ID_SHORT, workRecord.getEndTime().toString()));
    }
    if (workRecord.getStartTime() != null) {
      workRecordEntry.addSubmodelElement(new Property(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_START_TIME_PROPERTY_ID_SHORT, workRecord.getStartTime().toString()));
    }
    if (workRecord.getOperationType() != null) {
      workRecordEntry.addSubmodelElement(new Property(WORK_RECORDS_SUBMODEL_WORK_RECORDS_ENTRY_OPERATION_TYPE_PROPERTY_ID_SHORT, workRecord.getOperationType().name()));
    }

    return workRecordEntry;
  }

  private String constructPathForFieldRelatedFile(final String id, @Nullable final String relativePath) {
    if (relativePath == null) {
      return String.format("%s/%s/%s", "twin-hub", "fields", encodePathSegment(id));
    } else {
      return String.format("%s/%s/%s/%s", "twin-hub", "fields", encodePathSegment(id), relativePath);
    }
  }

  private String encodePathSegment(final String pathSegment) {
    final String replacePattern = "[^a-zA-Z0-9_\\-.]";
    return pathSegment.replaceAll(replacePattern, "_");
  }

}
