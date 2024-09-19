package de.fraunhofer.iese.cognac.ads.ads_platform.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Arrays;

class JwtUtilTest {

  @Test
  void givenSpringAuthenticationForUser_whenMapAuthentication_thenReturnCognacAuthenticationForUser() {
    final Authentication springAuthentication = JwtTestUtil.getAuthenticationWithUsername("client", "subj", "scope1 scope2", "username");

    final de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication cognacAuthentication = JwtUtil.mapAuthentication(springAuthentication);

    Assertions.assertNotNull(cognacAuthentication);
    Assertions.assertEquals(RequestorType.CLIENT_USING_USER_ACCOUNT, cognacAuthentication.getRequestorType());
    Assertions.assertEquals("client", cognacAuthentication.getClientId());
    Assertions.assertEquals("subj", cognacAuthentication.getSubject());
    Assertions.assertEquals("username", cognacAuthentication.getUsername());
    Assertions.assertLinesMatch(Arrays.asList("SCOPE_scope1", "SCOPE_scope2"), cognacAuthentication.getAuthorities());
  }

  @Test
  void givenSpringAuthenticationForServiceAccount_whenMapAuthentication_thenReturnCognacAuthenticationForServiceAccount() {
    final Authentication springAuthentication = JwtTestUtil.getAuthentication("client", "subj", "scope1 scope2", true);

    final de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication cognacAuthentication = JwtUtil.mapAuthentication(springAuthentication);

    Assertions.assertNotNull(cognacAuthentication);
    Assertions.assertEquals(RequestorType.CLIENT_USING_SERVICE_ACCOUNT, cognacAuthentication.getRequestorType());
    Assertions.assertEquals("client", cognacAuthentication.getClientId());
    Assertions.assertEquals("subj", cognacAuthentication.getSubject());
    Assertions.assertLinesMatch(Arrays.asList("SCOPE_scope1", "SCOPE_scope2"), cognacAuthentication.getAuthorities());
  }

}
