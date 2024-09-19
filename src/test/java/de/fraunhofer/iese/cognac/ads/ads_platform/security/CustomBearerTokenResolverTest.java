package de.fraunhofer.iese.cognac.ads.ads_platform.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import javax.servlet.http.HttpServletRequest;

class CustomBearerTokenResolverTest {

  private CustomBearerTokenResolver testSubject = new CustomBearerTokenResolver();

  @Test
  void whenTokenIsInHeaderThenResolve() {
    final HttpServletRequest mockedHttpServletRequest = Mockito.mock(HttpServletRequest.class);
    final String expectedToken = "test123";
    Mockito.when(mockedHttpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + expectedToken);
    final String token = testSubject.resolve(mockedHttpServletRequest);
    Assertions.assertEquals(expectedToken, token);
  }

  @Test
  void whenTokenIsNotInHeaderThenReturnNull() {
    final HttpServletRequest mockedHttpServletRequest = Mockito.mock(HttpServletRequest.class);
    Mockito.when(mockedHttpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
    final String token = testSubject.resolve(mockedHttpServletRequest);
    Assertions.assertNull(token);
  }

  @Test()
  void whenTokenIsMalformedThenThrowException() {
    final HttpServletRequest mockedHttpServletRequest = Mockito.mock(HttpServletRequest.class);
    Mockito.when(mockedHttpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer testü test");
    Assertions.assertThrows(OAuth2AuthenticationException.class, () -> testSubject.resolve(mockedHttpServletRequest));
  }
}