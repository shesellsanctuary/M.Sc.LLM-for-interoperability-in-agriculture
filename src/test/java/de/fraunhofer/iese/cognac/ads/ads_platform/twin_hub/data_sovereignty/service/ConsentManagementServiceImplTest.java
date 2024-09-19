package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.RequestorType;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.IdentifierService;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.TimeService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Consent;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequest;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequestAnswer;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequestDecision;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentState;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.RequestorIdentity;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.repository.ConsentRepository;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.repository.ConsentRequestRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ConsentManagementServiceImplTest {

  @Mock private ConsentRepository consentRepositoryMock;
  @Mock private ConsentRequestRepository consentRequestRepositoryMock;
  @Mock private TimeService timeServiceMock;
  @Mock private IdentifierService identifierServiceMock;

  private ConsentManagementServiceImpl testSubject;

  @BeforeEach
  void setUp() {
    testSubject =
        new ConsentManagementServiceImpl(
            consentRepositoryMock,
            consentRequestRepositoryMock,
            timeServiceMock,
            identifierServiceMock);
  }

  @AfterEach
  void tearDown() {
    Mockito.verifyNoMoreInteractions(consentRepositoryMock);
    Mockito.verifyNoMoreInteractions(consentRequestRepositoryMock);
    Mockito.verifyNoMoreInteractions(timeServiceMock);
    Mockito.verifyNoMoreInteractions(identifierServiceMock);
  }

  @Nested
  class GetConsentTests {
    @Test
    void givenRepositoryFindsConsent_whenGetConsent_thenReturnConsent() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend",
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend"),
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
      Mockito.when(
              consentRepositoryMock.findByIdAndConsentGiverId(
                  "b0573a68-841d-41c7-8d2e-629bf9292ae1", "Bill-the-Farmer-ID"))
          .thenReturn(Optional.of(mockedConsent));

      Optional<Consent> consent_returned =
          testSubject.getConsent("b0573a68-841d-41c7-8d2e-629bf9292ae1", authentication);

      Assertions.assertTrue(consent_returned.isPresent());
      Assertions.assertEquals(mockedConsent, consent_returned.get());
      Mockito.verify(consentRepositoryMock, Mockito.times(1))
          .findByIdAndConsentGiverId("b0573a68-841d-41c7-8d2e-629bf9292ae1", "Bill-the-Farmer-ID");
    }
  }

  @Nested
  class RevokeConsentTests {
    @Test
    void givenRepositoryFindsConsent_whenRevokeConsent_thenConsentRevoked()
        throws DoesNotExistException {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend",
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend"),
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
      Consent mockedConsent_revoked =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend",
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.REVOKED,
              false,
              false,
              Set.of("fieldTwin-1"),
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      Mockito.when(
              consentRepositoryMock.findByIdAndConsentGiverId(
                  "b0573a68-841d-41c7-8d2e-629bf9292ae1", "Bill-the-Farmer-ID"))
          .thenReturn(Optional.of(mockedConsent));

      testSubject.revokeConsent("b0573a68-841d-41c7-8d2e-629bf9292ae1", authentication);

      Mockito.verify(consentRepositoryMock, Mockito.times(1))
          .findByIdAndConsentGiverId("b0573a68-841d-41c7-8d2e-629bf9292ae1", "Bill-the-Farmer-ID");
      Mockito.verify(consentRepositoryMock, Mockito.times(1)).save(mockedConsent_revoked);
    }

    @Test
    void givenConsentDoesNotExist_whenRevokeConsent_thenThrowDoesNotExistException() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      Mockito.when(
              consentRepositoryMock.findByIdAndConsentGiverId(
                  "b0573a68-841d-41c7-8d2e-629bf9292ae1", "Bill-the-Farmer-ID"))
          .thenReturn(Optional.empty());

      Assertions.assertThrows(
          DoesNotExistException.class,
          () -> testSubject.revokeConsent("b0573a68-841d-41c7-8d2e-629bf9292ae1", authentication));

      Mockito.verify(consentRepositoryMock, Mockito.times(1))
          .findByIdAndConsentGiverId("b0573a68-841d-41c7-8d2e-629bf9292ae1", "Bill-the-Farmer-ID");
    }
  }

  @Nested
  class CreateConsentRequestTests {
    @Test
    void
        givenRepositoryCreatesConsentRequest_whenCreateConsentRequest_thenIsConsentRequestCreated() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:request-consent"),
              "bill");
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
      ConsentRequest expected_consentRequest =
          ConsentRequest.of(
              "980cedfd-1cf1-4fd8-a45f-65a7196ea217",
              Instant.parse("2021-04-28T10:11:12.00Z"),
              "Bill-the-farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID", "ads-platform-frontend", "bill", "ads-platform-frontend"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              false,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usage is allowed.");
      ArgumentCaptor<ConsentRequest> captor = ArgumentCaptor.forClass(ConsentRequest.class);
      Mockito.when(consentRequestRepositoryMock.save(ArgumentMatchers.any(ConsentRequest.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));
      Mockito.when(identifierServiceMock.generateRandomUuid())
          .thenReturn(UUID.fromString("980cedfd-1cf1-4fd8-a45f-65a7196ea217"));

      ConsentRequest actual_consentRequest =
          testSubject.createConsentRequest(mockedConsentRequest_input, authentication);

      Assertions.assertEquals(expected_consentRequest, actual_consentRequest);
      Mockito.verify(consentRequestRepositoryMock, Mockito.times(1)).save(captor.capture());
      Assertions.assertEquals(expected_consentRequest, captor.getValue());
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(identifierServiceMock, Mockito.times(1)).generateRandomUuid();
    }
  }

  @Nested
  class GetConsentRequestsTests {
    @Test
    void givenRepositoryFindsConsentRequests_whenGetConsentRequests_thenReturnConsentRequests() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      List<ConsentRequest> mockedRequests =
          new ArrayList<>(
              List.of(
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
                      Collections.singletonMap(
                          TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
                      "data usage is allowed.")));
      Mockito.when(consentRequestRepositoryMock.findByReceiverIdOrderByCreatedAtDesc("Bill-the-Farmer-ID"))
          .thenReturn(mockedRequests);

      List<ConsentRequest> list = testSubject.getConsentRequests(authentication);

      Assertions.assertNotNull(list);
      Assertions.assertEquals(mockedRequests, list);
      Mockito.verify(consentRequestRepositoryMock, Mockito.times(1))
          .findByReceiverIdOrderByCreatedAtDesc("Bill-the-Farmer-ID");
    }
  }

  @Nested
  class GetConsentRequestTests {
    @Test
    void givenRepositoryFindsConsentRequest_whenGetConsentRequest_thenReturnConsentRequest() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
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
      Mockito.when(
              consentRequestRepositoryMock.findByIdAndReceiverId(
                  "980cedfd-1cf1-4fd8-a45f-65a7196ea217", "Bill-the-Farmer-ID"))
          .thenReturn(Optional.of(mockedConsentRequest));

      Optional<ConsentRequest> consentRequest_returned =
          testSubject.getConsentRequest("980cedfd-1cf1-4fd8-a45f-65a7196ea217", authentication);

      Assertions.assertTrue(consentRequest_returned.isPresent());
      Assertions.assertEquals(
          "980cedfd-1cf1-4fd8-a45f-65a7196ea217", consentRequest_returned.get().getId());
      Assertions.assertEquals(
          Instant.parse("2022-06-17T07:46:53.971086400Z"),
          consentRequest_returned.get().getCreatedAt());
      Assertions.assertEquals("Bill-the-farmer-ID", consentRequest_returned.get().getReceiverId());
      Assertions.assertEquals(
          RequestorIdentity.of(
              "user-123", "ads-platform-frontend", "user-123", "ads-platform-frontend"),
          consentRequest_returned.get().getRequestorIdentity());
      Assertions.assertEquals(
          Instant.parse("2020-04-28T10:11:12.00Z"), consentRequest_returned.get().getStartTime());
      Assertions.assertEquals(
          Instant.parse("2022-04-28T10:11:12.00Z"), consentRequest_returned.get().getEndTime());
      Assertions.assertFalse(consentRequest_returned.get().isRequestFullAccess());
      Assertions.assertFalse(consentRequest_returned.get().isRequestAllTwinResourcePermissions());
      Assertions.assertEquals(
          Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
          consentRequest_returned.get().getTwinResourcePermissions());
      Assertions.assertEquals(
          "data usage is allowed.", consentRequest_returned.get().getDataUsageStatement());
      Mockito.verify(consentRequestRepositoryMock, Mockito.times(1))
          .findByIdAndReceiverId("980cedfd-1cf1-4fd8-a45f-65a7196ea217", "Bill-the-Farmer-ID");
    }
  }

  @Nested
  class AnswerConsentRequestTest {
    @Test
    void
        givenConsentRequestExists_whenAcceptConsentRequest_thenConsentIsCreatedAndConsentRequestIsDeleted()
            throws DoesNotExistException {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      ConsentRequest mockedConsentRequest =
          ConsentRequest.of(
              "980cedfd-1cf1-4fd8-a45f-65a7196ea217",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend",
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              false,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.");
      Consent expected_consent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae0",
              Instant.parse("2021-04-28T10:11:12.00Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend",
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend"),
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
      ConsentRequestAnswer mockedConsentRequestAnswer =
          ConsentRequestAnswer.of(
              ConsentRequestDecision.ACCEPT, false, Set.of("fieldTwin-1"), "additional.");
      ArgumentCaptor<Consent> captor = ArgumentCaptor.forClass(Consent.class);
      Mockito.when(
              consentRequestRepositoryMock.findByIdAndReceiverId(
                  "980cedfd-1cf1-4fd8-a45f-65a7196ea217", "Bill-the-Farmer-ID"))
          .thenReturn(Optional.of(mockedConsentRequest));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));
      Mockito.when(identifierServiceMock.generateRandomUuid())
          .thenReturn(UUID.fromString("b0573a68-841d-41c7-8d2e-629bf9292ae0"));

      testSubject.answerConsentRequest(
          "980cedfd-1cf1-4fd8-a45f-65a7196ea217", mockedConsentRequestAnswer, authentication);

      Mockito.verify(consentRepositoryMock, Mockito.times(1)).save(captor.capture());
      Mockito.verify(consentRequestRepositoryMock, Mockito.times(1))
          .deleteById("980cedfd-1cf1-4fd8-a45f-65a7196ea217");
      Assertions.assertEquals(expected_consent, captor.getValue());
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(identifierServiceMock, Mockito.times(1)).generateRandomUuid();
    }

    @Test
    void givenConsentRequestExists_whenDeclineConsentRequest_thenConsentRequestIsDeleted()
        throws DoesNotExistException {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      ConsentRequest mockedConsentRequest =
          ConsentRequest.of(
              "980cedfd-1cf1-4fd8-a45f-65a7196ea217",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend",
                  "Bill-the-Farmer-ID",
                  "ads-platform-frontend"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              false,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usage is allowed.");
      ConsentRequestAnswer mockedConsentRequestAnswer =
          ConsentRequestAnswer.of(
              ConsentRequestDecision.DECLINE, false, Set.of("fieldTwin-1"), "additional.");
      Mockito.when(
              consentRequestRepositoryMock.findByIdAndReceiverId(
                  "980cedfd-1cf1-4fd8-a45f-65a7196ea217", "Bill-the-Farmer-ID"))
          .thenReturn(Optional.of(mockedConsentRequest));

      testSubject.answerConsentRequest(
          "980cedfd-1cf1-4fd8-a45f-65a7196ea217", mockedConsentRequestAnswer, authentication);

      Mockito.verify(consentRequestRepositoryMock, Mockito.times(1))
          .findByIdAndReceiverId("980cedfd-1cf1-4fd8-a45f-65a7196ea217", "Bill-the-Farmer-ID");
      Mockito.verify(consentRequestRepositoryMock, Mockito.times(1))
          .deleteById("980cedfd-1cf1-4fd8-a45f-65a7196ea217");
    }

    @Test
    void givenConsentRequestDoesNotExist_whenAcceptConsentRequest_thenThrowDoesNotExistException() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      Mockito.when(
              consentRequestRepositoryMock.findByIdAndReceiverId(
                  "consentRequestId_not_exist", "Bill-the-Farmer-ID"))
          .thenReturn(Optional.empty());

      Assertions.assertThrows(
          DoesNotExistException.class,
          () ->
              testSubject.answerConsentRequest(
                  "consentRequestId_not_exist", new ConsentRequestAnswer(), authentication));

      Mockito.verify(consentRequestRepositoryMock, Mockito.times(1))
          .findByIdAndReceiverId("consentRequestId_not_exist", "Bill-the-Farmer-ID");
    }
  }

  @Nested
  class GetConsentsTests {
    @Test
    void givenRepositoryReturnConsents_whenGetConsents_thenReturnConsents() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
      List<Consent> mockedConsents =
          List.of(
              Consent.of(
                  "b0573a68-841d-41c7-8d2e-629bf9292ae1",
                  Instant.parse("2022-06-17T07:46:53.971086400Z"),
                  "Bill-the-Farmer-ID",
                  RequestorIdentity.of(
                      "Bill-the-Farmer-ID",
                      "ads-platform-frontend",
                      "Bill-the-Farmer-ID",
                      "ads-platform-frontend"),
                  Instant.parse("2020-04-28T10:11:12.00Z"),
                  Instant.parse("2022-04-28T10:11:12.00Z"),
                  ConsentState.ACTIVE,
                  false,
                  false,
                  Set.of("fieldTwin-1"),
                  false,
                  Collections.singletonMap(
                      TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
                  "data usable.",
                  "additional."));
      String consentGiverId = "Bill-the-Farmer-ID";
      Mockito.when(consentRepositoryMock.findByConsentGiverIdOrderByCreatedAtDesc(consentGiverId))
          .thenReturn(mockedConsents);

      List<Consent> list = testSubject.getConsents(authentication);

      Assertions.assertEquals(1, list.size());
      Assertions.assertEquals("b0573a68-841d-41c7-8d2e-629bf9292ae1", list.get(0).getId());
      Assertions.assertEquals(
          Instant.parse("2022-06-17T07:46:53.971086400Z"), list.get(0).getCreatedAt());
      Assertions.assertEquals(consentGiverId, list.get(0).getConsentGiverId());
      Assertions.assertEquals(
          RequestorIdentity.of(
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              "Bill-the-Farmer-ID",
              "ads-platform-frontend"),
          list.get(0).getRequestorIdentity());
      Assertions.assertEquals(Instant.parse("2020-04-28T10:11:12.00Z"), list.get(0).getStartTime());
      Assertions.assertEquals(Instant.parse("2022-04-28T10:11:12.00Z"), list.get(0).getEndTime());
      Assertions.assertEquals(ConsentState.ACTIVE, list.get(0).getState());
      Assertions.assertFalse(list.get(0).isGrantFullAccess());
      Assertions.assertFalse(list.get(0).isGrantAccessToAllTwins());
      Assertions.assertEquals(Set.of("fieldTwin-1"), list.get(0).getTwinIds());
      Assertions.assertFalse(list.get(0).isGrantAllTwinResourcePermissions());
      Assertions.assertEquals(
          Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
          list.get(0).getTwinResourcePermissions());
      Assertions.assertEquals("data usable.", list.get(0).getDataUsageStatement());
      Assertions.assertEquals("additional.", list.get(0).getAdditionalNotes());
      Mockito.verify(consentRepositoryMock, Mockito.times(1)).findByConsentGiverIdOrderByCreatedAtDesc(consentGiverId);
    }

    @Test
    void givenRepositoryReturnConsents_whenGetConsentsByConsentGiverId_thenReturnConsents() {
      List<Consent> mockedConsents =
          List.of(
              Consent.of(
                  "b0573a68-841d-41c7-8d2e-629bf9292ae1",
                  Instant.parse("2022-06-17T07:46:53.971086400Z"),
                  "Bill-the-Farmer-ID",
                  RequestorIdentity.of(
                      "Bill-the-Farmer-ID",
                      "ads-platform-frontend",
                      "Bill-the-Farmer-ID",
                      "ads-platform-frontend"),
                  Instant.parse("2020-04-28T10:11:12.00Z"),
                  Instant.parse("2022-04-28T10:11:12.00Z"),
                  ConsentState.ACTIVE,
                  false,
                  false,
                  Set.of("fieldTwin-1"),
                  false,
                  Collections.singletonMap(
                      TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
                  "data usable.",
                  "additional."));
      String consentGiverId = "Bill-the-Farmer-ID";
      Mockito.when(consentRepositoryMock.findByConsentGiverIdOrderByCreatedAtDesc(consentGiverId))
          .thenReturn(mockedConsents);

      List<Consent> list = testSubject.getConsentsByConsentGiverId(consentGiverId);

      Assertions.assertEquals(1, list.size());
      Assertions.assertEquals("b0573a68-841d-41c7-8d2e-629bf9292ae1", list.get(0).getId());
      Assertions.assertEquals(
          Instant.parse("2022-06-17T07:46:53.971086400Z"), list.get(0).getCreatedAt());
      Assertions.assertEquals(consentGiverId, list.get(0).getConsentGiverId());
      Assertions.assertEquals(
          RequestorIdentity.of(
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              "Bill-the-Farmer-ID",
              "ads-platform-frontend"),
          list.get(0).getRequestorIdentity());
      Assertions.assertEquals(Instant.parse("2020-04-28T10:11:12.00Z"), list.get(0).getStartTime());
      Assertions.assertEquals(Instant.parse("2022-04-28T10:11:12.00Z"), list.get(0).getEndTime());
      Assertions.assertEquals(ConsentState.ACTIVE, list.get(0).getState());
      Assertions.assertFalse(list.get(0).isGrantFullAccess());
      Assertions.assertFalse(list.get(0).isGrantAccessToAllTwins());
      Assertions.assertEquals(Set.of("fieldTwin-1"), list.get(0).getTwinIds());
      Assertions.assertFalse(list.get(0).isGrantAllTwinResourcePermissions());
      Assertions.assertEquals(
          Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
          list.get(0).getTwinResourcePermissions());
      Assertions.assertEquals("data usable.", list.get(0).getDataUsageStatement());
      Assertions.assertEquals("additional.", list.get(0).getAdditionalNotes());
      Mockito.verify(consentRepositoryMock, Mockito.times(1)).findByConsentGiverIdOrderByCreatedAtDesc(consentGiverId);
    }
  }
}
