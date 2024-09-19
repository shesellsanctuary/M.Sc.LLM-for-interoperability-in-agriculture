package de.fraunhofer.iese.cognac.ads.ads_platform.security;

import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JwtTestUtil {

  public static JwtAuthenticationToken getAuthentication(final String client, final String subject, final String scope) {
    return getAuthenticationWithUsername(client, subject, scope, null, false);
  }

  public static JwtAuthenticationToken getAuthentication(final String client, final String subject, final String scope, final boolean isServiceAccount) {
    return getAuthenticationWithUsername(client, subject, scope, null, isServiceAccount);
  }

  public static JwtAuthenticationToken getAuthenticationWithUsername(final String client, final String subject, final String scope, final String username) {
    return getAuthenticationWithUsername(client, subject, scope, username, false);
  }


  private static JwtAuthenticationToken getAuthenticationWithUsername(final String client, final String subject, final String scope, @Nullable final String username, final boolean isServiceAccount) {
    final Supplier<Jwt> jwtSupplier = () -> {
      final Jwt.Builder tokenBuilder = Jwt.withTokenValue("token");
      tokenBuilder
          .header("alg", "none")
          .subject(subject)
          .claim("scope", scope)
          .claim(IdTokenClaimNames.AZP, client);

      if (null != username) {
        tokenBuilder.claim(StandardClaimNames.PREFERRED_USERNAME, username);
      }

      if (isServiceAccount) {
        final Map<String, List<String>> realmAccessMap = new HashMap<>();
        realmAccessMap.put("roles", Collections.singletonList("third-party-service"));
        tokenBuilder.claim("realm_access", realmAccessMap);
      }

      return tokenBuilder.build();
    };
    return new JwtAuthenticationToken(
        jwtSupplier.get(),
        Arrays.stream(scope.split(" ")).map(s -> "SCOPE_" + s).map(SimpleGrantedAuthority::new).collect(Collectors.toList())
    );
  }
}
