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
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequest;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequestAnswer;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequestDecision;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.RequestorIdentity;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service.ConsentRequestService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@WebMvcTest(ConsentRequestController.class)
@Import({ConsentRequestMapperImpl.class, ConsentMapperImpl.class, CommonsMapperImpl.class})
@ActiveProfiles("test")
class ConsentRequestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ConsentRequestService consentRequestServiceMock;

  @AfterEach
  void teardown() {
    Mockito.verifyNoMoreInteractions(consentRequestServiceMock);
  }

  @Nested
  class AnswerConsentRequestTests {
    @Test
    void givenServiceAnswersConsentRequest_whenAnswerConsentRequest_thenReturnNoContent()
        throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      final String json_input =
          "{"
              + "\"additionalNotes\": \"as reference see contract in harvesting folder.\","
              + "\"decision\": \"ACCEPT\","
              + "\"grantAccessToAllTwins\": false,"
              + "\"twinIds\": ["
              + "\"fieldTwin-1\","
              + "\"fieldTwin-2\""
              + "]"
              + "}";
      final ConsentRequestAnswer consentRequestAnswer =
          ConsentRequestAnswer.of(
              ConsentRequestDecision.ACCEPT,
              false,
              Set.of("fieldTwin-1", "fieldTwin-2"),
              "as reference see contract in harvesting folder.");
      final String consentRequestId = "testId";
      Mockito.doNothing()
          .when(consentRequestServiceMock)
          .answerConsentRequest(consentRequestId, consentRequestAnswer, expectedAuthentication);

      mockMvc
          .perform(
              post("/api/v1/twin-hub/consent-requests/{id}/answer", "testId")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(json_input)
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("Bill-the-Farmer-ID")
                                      .claim(IdTokenClaimNames.AZP, "ads-platform-frontend"))
                          .authorities(
                              new SimpleGrantedAuthority(
                                  "SCOPE_urn:ads:twin-hub:data-sovereignty:manage"))))
          .andExpect(status().isNoContent());

      Mockito.verify(consentRequestServiceMock, Mockito.times(1))
          .answerConsentRequest(consentRequestId, consentRequestAnswer, expectedAuthentication);
    }

    @Test
    void givenServiceConsentRequestNotExist_whenAnswerConsentRequest_thenNotFound()
        throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      final String json_input =
          "{"
              + "\"additionalNotes\": \"as reference see contract in harvesting folder.\","
              + "\"decision\": \"ACCEPT\","
              + "\"grantAccessToAllTwins\": false,"
              + "\"twinIds\": ["
              + "\"fieldTwin-1\","
              + "\"fieldTwin-2\""
              + "]"
              + "}";
      final ConsentRequestAnswer consentRequestAnswer =
          ConsentRequestAnswer.of(
              ConsentRequestDecision.ACCEPT,
              false,
              Set.of("fieldTwin-1", "fieldTwin-2"),
              "as reference see contract in harvesting folder.");
      Mockito.doThrow(new DoesNotExistException())
          .when(consentRequestServiceMock)
          .answerConsentRequest(
              "testConsentRequestId_not_exist", consentRequestAnswer, expectedAuthentication);

      mockMvc
          .perform(
              post(
                      "/api/v1/twin-hub/consent-requests/{id}/answer",
                      "testConsentRequestId_not_exist")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(json_input)
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("Bill-the-Farmer-ID")
                                      .claim(IdTokenClaimNames.AZP, "ads-platform-frontend"))
                          .authorities(
                              new SimpleGrantedAuthority(
                                  "SCOPE_urn:ads:twin-hub:data-sovereignty:manage"))))
          .andExpect(status().isNotFound());

      Mockito.verify(consentRequestServiceMock, Mockito.times(1))
          .answerConsentRequest(
              "testConsentRequestId_not_exist", consentRequestAnswer, expectedAuthentication);
    }

    @Test
    void whenAnswerConsentRequestUnauthenticated_thenUnauthenticated() throws Exception {
      mockMvc
          .perform(get("/api/v1/twin-hub/consent-requests/{id}/answer", "testId"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAnswerConsentRequestWithoutScope_thenForbidden() throws Exception {
      final String json_input =
          "{"
              + "\"additionalNotes\": \"as reference see contract in harvesting folder.\","
              + "\"decision\": \"ACCEPT\","
              + "\"grantAccessToAllTwins\": false,"
              + "\"twinIds\": ["
              + "\"fieldTwin-1\","
              + "\"fieldTwin-2\""
              + "]"
              + "}";

      mockMvc
          .perform(
              post("/api/v1/twin-hub/consent-requests/{id}/answer", "testId")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(json_input)
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("Bill-the-Farmer-ID")
                                      .claim(IdTokenClaimNames.AZP, "not-ads-platform-frontend"))))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  class GetConsentRequestTests {
    @Test
    void givenServiceReturnsConsentRequest_whenGetConsentRequest_thenReturnData() throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      ConsentRequest mockedConsentRequest =
          ConsentRequest.of(
              "980cedfd-1cf1-4fd8-a45f-65a7196ea217",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-farmer-ID",
              RequestorIdentity.of(
                  "user-123", "ads-platform-frontend", "user-123", "ads-platform-frontend"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              false,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usage is allowed.");
      final String json_output =
          "{"
              + "\"id\":\"980cedfd-1cf1-4fd8-a45f-65a7196ea217\","
              + "\"createdAt\":\"2022-06-17T07:46:53.971086400Z\","
              + "\"receiverId\":\"Bill-the-farmer-ID\","
              + "\"requestorIdentity\":{"
              + "\"userId\":\"user-123\","
              + "\"clientId\":\"ads-platform-frontend\","
              + "\"userName\":\"user-123\","
              + "\"clientName\":\"ads-platform-frontend\""
              + "},"
              + "\"startTime\":\"2020-04-28T10:11:12Z\","
              + "\"endTime\":\"2022-04-28T10:11:12Z\","
              + "\"requestFullAccess\":false,"
              + "\"requestAllTwinResourcePermissions\":false,"
              + "\"twinResourcePermissions\":{"
              + "\"ARABLE_AREA\":["
              + "\"UPDATE\","
              + "\"READ\""
              + "]"
              + "},"
              + "\"dataUsageStatement\":\"data usage is allowed.\""
              + "}";
      Mockito.when(consentRequestServiceMock.getConsentRequest("testId", expectedAuthentication))
          .thenReturn(Optional.of(mockedConsentRequest));

      mockMvc
          .perform(
              get("/api/v1/twin-hub/consent-requests/{id}", "testId")
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

      Mockito.verify(consentRequestServiceMock, Mockito.times(1))
          .getConsentRequest("testId", expectedAuthentication);
    }

    @Test
    void givenServiceReturnsNoConsentRequest_whenGetConsentRequest_thenReturnNotFound()
        throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      Mockito.when(
              consentRequestServiceMock.getConsentRequest(
                  "testId_not_exist", expectedAuthentication))
          .thenReturn(Optional.empty());

      mockMvc
          .perform(
              get("/api/v1/twin-hub/consent-requests/{id}", "testId_not_exist")
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

      Mockito.verify(consentRequestServiceMock, Mockito.times(1))
          .getConsentRequest("testId_not_exist", expectedAuthentication);
    }

    @Test
    void whenGetConsentRequestUnauthenticated_thenUnauthenticated() throws Exception {
      mockMvc
          .perform(get("/api/v1/twin-hub/consent-request/{id}", "testId"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    void whenGetConsentRequestsWithoutScope_thenForbidden() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/twin-hub/consent-requests/{id}", "testId")
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
  class GetConsentRequestsTests {
    @Test
    void givenServiceReturnsConsentRequests_whenGetConsentRequests_thenReturnData()
        throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      List<ConsentRequest> mockedRequests = new ArrayList<>();
      ConsentRequest consentRequest =
          ConsentRequest.of(
              "980cedfd-1cf1-4fd8-a45f-65a7196ea217",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-farmer-ID",
              RequestorIdentity.of(
                  "user-123", "ads-platform-frontend", "user-123", "ads-platform-frontend"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              false,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usage is allowed.");
      mockedRequests.add(consentRequest);
      final String json_output =
          "[{"
              + "\"id\":\"980cedfd-1cf1-4fd8-a45f-65a7196ea217\","
              + "\"createdAt\":\"2022-06-17T07:46:53.971086400Z\","
              + "\"receiverId\":\"Bill-the-farmer-ID\","
              + "\"requestorIdentity\":{"
              + "\"userId\":\"user-123\","
              + "\"clientId\":\"ads-platform-frontend\","
              + "\"userName\":\"user-123\","
              + "\"clientName\":\"ads-platform-frontend\""
              + "},"
              + "\"startTime\":\"2020-04-28T10:11:12Z\","
              + "\"endTime\":\"2022-04-28T10:11:12Z\","
              + "\"requestFullAccess\":false,"
              + "\"requestAllTwinResourcePermissions\":false,"
              + "\"twinResourcePermissions\":{"
              + "\"ARABLE_AREA\":["
              + "\"UPDATE\","
              + "\"READ\""
              + "]"
              + "},"
              + "\"dataUsageStatement\":\"data usage is allowed.\""
              + "}]";
      Mockito.when(consentRequestServiceMock.getConsentRequests(expectedAuthentication))
          .thenReturn(mockedRequests);

      mockMvc
          .perform(
              get("/api/v1/twin-hub/consent-requests")
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

      Mockito.verify(consentRequestServiceMock, Mockito.times(1))
          .getConsentRequests(expectedAuthentication);
    }

    @Test
    void whenGetConsentRequestsUnauthenticated_thenUnauthenticated() throws Exception {
      mockMvc
          .perform(get("/api/v1/twin-hub/consent-requests"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    void whenGetConsentRequestsWithoutScope_thenForbidden() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/twin-hub/consent-requests")
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
  class CreateConsentRequestTests {
    @Test
    void givenServiceCreatedConsentRequest_whenCreateRequest_thenReturnData() throws Exception {
      final Authentication expectedAuthentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:request-consent"),
              "username");
      ConsentRequest mockedConsentRequest_created =
          ConsentRequest.of(
              "980cedfd-1cf1-4fd8-a45f-65a7196ea217",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-farmer-ID",
              RequestorIdentity.of(
                  "user-123", "ads-platform-frontend", "username", "ads-platform-frontend"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              false,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usage is allowed.");
      ConsentRequest mockedConsentRequest_input =
          ConsentRequest.of(
              null,
              null,
              "Bill-the-farmer-ID",
              null,
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              false,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usage is allowed.");
      final String json_output =
          "{"
              + "\"id\":\"980cedfd-1cf1-4fd8-a45f-65a7196ea217\","
              + "\"createdAt\":\"2022-06-17T07:46:53.971086400Z\","
              + "\"receiverId\":\"Bill-the-farmer-ID\","
              + "\"requestorIdentity\":{"
              + "\"userId\":\"user-123\","
              + "\"clientId\":\"ads-platform-frontend\","
              + "\"userName\":\"username\","
              + "\"clientName\":\"ads-platform-frontend\""
              + "},"
              + "\"startTime\":\"2020-04-28T10:11:12Z\","
              + "\"endTime\":\"2022-04-28T10:11:12Z\","
              + "\"requestFullAccess\":false,"
              + "\"requestAllTwinResourcePermissions\":false,"
              + "\"twinResourcePermissions\":{"
              + "\"ARABLE_AREA\":["
              + "\"UPDATE\","
              + "\"READ\""
              + "]"
              + "},"
              + "\"dataUsageStatement\":\"data usage is allowed.\""
              + "}";
      final String json_input =
          "{"
              + "\"dataUsageStatement\": \"data usage is allowed.\","
              + "\"id\": null,"
              + "\"createdAt\": null,"
              + "\"endTime\": \"2022-04-28T10:11:12.00Z\","
              + "\"receiverId\": \"Bill-the-farmer-ID\","
              + "\"requestAllTwinResourcePermissions\": false,"
              + "\"requestFullAccess\": false,"
              + "\"startTime\": \"2020-04-28T10:11:12.00Z\","
              + "\"twinResourcePermissions\": {"
              + "\"ARABLE_AREA\": ["
              + "\"UPDATE\","
              + "\"READ\""
              + "]"
              + "}"
              + "}";
      Mockito.when(
              consentRequestServiceMock.createConsentRequest(
                  mockedConsentRequest_input, expectedAuthentication))
          .thenReturn(mockedConsentRequest_created);

      mockMvc
          .perform(
              post("/api/v1/twin-hub/consent-requests")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(json_input)
                  .with(
                      jwt()
                          .jwt(
                              builder ->
                                  builder
                                      .subject("user-123")
                                      .claim(IdTokenClaimNames.AZP, "ads-platform-frontend")
                                      .claim(StandardClaimNames.PREFERRED_USERNAME, "username")
                          )
                          .authorities(
                              new SimpleGrantedAuthority(
                                  "SCOPE_urn:ads:twin-hub:data-sovereignty:request-consent"))))
          .andExpect(status().isCreated())
          .andExpect(content().json(json_output));

      Mockito.verify(consentRequestServiceMock, Mockito.times(1))
          .createConsentRequest(mockedConsentRequest_input, expectedAuthentication);
    }

    @Test
    void whenCreateRequestUnauthenticated_thenUnauthenticated() throws Exception {
      mockMvc
          .perform(post("/api/v1/twin-hub/consent-requests"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    void whenCreateRequestWithoutScope_thenForbidden() throws Exception {
      final String json_input =
          "{"
              + "\"dataUsageStatement\": \"data usage is allowed.\","
              + "\"id\": null,"
              + "\"createdAt\": null,"
              + "\"endTime\": \"2022-04-28T10:11:12.00Z\","
              + "\"receiverId\": \"Bill-the-farmer-ID\","
              + "\"requestAllTwinResourcePermissions\": false,"
              + "\"requestFullAccess\": false,"
              + "\"requestorIdentity\": {"
              + "\"clientId\": \"ads-platform-frontend\","
              + "\"userId\": \"user-123\","
              + "\"clientName\": \"ads-platform-frontend\","
              + "\"userName\": \"user-123\""
              + "},"
              + "\"startTime\": \"2020-04-28T10:11:12.00Z\","
              + "\"twinResourcePermissions\": {"
              + "\"ARABLE_AREA\": ["
              + "\"UPDATE\","
              + "\"READ\""
              + "]"
              + "}"
              + "}";

      mockMvc
          .perform(
              post("/api/v1/twin-hub/consent-requests")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(json_input)
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
