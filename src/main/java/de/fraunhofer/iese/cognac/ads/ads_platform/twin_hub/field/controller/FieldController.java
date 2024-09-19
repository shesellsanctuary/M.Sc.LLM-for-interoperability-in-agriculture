package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.controller;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.InvalidInputException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.JwtUtil;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.SecurityFilters;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.FieldDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.OperationType;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.PlantObservationDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.SoilMeasurementWorkOrderDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.SoilMeasurementWorkRecordDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.dto.WorkRecordDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.mapper.FieldMapper;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.TypedResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.service.FieldService;

import org.apache.commons.lang3.StringUtils;
import org.geojson.Feature;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;


@RestController
@CrossOrigin
@RequestMapping("/api/v1/twin-hub/fields")
public class FieldController {
  // TODO add additional security checks
  // private static final Logger logger =
  // LoggerFactory.getLogger(FieldController.class);
  private final FieldService fieldService;
  private final FieldMapper fieldMapper;

  public FieldController(final FieldService fieldService, final FieldMapper fieldMapper) {
    this.fieldService = fieldService;
    this.fieldMapper = fieldMapper;
  }

  @GetMapping
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<List<FieldDto>> getFields(JwtAuthenticationToken jwtAuthenticationToken) {
    return ResponseEntity.ok(
        this.fieldService.getFields(JwtUtil.mapAuthentication(jwtAuthenticationToken)).stream()
            .map(this.fieldMapper::mapEntityToDto)
            .collect(Collectors.toList()));
  }

  @PostMapping
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_MANAGE)
  public ResponseEntity<FieldDto> createField(@RequestBody @Valid final FieldDto fieldDto,
      Authentication authentication) {
    try {
      final FieldDto respBody = this.fieldMapper.mapEntityToDto(
          this.fieldService.createField(
              this.fieldMapper.mapDtoToEntity(fieldDto),
              JwtUtil.mapAuthentication(authentication)));
      final URI location = ServletUriComponentsBuilder
          .fromCurrentRequest()
          .path("/{id}")
          .buildAndExpand(respBody.getId())
          .toUri();
      return ResponseEntity.created(location).body(respBody);
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping("{id}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<FieldDto> getField(
      @PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      return ResponseEntity.of(
          this.fieldService.getField(id, JwtUtil.mapAuthentication(authentication))
              .map(this.fieldMapper::mapEntityToDto));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PatchMapping(value = "{id}", consumes = "application/merge-patch+json")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_MANAGE)
  public ResponseEntity<Void> patchField(
      @PathVariable(name = "id") final String id,
      @RequestBody @Valid final FieldDto body,
      Authentication authentication) {
    try {
      // TODO consider replacing with updateField method that takes care of updating
      // what is mutable
      if (null != body.getName()) {
        this.fieldService.setName(id, body.getName(), JwtUtil.mapAuthentication(authentication));
      }
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (InvalidInputException e) {
      return ResponseEntity.badRequest().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @DeleteMapping("{id}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_MANAGE)
  public ResponseEntity<Void> deleteField(@PathVariable(name = "id") final String id, Authentication authentication) {
    try {
      this.fieldService.deleteField(id, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

  }

  @GetMapping("{id}/geometries")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<Map<String, Feature>> getGeometries(@PathVariable(name = "id") final String id) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("{id}/geometries/arable-area")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<Feature> getArableArea(@PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      return ResponseEntity.of(this.fieldService.getArableArea(id, JwtUtil.mapAuthentication(authentication)));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping("{id}/geometries/arable-area")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Feature> updateArableArea(
      @PathVariable(name = "id") final String id,
      @RequestBody final Feature feature,
      Authentication authentication) {
    try {
      this.fieldService.setArableArea(id, feature, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.ok(feature);
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping("{id}/geometries/arable-area/unkown-format")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<String> updateArableAreaUnknownFormat(
      @PathVariable(name = "id") final String id,
      @RequestBody final Object body,
      Authentication authentication) {

    try {
      String pythonPath = "llm-module/convertJsonBoundariesToGeoJson.py";
      String finalOutput;
      UUID uuid = UUID.randomUUID();

      ProcessBuilder pb = new ProcessBuilder("python", pythonPath, body.toString(), uuid.toString());
      Process process = pb.start();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        finalOutput = reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
      
      System.out.println("Final Output:" + finalOutput);
      this.fieldService.setArableArea(id, finalOutput,
          JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.ok(finalOutput);
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("{id}/geometries/arable-area")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deleteArableArea(@PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      this.fieldService.deleteArableArea(id, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping("{id}/geometries/trafficable-area")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<Feature> getTrafficableArea(@PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      return ResponseEntity.of(this.fieldService.getTrafficableArea(id, JwtUtil.mapAuthentication(authentication)));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping("{id}/geometries/trafficable-area")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Feature> updateTrafficableArea(
      @PathVariable(name = "id") final String id,
      @RequestBody final Feature feature,
      Authentication authentication) {
    try {
      this.fieldService.setTrafficableArea(id, feature, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.ok(feature);
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @DeleteMapping("{id}/geometries/trafficable-area")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deleteTrafficableArea(@PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      this.fieldService.deleteTrafficableArea(id, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping("{id}/geometries/non-trafficable-area")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<Feature> getNonTrafficableArea(@PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      return ResponseEntity.of(this.fieldService.getNonTrafficableArea(id, JwtUtil.mapAuthentication(authentication)));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping("{id}/geometries/non-trafficable-area")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Feature> updateNonTrafficableArea(
      @PathVariable(name = "id") final String id,
      @RequestBody final Feature feature,
      Authentication authentication) {
    try {
      this.fieldService.setNonTrafficableArea(id, feature, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.ok(feature);
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @DeleteMapping("{id}/geometries/non-trafficable-area")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deleteNonTrafficableArea(@PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      this.fieldService.deleteNonTrafficableArea(id, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping("{id}/geometries/tracks")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<Feature> getTracks(@PathVariable(name = "id") final String id, Authentication authentication) {
    try {
      return ResponseEntity.of(this.fieldService.getTracks(id, JwtUtil.mapAuthentication(authentication)));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping("{id}/geometries/tracks")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Feature> updateTracks(
      @PathVariable(name = "id") final String id,
      @RequestBody final Feature feature,
      Authentication authentication) {
    try {
      this.fieldService.setTracks(id, feature, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.ok(feature);
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @DeleteMapping("{id}/geometries/tracks")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deleteTracks(@PathVariable(name = "id") final String id, Authentication authentication) {
    try {
      this.fieldService.deleteTracks(id, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping("{id}/work-records")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<List<WorkRecordDto>> getWorkRecords(
      @PathVariable(name = "id") final String id,
      @RequestParam(name = "operation-type", required = false) final Optional<OperationType> operationType,
      @RequestParam(name = "crop-season", required = false) final Optional<String> cropSeason,
      Authentication authentication) {
    try {
      return ResponseEntity.of(
          this.fieldService.getWorkRecords(
              id,
              operationType.map(this.fieldMapper::mapDtoToEntity).orElse(null),
              cropSeason.filter(StringUtils::isNotBlank).orElse(null),
              JwtUtil.mapAuthentication(authentication))
              .map(
                  workRecords -> workRecords.stream()
                      .map(this.fieldMapper::mapEntityToDto)
                      .collect(Collectors.toList())));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PostMapping("{id}/work-records")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<WorkRecordDto> createWorkRecord(
      @PathVariable(name = "id") final String id,
      @RequestBody @Valid final WorkRecordDto body,
      Authentication authentication) {
    try {
      final WorkRecordDto respBody = this.fieldMapper.mapEntityToDto(
          this.fieldService.createWorkRecord(id, this.fieldMapper.mapDtoToEntity(body),
              JwtUtil.mapAuthentication(authentication)));
      final URI location = ServletUriComponentsBuilder
          .fromCurrentRequest()
          .path("/{id}")
          .buildAndExpand(respBody.getId())
          .toUri();
      return ResponseEntity.created(location).body(respBody);
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping("{id}/work-records/{workRecordId}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<WorkRecordDto> getWorkRecord(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workRecordId") final String workRecordId,
      Authentication authentication) {
    try {
      return ResponseEntity.of(
          this.fieldService.getWorkRecord(id, workRecordId, JwtUtil.mapAuthentication(authentication))
              .map(this.fieldMapper::mapEntityToDto));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @DeleteMapping("{id}/work-records/{workRecordId}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deleteWorkRecord(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workRecordId") final String workRecordId,
      Authentication authentication) {
    try {
      this.fieldService.deleteWorkRecord(id, workRecordId, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping(value = "{id}/work-records/{workRecordId}/shapefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> setWorkRecordShapefile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workRecordId") final String workRecordId,
      @RequestPart(name = "file") MultipartFile file,
      Authentication authentication) {
    try {
      this.fieldService.setWorkRecordShapefile(
          id,
          workRecordId,
          TypedResource.of(file.getResource(),
              Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE)),
          JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping(value = "{id}/work-records/{workRecordId}/shapefile")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<Resource> getWorkRecordShapefile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workRecordId") final String workRecordId,
      Authentication authentication) {
    final Optional<TypedResource> resourceOptional;
    try {
      resourceOptional = this.fieldService.getWorkRecordShapefile(
          id,
          workRecordId,
          JwtUtil.mapAuthentication(authentication));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return getResponseEntityForTypedResourceOptional(resourceOptional);
  }

  @DeleteMapping(value = "{id}/work-records/{workRecordId}/shapefile")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deleteWorkRecordShapefile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workRecordId") final String workRecordId,
      Authentication authentication) {
    try {
      this.fieldService.deleteWorkRecordShapefile(
          id,
          workRecordId,
          JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  ////////////////////////////

  @GetMapping("{id}/soil-measurement-work-orders")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<List<SoilMeasurementWorkOrderDto>> getSoilMeasurementWorkOrders(
      @PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      return ResponseEntity.of(
          this.fieldService.getSoilMeasurementWorkOrders(
              id,
              JwtUtil.mapAuthentication(authentication))
              .map(
                  observations -> observations.stream()
                      .map(this.fieldMapper::mapEntityToDto)
                      .collect(Collectors.toList())));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PostMapping("{id}/soil-measurement-work-orders")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<SoilMeasurementWorkOrderDto> createSoilMeasurementWorkOrder(
      @PathVariable(name = "id") final String id,
      @RequestBody @Valid final SoilMeasurementWorkOrderDto body,
      Authentication authentication) {
    try {
      final SoilMeasurementWorkOrderDto respBody = this.fieldMapper.mapEntityToDto(
          this.fieldService.createSoilMeasurementWorkOrder(id, this.fieldMapper.mapDtoToEntity(body),
              JwtUtil.mapAuthentication(authentication)));
      final URI location = ServletUriComponentsBuilder
          .fromCurrentRequest()
          .path("/{id}")
          .buildAndExpand(respBody.getId())
          .toUri();
      return ResponseEntity.created(location).body(respBody);
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping("{id}/soil-measurement-work-orders/{workOrderId}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<SoilMeasurementWorkOrderDto> getSoilMeasurementWorkOrder(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workOrderId") final String workOrderId,
      Authentication authentication) {
    try {
      return ResponseEntity.of(
          this.fieldService.getSoilMeasurementWorkOrder(id, workOrderId, JwtUtil.mapAuthentication(authentication))
              .map(this.fieldMapper::mapEntityToDto));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @DeleteMapping("{id}/soil-measurement-work-orders/{workOrderId}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deleteSoilMeasurementWorkOrder(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workOrderId") final String workOrderId,
      Authentication authentication) {
    try {
      this.fieldService.deleteSoilMeasurementWorkOrder(id, workOrderId, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping(value = "{id}/soil-measurement-work-orders/{workOrderId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> setSoilMeasurementWorkOrderFile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workOrderId") final String workOrderId,
      @RequestPart(name = "file") MultipartFile file,
      Authentication authentication) {
    try {
      this.fieldService.setSoilMeasurementWorkOrderFile(
          id,
          workOrderId,
          TypedResource.of(file.getResource(),
              Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE)),
          JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping(value = "{id}/soil-measurement-work-orders/{workOrderId}/file")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<Resource> getSoilMeasurementWorkOrderFile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workOrderId") final String workOrderId,
      Authentication authentication) {
    final Optional<TypedResource> resourceOptional;
    try {
      resourceOptional = this.fieldService.getSoilMeasurementWorkOrderFile(
          id,
          workOrderId,
          JwtUtil.mapAuthentication(authentication));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return getResponseEntityForTypedResourceOptional(resourceOptional);
  }
  ///////////////////////////

  @GetMapping("{id}/soil-measurement-work-records")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<List<SoilMeasurementWorkRecordDto>> getSoilMeasurementWorkRecords(
      @PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      return ResponseEntity.of(
          this.fieldService.getSoilMeasurementWorkRecords(
              id,
              JwtUtil.mapAuthentication(authentication))
              .map(
                  observations -> observations.stream()
                      .map(this.fieldMapper::mapEntityToDto)
                      .collect(Collectors.toList())));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PostMapping("{id}/soil-measurement-work-records")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<SoilMeasurementWorkRecordDto> createSoilMeasurementWorkRecord(
      @PathVariable(name = "id") final String id,
      @RequestBody @Valid final SoilMeasurementWorkRecordDto body,
      Authentication authentication) {
    try {
      final SoilMeasurementWorkRecordDto respBody = this.fieldMapper.mapEntityToDto(
          this.fieldService.createSoilMeasurementWorkRecord(id, this.fieldMapper.mapDtoToEntity(body),
              JwtUtil.mapAuthentication(authentication)));
      final URI location = ServletUriComponentsBuilder
          .fromCurrentRequest()
          .path("/{id}")
          .buildAndExpand(respBody.getId())
          .toUri();
      return ResponseEntity.created(location).body(respBody);
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping("{id}/soil-measurement-work-records/{workRecordId}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<SoilMeasurementWorkRecordDto> getSoilMeasurementWorkRecord(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workRecordId") final String workRecordId,
      Authentication authentication) {
    try {
      return ResponseEntity.of(
          this.fieldService.getSoilMeasurementWorkRecord(id, workRecordId, JwtUtil.mapAuthentication(authentication))
              .map(this.fieldMapper::mapEntityToDto));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @DeleteMapping("{id}/soil-measurement-work-records/{workRecordId}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deleteSoilMeasurementWorkRecord(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workRecordId") final String workRecordId,
      Authentication authentication) {
    try {
      this.fieldService.deleteSoilMeasurementWorkRecord(id, workRecordId, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping(value = "{id}/soil-measurement-work-records/{workRecordId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> setSoilMeasurementWorkRecordFile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workRecordId") final String workRecordId,
      @RequestPart(name = "file") MultipartFile file,
      Authentication authentication) {
    try {
      this.fieldService.setSoilMeasurementWorkRecordFile(
          id,
          workRecordId,
          TypedResource.of(file.getResource(),
              Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE)),
          JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping(value = "{id}/soil-measurement-work-records/{workRecordId}/file")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<Resource> getSoilMeasurementWorkRecordFile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "workRecordId") final String workRecordId,
      Authentication authentication) {
    final Optional<TypedResource> resourceOptional;
    try {
      resourceOptional = this.fieldService.getSoilMeasurementWorkRecordFile(
          id,
          workRecordId,
          JwtUtil.mapAuthentication(authentication));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return getResponseEntityForTypedResourceOptional(resourceOptional);
  }
  ///////////////////////////

  @GetMapping("{id}/plant-observations")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<List<PlantObservationDto>> getPlantObservations(
      @PathVariable(name = "id") final String id,
      Authentication authentication) {
    try {
      return ResponseEntity.of(
          this.fieldService.getPlantObservations(
              id,
              JwtUtil.mapAuthentication(authentication))
              .map(
                  observations -> observations.stream()
                      .map(this.fieldMapper::mapEntityToDto)
                      .collect(Collectors.toList())));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PostMapping("{id}/plant-observations")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<PlantObservationDto> createPlantObservation(
      @PathVariable(name = "id") final String id,
      @RequestBody @Valid final PlantObservationDto body,
      Authentication authentication) {
    try {
      final PlantObservationDto respBody = this.fieldMapper.mapEntityToDto(
          this.fieldService.createPlantObservation(id, this.fieldMapper.mapDtoToEntity(body),
              JwtUtil.mapAuthentication(authentication)));
      final URI location = ServletUriComponentsBuilder
          .fromCurrentRequest()
          .path("/{id}")
          .buildAndExpand(respBody.getId())
          .toUri();
      return ResponseEntity.created(location).body(respBody);
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping("{id}/plant-observations/{observationId}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<PlantObservationDto> getPlantObservation(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "observationId") final String observationId,
      Authentication authentication) {
    try {
      return ResponseEntity.of(
          this.fieldService.getPlantObservation(id, observationId, JwtUtil.mapAuthentication(authentication))
              .map(this.fieldMapper::mapEntityToDto));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @DeleteMapping("{id}/plant-observations/{observationId}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deletePlantObservation(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "observationId") final String observationId,
      Authentication authentication) {
    try {
      this.fieldService.deletePlantObservation(id, observationId, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping(value = "{id}/plant-observations/{observationId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> setPlantObservationFile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "observationId") final String observationId,
      @RequestPart(name = "file") MultipartFile file,
      Authentication authentication) {
    try {
      this.fieldService.setPlantObservationFile(
          id,
          observationId,
          TypedResource.of(file.getResource(),
              Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE)),
          JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @GetMapping(value = "{id}/plant-observations/{observationId}/file")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_READ)
  public ResponseEntity<Resource> getPlantObservationFile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "observationId") final String observationId,
      Authentication authentication) {
    final Optional<TypedResource> resourceOptional;
    try {
      resourceOptional = this.fieldService.getPlantObservationFile(
          id,
          observationId,
          JwtUtil.mapAuthentication(authentication));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return getResponseEntityForTypedResourceOptional(resourceOptional);
  }

  @DeleteMapping(value = "{id}/plant-observations/{observationId}/file")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_TWINS_WRITE)
  public ResponseEntity<Void> deletePlantObservationFile(
      @PathVariable(name = "id") final String id,
      @PathVariable(name = "observationId") final String observationId,
      Authentication authentication) {
    try {
      this.fieldService.deletePlantObservationFile(
          id,
          observationId,
          JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  private ResponseEntity<Resource> getResponseEntityForTypedResourceOptional(
      final Optional<TypedResource> resourceOptional) {
    if (resourceOptional.isPresent()) {
      final TypedResource typedResource = resourceOptional.get();
      final Resource resource = typedResource.getResource();
      final String filename = Optional.ofNullable(resource.getFilename()).filter(StringUtils::isNotBlank)
          .orElse("data");
      return ResponseEntity.status(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
          .body(resource);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
