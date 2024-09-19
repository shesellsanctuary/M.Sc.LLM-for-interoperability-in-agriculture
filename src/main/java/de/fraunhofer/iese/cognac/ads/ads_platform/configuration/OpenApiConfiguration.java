package de.fraunhofer.iese.cognac.ads.ads_platform.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
  private final OAuth2ResourceServerProperties.Jwt jwtProperties;

  @Autowired
  public OpenApiConfiguration(OAuth2ResourceServerProperties properties) {
    this.jwtProperties = properties.getJwt();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    final String securitySchemeName = "oidc";
    final SecurityScheme securityScheme = new SecurityScheme();
    securityScheme.setName(securitySchemeName);
    securityScheme.setType(SecurityScheme.Type.OPENIDCONNECT);
    securityScheme.setScheme("bearer");
    securityScheme.setOpenIdConnectUrl(this.jwtProperties.getIssuerUri() + "/.well-known/openid-configuration");
    securityScheme.setBearerFormat("JWT");

    return new OpenAPI().addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(new Components().addSecuritySchemes(securitySchemeName, securityScheme));
  }
}
