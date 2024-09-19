package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.util;

import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.RequestorIdentity;

import org.springframework.lang.Nullable;

public class RequestorIdentityUtil {

  private static final String SERVICE_ACCOUNT_USERNAME_SUFFIX = " - Service Account";

  public static RequestorIdentity determineRequestorIdentity(final Authentication authentication) {
    return RequestorIdentity.of(
        determineUserId(authentication),
        determineClientId(authentication),
        determineUserName(authentication),
        determineClientName(authentication)
    );
  }

  private static String determineUserId(final Authentication authentication) {
    return authentication.getSubject();
  }

  private static String determineClientId(final Authentication authentication) {
    return authentication.getClientId();
  }

  @Nullable
  private static String determineUserName(final Authentication authentication) {
    // TODO one might want to improve this
    switch (authentication.getRequestorType()) {
      case CLIENT_USING_USER_ACCOUNT:
        return authentication.getUsername();
      case CLIENT_USING_SERVICE_ACCOUNT:
        return determineClientName(authentication) + SERVICE_ACCOUNT_USERNAME_SUFFIX;
      default:
        return null;
    }
  }

  private static String determineClientName(final Authentication authentication) {
    // TODO one might want to improve this
    return authentication.getClientId();
  }
}
