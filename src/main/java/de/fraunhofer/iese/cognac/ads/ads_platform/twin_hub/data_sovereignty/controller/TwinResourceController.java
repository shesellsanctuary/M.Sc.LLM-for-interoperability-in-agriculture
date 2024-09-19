package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.controller;

import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.TwinResourceDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/twin-hub/twin-resources")
public class TwinResourceController {
  @Autowired
  public TwinResourceController() {}

  @GetMapping
  public ResponseEntity<List<TwinResourceDto>> getTwinResources() {
    return ResponseEntity.ok(Arrays.asList(TwinResourceDto.values()));
  }
}
