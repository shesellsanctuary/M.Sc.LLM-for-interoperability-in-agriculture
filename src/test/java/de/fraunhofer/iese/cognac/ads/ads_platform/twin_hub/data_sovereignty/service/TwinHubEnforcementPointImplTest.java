package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.RequestorType;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.TimeService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.service.TwinOwnershipService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Consent;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentState;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.RequestorIdentity;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class TwinHubEnforcementPointImplTest {
  @Mock private TwinOwnershipService twinOwnershipServiceMock;
  @Mock private ConsentService consentServiceMock;
  @Mock private AccessLogService accessLogServiceMock;
  @Mock private TimeService timeServiceMock;
  @Mock private TwinNameProvider twinNameProvider;

  private TwinHubEnforcementPointImpl testSubject;

  @BeforeEach
  void setUp() {
    testSubject =
        new TwinHubEnforcementPointImpl(
            twinOwnershipServiceMock, consentServiceMock, accessLogServiceMock, timeServiceMock, twinNameProvider);
  }

  @AfterEach
  void tearDown() {
    Mockito.verifyNoMoreInteractions(twinOwnershipServiceMock);
    Mockito.verifyNoMoreInteractions(consentServiceMock);
    Mockito.verifyNoMoreInteractions(accessLogServiceMock);
    Mockito.verifyNoMoreInteractions(timeServiceMock);
  }

  @Nested
  class AllowAccessTests {

    @Test
    void givenFirstConsentDoesNotMatch_whenEnforceDataAccess_thenLogAccess()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
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
      Consent mockedConsent2 =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae2",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Alice-the-service-employee-ID",
                  "john-deere-fmis",
                  "Alice-the-service-employee-ID",
                  "john-deere-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              true,
              null,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent2, mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinResourceAccess(twinId, twinResource, twinResourcePath, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, twinResource, twinResourcePath, action);
    }

    @Test
    void whenEnforceDataAccessWithAllCorrectInputData_thenLogAccess() throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
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
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinResourceAccess(twinId, twinResource, twinResourcePath, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, twinResource, twinResourcePath, action);
    }

    @Test
    void givenConsentWithFullAccess_whenEnforceDataAccess_thenLogAccess()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              true,
              false,
              null,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinResourceAccess(twinId, twinResource, twinResourcePath, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, twinResource, twinResourcePath, action);
    }

    @Test
    void givenConsentWithWildCards_whenEnforceDataAccess_thenLogAccess() throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of("*", "*", null, null),
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
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinResourceAccess(twinId, twinResource, twinResourcePath, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, twinResource, twinResourcePath, action);
    }

    @Test
    void givenConsentWithoutStartAndEndTime_whenEnforceDataAccess_thenLogAccess()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
              null,
              null,
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("fieldTwin-1"),
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinResourceAccess(twinId, twinResource, twinResourcePath, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, twinResource, twinResourcePath, action);
    }

    @Test
    void givenConsentWithAccessToAllTwins_whenEnforceDataAccess_thenLogAccess()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              true,
              null,
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinResourceAccess(twinId, twinResource, twinResourcePath, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, twinResource, twinResourcePath, action);
    }

    @Test
    void givenConsentWithAccessToAllTwinResources_whenEnforceDataAccess_thenLogAccess()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("fieldTwin-1"),
              true,
              null,
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinResourceAccess(twinId, twinResource, twinResourcePath, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, twinResource, twinResourcePath, action);
    }

    @Test
    void givenConsentWithAccessToAllTwinResourcesAndConsentForAnotherConsentGiver_whenEnforceDataAccess_thenLogAccess()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("fieldTwin-1"),
              true,
              null,
              "data usable.",
              "additional.");
      Consent wrongMockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Billy-the-wrong-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("fieldTwin-1"),
              true,
              null,
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent, wrongMockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinResourceAccess(twinId, twinResource, twinResourcePath, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, twinResource, twinResourcePath, action);
    }
  }

  @Nested
  class DenyAccessTests {
    @Test
    void givenNotKnownOwnerOfTwin_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId)).thenReturn(Optional.empty());

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
    }

    @Test
    void givenOnlyRevokedConsent_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
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
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
    }

    @Test
    void givenNoMatchingUserId_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "wrong_user_id", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
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
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
    }

    @Test
    void givenNoMatchingClientId_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "another-basic-fmis", "Bob-the-Farmer-ID", "another-basic-fmis"),
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
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
    }

    @Test
    void givenConsentNotStarted_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2021-05-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("fieldTwin-1"),
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
    }

    @Test
    void givenConsentExpired_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2021-03-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("fieldTwin-1"),
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
    }

    @Test
    void givenNotAllowedAction_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.CREATE;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
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
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
    }

    @Test
    void givenConsentForAnotherTwin_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bob-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bob-the-Farmer-ID", "basic-fmis", "Bob-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("wrong_field_twin"),
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
    }
  }

  //TODO: class does not tests all filter cases. These are tested already in AllowAccessTests and DenyAccessTests
  @Nested
  class EntityAccessTests {
    @Test
    void givenConsentWithFullAccess_whenEnforceEntityAccess_thenAllowAccess()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID", "basic-fmis", "Bill-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              true,
              false,
              null,
              false,
              null,
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinEntityAccess(twinId, Action.READ, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, null, null, Action.READ);
    }

    @Test
    void
        givenConsentWithFullAccessAndWildCardAndWithoutStartAndEndTime_whenEnforceEntityAccess_thenAllowAccess()
            throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "some-basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of("Bill-the-Farmer-ID", "*", "Bill-the-Farmer-ID", null),
              null,
              null,
              ConsentState.ACTIVE,
              true,
              false,
              null,
              false,
              null,
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.of(twinName));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinEntityAccess(twinId, Action.READ, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, null, null, Action.READ);
    }

    @Test
    void givenConsentForWrongFields_whenEnforceEntityAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID", "basic-fmis", "Bill-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("wrong_field_id"),
              false,
              null,
              "data usable.",
              "additional.");
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      Assertions.assertThrows(
          ForbiddenException.class,
          () -> testSubject.enforceTwinEntityAccess(twinId, Action.READ, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
    }

    @Test
    void givenNoOwnerFound_whenEnforceEntityAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.empty());

      Assertions.assertThrows(
          ForbiddenException.class,
          () -> testSubject.enforceTwinEntityAccess(twinId, Action.READ, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
    }

    @Test
    void givenConsentWithFullAccess_whenEnforceEntityAccessWithCreateAction_thenAllowAccess()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "some-basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of("Bill-the-Farmer-ID", "*", "Bill-the-Farmer-ID", null),
              null,
              null,
              ConsentState.ACTIVE,
              true,
              false,
              null,
              false,
              null,
              "data usable.",
              "additional.");
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(twinNameProvider.getTwinName(twinId)).thenReturn(Optional.empty());
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinEntityAccess(twinId, Action.CREATE, authentication);

      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(twinNameProvider, Mockito.times(1)).getTwinName(twinId);
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, null, null, null, Action.CREATE);
    }

    @Test
    void
        givenClientUsesServiceAccount_whenEnforceEntityAccessWithCreateAction_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_SERVICE_ACCOUNT,
              "Bill-the-Farmer-ID",
              "basic-fmis - Service Account",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));

      Assertions.assertThrows(
          ForbiddenException.class,
          () -> testSubject.enforceTwinEntityAccess(twinId, Action.CREATE, authentication));
    }
  }

  @Nested
  class EntityCreationTests {
    @Test
    void givenConsentWithFullAccess_whenEnforceEntityCreation_thenAllowAccess()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID", "basic-fmis", "Bill-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              true,
              false,
              null,
              false,
              null,
              "data usable.",
              "additional.");
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      testSubject.enforceTwinEntityCreation(twinId, twinName, authentication);

      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(accessLogServiceMock, Mockito.times(1))
          .logAccess(authentication, twinId, twinName, null, null, Action.CREATE);
    }

    @Test
    void
    givenClientUsesServiceAccount_whenEnforceEntityCreation_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_SERVICE_ACCOUNT,
              "basic-fmis-service-account-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"),
              "basic-fmis - Service Account");

      Assertions.assertThrows(
          ForbiddenException.class,
          () -> testSubject.enforceTwinEntityCreation(twinId, twinName, authentication));
    }

    @Test
    void givenNoFullAccess_whenEnforceTwinEntityCreation_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "basic-fmis",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Consent mockedConsent =
          Consent.of(
              "b0573a68-841d-41c7-8d2e-629bf9292ae1",
              Instant.parse("2022-06-17T07:46:53.971086400Z"),
              "Bill-the-Farmer-ID",
              RequestorIdentity.of(
                  "Bill-the-Farmer-ID", "basic-fmis", "Bill-the-Farmer-ID", "basic-fmis"),
              Instant.parse("2020-04-28T10:11:12.00Z"),
              Instant.parse("2022-04-28T10:11:12.00Z"),
              ConsentState.ACTIVE,
              false,
              false,
              Set.of("some_field_id"),
              false,
              Collections.singletonMap(TwinResource.ARABLE_AREA, Set.of(Action.READ, Action.UPDATE)),
              "data usable.",
              "additional.");
      Mockito.when(consentServiceMock.getConsentsByConsentGiverId("Bill-the-Farmer-ID"))
          .thenReturn(List.of(mockedConsent));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2021-04-28T10:11:12.00Z"));

      Assertions.assertThrows(
          ForbiddenException.class,
          () -> testSubject.enforceTwinEntityCreation(twinId, twinName, authentication));

      Mockito.verify(consentServiceMock, Mockito.times(1))
          .getConsentsByConsentGiverId("Bill-the-Farmer-ID");
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
    }
  }

  @Nested
  class TwinHubFrontendExemptionTests {
    @Test
    void givenTwinIsOwnedByPresentUser_whenEnforceDataAccess_thenDoNothing()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));

      testSubject.enforceTwinResourceAccess(
          twinId, twinResource, twinResourcePath, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verifyNoInteractions(accessLogServiceMock);
    }

    @Test
    void givenTwinIsOwnedByPresentUser_whenEnforceEntityAccess_thenDoNothing()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));

      testSubject.enforceTwinEntityAccess(twinId, action, authentication);

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
      Mockito.verifyNoInteractions(accessLogServiceMock);
    }

    @Test
    void givenTwinHubFrontendAuthenticated_whenEnforceEntityCreation_thenDoNothing()
        throws ForbiddenException {
      String twinId = "fieldTwin-1";
      String twinName = "fieldTwinName";
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));

      testSubject.enforceTwinEntityCreation(twinId, twinName, authentication);

      Mockito.verifyNoInteractions(accessLogServiceMock);
    }

    @Test
    void givenTwinIsOwnedByAnotherUser_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "not-Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
    }

    @Test
    void givenTwinIsOwnedByAnotherUser_whenEnforceEntityAccess_thenThrowForbiddenException() {
      String twinId = "fieldTwin-1";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "not-Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.of("Bill-the-Farmer-ID"));

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinEntityAccess(
                  twinId, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
    }

    @Test
    void givenTwinOwnerIsUnknown_whenEnforceDataAccess_thenThrowForbiddenException() {
      String twinId = "not-fieldTwin-1";
      TwinResource twinResource = TwinResource.ARABLE_AREA;
      String twinResourcePath = "/GEOMETRIES/arable-data";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId)).thenReturn(Optional.empty());

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinResourceAccess(
                  twinId, twinResource, twinResourcePath, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
    }

    @Test
    void givenTwinOwnerIsUnknown_whenEnforceEntityAccess_thenThrowForbiddenException() {
      String twinId = "not-fieldTwin-1";
      Action action = Action.READ;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:twins:read"));
      Mockito.when(twinOwnershipServiceMock.getOwnerOfTwin(twinId))
          .thenReturn(Optional.empty());

      Assertions.assertThrows(
          ForbiddenException.class,
          () ->
              testSubject.enforceTwinEntityAccess(
                  twinId, action, authentication));

      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getOwnerOfTwin(twinId);
    }

    @Test
    void whenEnforceEntityAccessForCreatingATwin_thenDoNothing() throws ForbiddenException {
      String twinId = "to-be-created-fieldTwin-1";
      Action action = Action.CREATE;
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "Bill-the-Farmer-ID",
              "ads-platform-frontend",
              List.of("SCOPE_urn:ads:twin-hub:twins:manage"));

      testSubject.enforceTwinEntityAccess(twinId, action, authentication);

      Mockito.verifyNoInteractions(accessLogServiceMock);
    }
  }
}

