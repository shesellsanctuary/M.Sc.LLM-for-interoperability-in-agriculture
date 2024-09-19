package de.fraunhofer.iese.cognac.ads.ads_platform.security;

import lombok.Value;
import org.springframework.lang.Nullable;

import java.util.List;

@Value(staticConstructor = "of")
public class Authentication {
  RequestorType requestorType;
  String subject;
  String clientId;
  List<String> authorities;
  @Nullable
  String username;

  public static Authentication of(final RequestorType requestorType, final String subject, final String clientId, final List<String> authorities) {
    return new Authentication(requestorType, subject, clientId, authorities, null);
  }
}
