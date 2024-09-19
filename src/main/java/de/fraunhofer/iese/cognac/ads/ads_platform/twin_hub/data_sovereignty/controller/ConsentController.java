package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.controller;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.JwtUtil;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.SecurityFilters;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.ConsentDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper.ConsentMapper;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service.ConsentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/twin-hub/consents")
public class ConsentController {
  private final ConsentService consentService;
  private final ConsentMapper consentMapper;

  @Autowired
  public ConsentController(final ConsentService consentService, final ConsentMapper consentMapper) {
    this.consentService = consentService;
    this.consentMapper = consentMapper;
  }

  @GetMapping
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_DATA_SOVEREIGNTY_MANAGE)
  public ResponseEntity<List<ConsentDto>> getConsents(final Authentication authentication) {
    return ResponseEntity.ok(
        this.consentService.getConsents(JwtUtil.mapAuthentication(authentication)).stream()
            .map(this.consentMapper::mapEntityToDto)
            .collect(Collectors.toList())
    );
  }

  @GetMapping("{id}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_DATA_SOVEREIGNTY_MANAGE)
  public ResponseEntity<ConsentDto> getConsent(@PathVariable(name = "id") final String id, final Authentication authentication) {
    return ResponseEntity.of(
        this.consentService.getConsent(id, JwtUtil.mapAuthentication(authentication))
            .map(this.consentMapper::mapEntityToDto)
    );
  }

  @PostMapping("{id}/revoke")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_DATA_SOVEREIGNTY_MANAGE)
  public ResponseEntity<Void> revokeConsent(@PathVariable(name = "id") final String id, final Authentication authentication) {
    try {
      this.consentService.revokeConsent(id, JwtUtil.mapAuthentication(authentication));
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
