package de.fraunhofer.iese.cognac.ads.ads_platform.configuration;

import de.fraunhofer.iese.cognac.ads.ads_platform.security.CustomBearerTokenResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private static final String REQUIRED_AUD = "ads-platform";
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
  private final OAuth2ResourceServerProperties.Jwt jwtProperties;

  @Autowired
  public SecurityConfig(OAuth2ResourceServerProperties properties, ObjectMapper objectMapper) {
    this.jwtProperties = properties.getJwt();
    try {
      logger.info("OAuth2ResourceServerProperties.JWT: {}", objectMapper.writeValueAsString(this.jwtProperties));
    } catch (JsonProcessingException e) {
      logger.warn(e.getMessage(), e);
    }
  }

  @Bean
  JwtDecoder jwtDecoder() {
    final NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(this.jwtProperties.getJwkSetUri())
        .jwsAlgorithm(SignatureAlgorithm.from(this.jwtProperties.getJwsAlgorithm())).build();
    final String issuerUri = this.jwtProperties.getIssuerUri();
    if (StringUtils.isBlank(issuerUri)) {
      throw new BeanCreationException("Value for spring.security.oauth2.resourceserver.issuerUri is missing");
    }
    List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
    validators.add(JwtValidators.createDefaultWithIssuer(issuerUri));
    validators.add(new JwtClaimValidator<List<String>>(JwtClaimNames.AUD, aud -> null != aud && aud.contains(REQUIRED_AUD)));
    nimbusJwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));
    return nimbusJwtDecoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // make sure that no unauthenticated access to api is possible
    // Use of @PreAuthorize in Controllers to check for scope and more...
    http.authorizeRequests(
        requests -> requests
            .mvcMatchers("/api/v2/connector/access/**").permitAll()
            .mvcMatchers("/api/**").authenticated()
    );
    http.oauth2ResourceServer(
        httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer
            .jwt().and()
            .bearerTokenResolver(bearerTokenResolver())
    );
    http.csrf().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.cors(); // TODO check cross origin for long term suitability, do we want to stick with it?
  }

  private BearerTokenResolver bearerTokenResolver() {
    return new CustomBearerTokenResolver();
  }

}
