package de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.api.controller;

import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.api.dto.ConnectorCredentialDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.model.ConnectorCredential;
import de.fraunhofer.iese.cognac.ads.ads_platform.connector.credentials.business.service.ConnectorCredentialsService;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.InvalidInputException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.JwtUtil;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.SecurityFilters;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v2/connector/credentials")
public class ConnectorCredentialsController {
  private final ConnectorCredentialsService connectorCredentialsService;

  public ConnectorCredentialsController(final ConnectorCredentialsService connectorCredentialsService) {
    this.connectorCredentialsService = connectorCredentialsService;
  }

  @GetMapping
  @PreAuthorize(SecurityFilters.HAS_SCOPE_CONNECTOR_CONNECTOR_CREDENTIALS_MANAGE)
  public ResponseEntity<List<ConnectorCredentialDto>> getConnectorCredentials(JwtAuthenticationToken jwtAuthenticationToken) {
    return ResponseEntity.ok(
        this.connectorCredentialsService.getOwnConnectorCredentials(JwtUtil.mapAuthentication(jwtAuthenticationToken)).stream()
            .map(ConnectorCredentialDto::new)
            .collect(Collectors.toList())
    );
  }

  @PostMapping
  @PreAuthorize(SecurityFilters.HAS_SCOPE_CONNECTOR_CONNECTOR_CREDENTIALS_MANAGE)
  public ResponseEntity<ConnectorCredentialDto> createConnectorCredential(@RequestBody @Valid ConnectorCredentialDto payload, JwtAuthenticationToken jwtAuthenticationToken) {
    try {
      final ConnectorCredential createdConnectorCredential = this.connectorCredentialsService.createCredential(JwtUtil.mapAuthentication(jwtAuthenticationToken), payload.getProcessDetails().getTwinId(), payload.getProcessDetails().getAccessType(), payload.getProcessDetails().getTwinResourceId(), payload.getSingleUse());
      return ResponseEntity.status(HttpStatus.CREATED).body(new ConnectorCredentialDto(createdConnectorCredential));
    } catch (ForbiddenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    } catch (InvalidInputException e) {
      return ResponseEntity.unprocessableEntity().build();
    }
  }

  @DeleteMapping("{id}")
  @PreAuthorize(SecurityFilters.HAS_SCOPE_CONNECTOR_CONNECTOR_CREDENTIALS_MANAGE)
  public ResponseEntity<Void> deleteConnectorCredential(@PathVariable String id, JwtAuthenticationToken jwtAuthenticationToken) {
    this.connectorCredentialsService.deleteCredential(id, JwtUtil.mapAuthentication(jwtAuthenticationToken));
    return ResponseEntity.noContent().build();
  }

}
