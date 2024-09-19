package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.util;

import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.RequestorType;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.RequestorIdentity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class RequestorIdentityUtilTest {

  @Test
  void givenAuthenticationForUser_whenDetermineRequestorIdentity_thenReturnRequestorIdentityForUser() {
    final Authentication authentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "userId", "clientId", Collections.emptyList(), "userName");

    final RequestorIdentity actualRequestorIdentity = RequestorIdentityUtil.determineRequestorIdentity(authentication);

    final RequestorIdentity expectedRequestorIdentity = RequestorIdentity.of("userId", "clientId", "userName", "clientId");
    Assertions.assertEquals(expectedRequestorIdentity, actualRequestorIdentity);
  }

  @Test
  void givenAuthenticationForServiceAccount_whenDetermineRequestorIdentity_thenReturnRequestorIdentityForServiceAccount() {
    final Authentication authentication = Authentication.of(RequestorType.CLIENT_USING_SERVICE_ACCOUNT, "userId", "clientId", Collections.emptyList());

    final RequestorIdentity actualRequestorIdentity = RequestorIdentityUtil.determineRequestorIdentity(authentication);

    final RequestorIdentity expectedRequestorIdentity = RequestorIdentity.of("userId", "clientId", "clientId - Service Account", "clientId");
    Assertions.assertEquals(expectedRequestorIdentity, actualRequestorIdentity);
  }
}