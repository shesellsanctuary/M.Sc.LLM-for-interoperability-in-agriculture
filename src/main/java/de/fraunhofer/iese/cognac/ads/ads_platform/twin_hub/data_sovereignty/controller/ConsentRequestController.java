package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.controller;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.JwtUtil;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.SecurityFilters;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.ConsentRequestAnswerDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.ConsentRequestDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper.ConsentRequestMapper;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service.ConsentRequestService;

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
@RequestMapping("/api/v1/twin-hub/consent-requests")
public class ConsentRequestController {
  private final ConsentRequestService consentRequestService;
  private final ConsentRequestMapper consentRequestMapper;

  @Autowired
  public ConsentRequestController(
      final ConsentRequestService consentRequestService,
      final ConsentRequestMapper consentRequestMapper
  ) {
    this.consentRequestService = consentRequestService;
    this.consentRequestMapper = consentRequestMapper;
  }

  @GetMapping
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_DATA_SOVEREIGNTY_MANAGE)
  public ResponseEntity<List<ConsentRequestDto>> getConsentRequests(final Authentication authentication) {
    return ResponseEntity.ok(
        this.consentRequestService.getConsentRequests(JwtUtil.mapAuthentication(authentication)).stream()
            .map(this.consentRequestMapper::mapEntityToDto)
            .collect(Collectors.toList())
    );
  }

  @PostMapping
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_DATA_SOVEREIGNTY_REQUEST_CONSENT)
  public ResponseEntity<ConsentRequestDto> createConsentRequest(
      @RequestBody @Valid final ConsentRequestDto body,
      final Authentication authentication
  ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        this.consentRequestMapper.mapEntityToDto(
            this.consentRequestService.createConsentRequest(
                this.consentRequestMapper.mapDtoToEntity(body),
                JwtUtil.mapAuthentication(authentication)
            )
        )
    );
  }

  @GetMapping("{id}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_DATA_SOVEREIGNTY_MANAGE)
  public ResponseEntity<ConsentRequestDto> getConsentRequest(
      @PathVariable(name = "id") final String id,
      final Authentication authentication
  ) {
    return ResponseEntity.of(
        this.consentRequestService.getConsentRequest(id, JwtUtil.mapAuthentication(authentication))
            .map(this.consentRequestMapper::mapEntityToDto)
    );
  }

  @PostMapping("{id}/answer")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_TWIN_HUB_DATA_SOVEREIGNTY_MANAGE)
  public ResponseEntity<Void> answerConsentRequest(
      @PathVariable final String id,
      @RequestBody @Valid final ConsentRequestAnswerDto body,
      Authentication authentication
  ) {
    try {
      this.consentRequestService.answerConsentRequest(
          id,
          this.consentRequestMapper.mapDtoToEntity(
              body
          ),
          JwtUtil.mapAuthentication(authentication)
      );
      return ResponseEntity.noContent().build();
    } catch (DoesNotExistException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
