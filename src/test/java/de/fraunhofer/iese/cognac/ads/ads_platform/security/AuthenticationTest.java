package de.fraunhofer.iese.cognac.ads.ads_platform.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class AuthenticationTest {

  @Test
  public void whenOfWithoutUsername_thenReturnAuthenticationWithoutUsername() {
    final Authentication testSubject = Authentication.of(RequestorType.UNKNOWN, "sub", "client", Arrays.asList("auth1", "auth2"));

    Assertions.assertEquals(RequestorType.UNKNOWN, testSubject.getRequestorType());
    Assertions.assertEquals("sub", testSubject.getSubject());
    Assertions.assertEquals("client", testSubject.getClientId());
    final List<String> authorities = testSubject.getAuthorities();
    Assertions.assertEquals(2, authorities.size());
    Assertions.assertTrue(authorities.contains("auth1"));
    Assertions.assertTrue(authorities.contains("auth2"));
    Assertions.assertNull(testSubject.getUsername());
  }

  @Test
  public void whenOfWithUsername_thenReturnAuthenticationWithUsername() {
    final Authentication testSubject = Authentication.of(RequestorType.UNKNOWN, "sub", "client", Arrays.asList("auth1", "auth2"), "username");

    Assertions.assertEquals(RequestorType.UNKNOWN, testSubject.getRequestorType());
    Assertions.assertEquals("sub", testSubject.getSubject());
    Assertions.assertEquals("client", testSubject.getClientId());
    final List<String> authorities = testSubject.getAuthorities();
    Assertions.assertEquals(2, authorities.size());
    Assertions.assertTrue(authorities.contains("auth1"));
    Assertions.assertTrue(authorities.contains("auth2"));
    Assertions.assertEquals("username", testSubject.getUsername());
  }

}