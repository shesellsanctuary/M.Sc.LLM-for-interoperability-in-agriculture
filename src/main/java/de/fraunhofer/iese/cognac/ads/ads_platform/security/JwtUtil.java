package de.fraunhofer.iese.cognac.ads.ads_platform.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JwtUtil {
  private static final String THIRD_PARTY_SERVICE_REALM_ROLE = "third-party-service";

  public static Authentication mapAuthentication(final org.springframework.security.core.Authentication authentication) {
    if (!(authentication instanceof JwtAuthenticationToken)) {
      throw new RuntimeException("authentication not instanceof JwtAuthenticationToken");
    } else {
      final JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
      return Authentication.of(
          determineRequestorType(jwtAuthenticationToken),
          jwtAuthenticationToken.getToken().getSubject(),
          determineClientId(jwtAuthenticationToken),
          determineAuthorities(jwtAuthenticationToken),
          determineUsername(jwtAuthenticationToken)
      );
    }
  }

  @Nullable
  private static String determineClientId(final JwtAuthenticationToken jwtAuthenticationToken) {
    return jwtAuthenticationToken.getToken().getClaim(IdTokenClaimNames.AZP);
  }

  private static RequestorType determineRequestorType(JwtAuthenticationToken jwtAuthenticationToken) {
    final RequestorType requestorType;
    if (
        StringUtils.isBlank(determineClientId(jwtAuthenticationToken))
            || StringUtils.isBlank(jwtAuthenticationToken.getToken().getSubject())
    ) {
      requestorType = RequestorType.UNKNOWN;
    } else if (isServiceAccount(jwtAuthenticationToken)) {
      requestorType = RequestorType.CLIENT_USING_SERVICE_ACCOUNT;
    } else if (StringUtils.isNotBlank(jwtAuthenticationToken.getToken().getSubject())) {
      requestorType = RequestorType.CLIENT_USING_USER_ACCOUNT;
    } else {
      requestorType = RequestorType.UNKNOWN;
    }
    return requestorType;
  }

  private static List<String> determineAuthorities(JwtAuthenticationToken jwtAuthenticationToken) {
    return jwtAuthenticationToken.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
  }

  private static boolean isServiceAccount(final JwtAuthenticationToken jwtAuthenticationToken) {
    return Optional.ofNullable(jwtAuthenticationToken)
        .map(JwtAuthenticationToken::getToken)
        .map(jwt -> jwt.getClaimAsMap("realm_access"))
        .map(realmAccess -> realmAccess.getOrDefault("roles", null))
        .map(roles -> {
          if (roles instanceof List) {
            return ((List<?>) roles).stream().anyMatch(THIRD_PARTY_SERVICE_REALM_ROLE::equals);
          } else {
            return null;
          }
        })
        .orElse(false);
  }

  @Nullable
  private static String determineUsername(final JwtAuthenticationToken jwtAuthenticationToken) {
    return jwtAuthenticationToken.getToken().getClaim(StandardClaimNames.PREFERRED_USERNAME);
  }

}
