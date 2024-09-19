package de.fraunhofer.iese.cognac.ads.ads_platform.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@Hidden
public class RootController {
  private static final String FRONTEND_CLIENT_ID = "ads-platform-frontend";
  private static final String API_BASE_URL = "/api/v1";
  private final OAuth2ResourceServerProperties.Jwt jwtProperties;

  @Autowired
  public RootController(OAuth2ResourceServerProperties properties) {
    this.jwtProperties = properties.getJwt();
  }

  @GetMapping("config.json")
  @ResponseBody
  public ResponseEntity<Map<String, String>> getConfig() {
    final Map<String, String> configMap = new HashMap<>();

    configMap.put("adsPlatformBaseUrl", API_BASE_URL);
    configMap.put("stsAuthority", this.jwtProperties.getIssuerUri());
    configMap.put("clientId", FRONTEND_CLIENT_ID);

    return ResponseEntity.ok(configMap);
  }

}
