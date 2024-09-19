package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.RequestorType;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper.CommonsMapperImpl;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper.ConsentMapperImpl;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper.ConsentRequestMapperImpl;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Consent;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentState;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.RequestorIdentity;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service.ConsentService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@WebMvcTest(ConsentController.class)
@Import({ConsentRequestMapperImpl.class, ConsentMapperImpl.class, CommonsMapperImpl.class})
@ActiveProfiles("test")
class ConsentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ConsentService consentServiceMock;

  @AfterEach
  void teardown() {
    Mockito.verifyNoMoreInteractions(consentServiceMock);
  }

  @Nested
  class RevokeConsentTests {
    @Test
    void givenServiceRevokesConsent_whenRevokeConsent_thenReturnNoContent() throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));

      mockMvc
          .perform(
              post("/api/v1/twin-hub/consents/{id}/revoke", "testConsentId")
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("user-123")
                                      .claim(IdTokenClaimNames.AZP, "ads-platform-frontend"))
                          .authorities(
                              new SimpleGrantedAuthority(
                                  "SCOPE_urn:ads:twin-hub:data-sovereignty:manage"))))
          .andExpect(status().isNoContent());

      Mockito.verify(consentServiceMock, Mockito.times(1))
          .revokeConsent("testConsentId", expectedAuthentication);
    }

    @Test
    void givenServiceConsentDoesNotExist_whenRevokeConsent_thenNotFound() throws Exception {
      final Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      Mockito.doThrow(new DoesNotExistException())
          .when(consentServiceMock)
          .revokeConsent("Id_not_exist", authentication);

      mockMvc
          .perform(
              post("/api/v1/twin-hub/consents/{id}/revoke", "Id_not_exist")
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("user-123")
                                      .claim(IdTokenClaimNames.AZP, "ads-platform-frontend"))
                          .authorities(
                              new SimpleGrantedAuthority(
                                  "SCOPE_urn:ads:twin-hub:data-sovereignty:manage"))))
          .andExpect(status().isNotFound());

      Mockito.verify(consentServiceMock, Mockito.times(1))
          .revokeConsent("Id_not_exist", authentication);
    }

    @Test
    void whenRevokeConsentUnauthenticated_thenUnauthenticated() throws Exception {
      mockMvc
          .perform(post("/api/v1/twin-hub/consents/{id}/revoke", "testConsentId"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    void whenRevokeConsentWithoutScope_thenForbidden() throws Exception {
      mockMvc
          .perform(
              post("/api/v1/twin-hub/consents/{id}/revoke", "testConsentId")
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("user-123")
                                      .claim(IdTokenClaimNames.AZP, "not-ads-platform-frontend"))))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  class GetConsentTests {
    @Test
    void givenServiceReturnsConsent_whenGetConsent_thenReturnData() throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "user-123", "ads-platform-frontend", "user-123", "ads-platform-frontend"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("fieldTwin-1"),
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      final String json_output =
          "{"
              + "\"id\":\"b0573a68-841d-41c7-8d2e-629bf9292ae1\""
              + ",\"createdAt\":\"2022-06-17T07:46:53.971086400Z\","
              + "\"consentGiverId\":\"Bill-the-Farmer-ID\","
              + "\"requestorIdentity\":{"
              + "\"userId\":\"user-123\","
              + "\"clientId\":\"ads-platform-frontend\","
              + "\"userName\":\"user-123\","
              + "\"clientName\":\"ads-platform-frontend\""
              + "},"
              + "\"startTime\":\"2020-04-28T10:11:12Z\","
              + "\"endTime\":\"2022-04-28T10:11:12Z\","
              + "\"state\":\"ACTIVE\","
              + "\"grantFullAccess\":false,"
              + "\"grantAccessToAllTwins\":false,"
              + "\"twinIds\":[\"fieldTwin-1\"],"
              + "\"grantAllTwinResourcePermissions\":false,"
              + "\"twinResourcePermissions\":{"
              + "\"ARABLE_AREA\":["
              + "\"READ\","
              + "\"UPDATE\""
              + "]"
              + "},"
              + "\"dataUsageStatement\":\"data usable.\","
              + "\"additionalNotes\":\"additional.\""
              + "}";
      Mockito.when(
              consentServiceMock.getConsent(
                  "b0573a68-841d-41c7-8d2e-629bf9292ae1", expectedAuthentication))
          .thenReturn(Optional.of(mockedConsent));

      mockMvc
          .perform(
              get("/api/v1/twin-hub/consents/{id}", "b0573a68-841d-41c7-8d2e-629bf9292ae1")
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("user-123")
                                      .claim(IdTokenClaimNames.AZP, "ads-platform-frontend"))
                          .authorities(
                              new SimpleGrantedAuthority(
                                  "SCOPE_urn:ads:twin-hub:data-sovereignty:manage"))))
          .andExpect(status().isOk())
          .andExpect(content().json(json_output));

      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsent("b0573a68-841d-41c7-8d2e-629bf9292ae1", expectedAuthentication);
    }

    @Test
    void givenServiceReturnsNoConsent_whenGetConsent_thenReturnNotFound() throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      Mockito.when(consentServiceMock.getConsent("Id_not_exist", expectedAuthentication))
          .thenReturn(Optional.empty());

      mockMvc
          .perform(
              get("/api/v1/twin-hub/consents/{id}", "Id_not_exist")
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("user-123")
                                      .claim(IdTokenClaimNames.AZP, "ads-platform-frontend"))
                          .authorities(
                              new SimpleGrantedAuthority(
                                  "SCOPE_urn:ads:twin-hub:data-sovereignty:manage"))))
          .andExpect(status().isNotFound());

      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsent("Id_not_exist", expectedAuthentication);
    }

    @Test
    void whenGetConsentUnauthenticated_thenUnauthenticated() throws Exception {
      mockMvc
          .perform(get("/api/v1/twin-hub/consents/{id}", "testConsentId"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    void whenGetConsentWithoutScope_thenForbidden() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/twin-hub/consents/{id}", "testConsentId")
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("user-123")
                                      .claim(IdTokenClaimNames.AZP, "not-ads-platform-frontend"))))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  class GetConsentsTests {
    @Test
    void givenServiceReturnsConsents_whenGetConsents_thenReturnData() throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      List<Consent> mockedConsents = new ArrayList<>();
      Consent consent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "user-123", "ads-platform-frontend", "user-123", "ads-platform-frontend"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("fieldTwin-1"),
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      mockedConsents.add(consent);
      final String json_output =
          "[{"
              + "\"id\":\"b0573a68-841d-41c7-8d2e-629bf9292ae1\""
              + ",\"createdAt\":\"2022-06-17T07:46:53.971086400Z\","
              + "\"consentGiverId\":\"Bill-the-Farmer-ID\","
              + "\"requestorIdentity\":{"
              + "\"userId\":\"user-123\","
              + "\"clientId\":\"ads-platform-frontend\","
              + "\"userName\":\"user-123\","
              + "\"clientName\":\"ads-platform-frontend\""
              + "},"
              + "\"startTime\":\"2020-04-28T10:11:12Z\","
              + "\"endTime\":\"2022-04-28T10:11:12Z\","
              + "\"state\":\"ACTIVE\","
              + "\"grantFullAccess\":false,"
              + "\"grantAccessToAllTwins\":false,"
              + "\"twinIds\":[\"fieldTwin-1\"],"
              + "\"grantAllTwinResourcePermissions\":false,"
              + "\"twinResourcePermissions\":{"
              + "\"ARABLE_AREA\":["
              + "\"READ\","
              + "\"UPDATE\""
              + "]"
              + "},"
              + "\"dataUsageStatement\":\"data usable.\","
              + "\"additionalNotes\":\"additional.\""
              + "}]";
      Mockito.when(consentServiceMock.getConsents(expectedAuthentication))
          .thenReturn(mockedConsents);

      mockMvc
          .perform(
              get("/api/v1/twin-hub/consents")
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("user-123")
                                      .claim(IdTokenClaimNames.AZP, "ads-platform-frontend"))
                          .authorities(
                              new SimpleGrantedAuthority(
                                  "SCOPE_urn:ads:twin-hub:data-sovereignty:manage"))))
          .andExpect(status().isOk())
          .andExpect(content().json(json_output));

      Mockito.verify(consentServiceMock, Mockito.times(1)).getConsents(expectedAuthentication);
    }

    @Test
    void whenGetConsentsWithoutAuthentication_thenUnauthorized() throws Exception {
      mockMvc.perform(get("/api/v1/twin-hub/consents")).andExpect(status().isUnauthorized());
    }

    @Test
    void whenGetConsentsWithoutScope_thenForbidden() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/twin-hub/consents")
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("user-123")
                                      .claim(IdTokenClaimNames.AZP, "not-ads-platform-frontend"))))
          .andExpect(status().isForbidden());
    }
  }
}
