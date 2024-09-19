package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.controller;

import de.fraunhofer.iese.cognac.ads.ads_platform.security.JwtUtil;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.SecurityFilters;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.AccessLogEntryDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper.AccessLogMapper;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service.AccessLogService;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/twin-hub/access-logs")
public class AccessLogController {

  private final AccessLogService accessLogService;
  private final AccessLogMapper accessLogMapper;

  @Autowired
  public AccessLogController(
      final AccessLogService accessLogService, final AccessLogMapper accessLogMapper) {
    this.accessLogService = accessLogService;
    this.accessLogMapper = accessLogMapper;
  }

  @GetMapping
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_DATA_SOVEREIGNTY_MANAGE)
//  @ApiImplicitParams({
//      @ApiImplicitParam(
//          name = "page",
//          dataType = "integer",
//          dataTypeClass = Integer.class,
//          paramType = "query",
//          defaultValue = "0",
//          example = "0",
//          value = "Zero-based page index (0..N)"),
//      @ApiImplicitParam(
//          name = "size",
//          dataType = "integer",
//          dataTypeClass = Integer.class,
//          paramType = "query",
//          defaultValue = "20",
//          example = "20",
//          value = "The size of the page to be returned"),
//      @ApiImplicitParam(
//          name = "sort",
//          allowMultiple = true,
//          dataType = "string",
//          dataTypeClass = String.class,
//          paramType = "query",
//          value =
//              "Sorting criteria in the format: name of property,(asc|desc). "
//                  + "Default sort order is ascending. "
//                  + "Multiple sort criteria are supported.")
//  })
  public ResponseEntity<Page<AccessLogEntryDto>> getAccessLogs(
      @ParameterObject @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) final Pageable pageable,
      final Authentication authentication
  ) {
    final Page<AccessLogEntryDto> page =
        this.accessLogService
            .getAccessLogs(pageable, JwtUtil.mapAuthentication(authentication))
            .map(this.accessLogMapper::mapEntityToDto);
    return ResponseEntity.ok(page);
  }
}
