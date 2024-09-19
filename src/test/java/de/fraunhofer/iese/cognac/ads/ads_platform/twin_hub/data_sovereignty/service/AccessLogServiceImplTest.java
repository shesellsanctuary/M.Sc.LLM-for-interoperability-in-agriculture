package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.RequestorType;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.IdentifierService;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.TimeService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.service.TwinOwnershipService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.AccessLogEntry;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.RequestorIdentity;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.repository.AccessLogEntryRepository;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AccessLogServiceImplTest {

  @Mock private TwinOwnershipService twinOwnershipServiceMock;
  @Mock private AccessLogEntryRepository accessLogEntryRepositoryMock;
  @Mock private TimeService timeServiceMock;
  @Mock private IdentifierService identifierServiceMock;

  private AccessLogServiceImpl testSubject;

  @BeforeEach
  void setUp() {
    testSubject =
        new AccessLogServiceImpl(
            accessLogEntryRepositoryMock,
            twinOwnershipServiceMock,
            timeServiceMock,
            identifierServiceMock);
  }

  @AfterEach
  void tearDown() {
    Mockito.verifyNoMoreInteractions(twinOwnershipServiceMock);
    Mockito.verifyNoMoreInteractions(accessLogEntryRepositoryMock);
    Mockito.verifyNoMoreInteractions(timeServiceMock);
    Mockito.verifyNoMoreInteractions(identifierServiceMock);
  }

  @Nested
  class LogAccessTests {
    @Test
    void givenRepositorySavesAccessLog_whenLogAccess_thenLogAccess() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "id",
              "ads-platform-frontend",
              Collections.emptyList(),
              "username");
      AccessLogEntry expected_AccessLogEntry =
          AccessLogEntry.of(
              "eccebfa0-80cc-4143-8fda-c83a17f5820c",
              Instant.parse("2022-06-22T17:11:12.00Z"),
              RequestorIdentity.of("id", "ads-platform-frontend", "username", "ads-platform-frontend"),
              "twinId-1",
              "twinName-1",
              TwinResource.ARABLE_AREA,
              "/GEOMETRIES/arable-area",
              Action.READ);
      ArgumentCaptor<AccessLogEntry> captor = ArgumentCaptor.forClass(AccessLogEntry.class);
      Mockito.when(accessLogEntryRepositoryMock.save(ArgumentMatchers.any(AccessLogEntry.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2022-06-22T17:11:12.00Z"));
      Mockito.when(identifierServiceMock.generateRandomUuid())
          .thenReturn(UUID.fromString("eccebfa0-80cc-4143-8fda-c83a17f5820c"));

      testSubject.logAccess(
          authentication,
          "twinId-1",
          "twinName-1",
          TwinResource.ARABLE_AREA,
          "/GEOMETRIES/arable-area", Action.READ);

      Mockito.verify(accessLogEntryRepositoryMock, Mockito.times(1)).save(captor.capture());
      Assertions.assertEquals(expected_AccessLogEntry, captor.getValue());
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(identifierServiceMock, Mockito.times(1)).generateRandomUuid();
    }

    @Test
    void givenRepositorySavesAccessLog_whenLogAccessWithServiceAccount_thenLogAccess() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_SERVICE_ACCOUNT,
              "id",
              "basic-fmis",
              Collections.emptyList());
      AccessLogEntry expected_AccessLogEntry =
          AccessLogEntry.of(
              "eccebfa0-80cc-4143-8fda-c83a17f5820c",
              Instant.parse("2022-06-22T17:11:12.00Z"),
              RequestorIdentity.of(
                  "id", "basic-fmis", "basic-fmis - Service Account", "basic-fmis"),
              "twinId-1",
              "twinName-1",
              TwinResource.ARABLE_AREA,
              "/GEOMETRIES/arable-area",
              Action.READ);
      ArgumentCaptor<AccessLogEntry> captor = ArgumentCaptor.forClass(AccessLogEntry.class);
      Mockito.when(accessLogEntryRepositoryMock.save(ArgumentMatchers.any(AccessLogEntry.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2022-06-22T17:11:12.00Z"));
      Mockito.when(identifierServiceMock.generateRandomUuid())
          .thenReturn(UUID.fromString("eccebfa0-80cc-4143-8fda-c83a17f5820c"));

      testSubject.logAccess(
          authentication,
          "twinId-1",
          "twinName-1",
          TwinResource.ARABLE_AREA,
          "/GEOMETRIES/arable-area", Action.READ);

      Mockito.verify(accessLogEntryRepositoryMock, Mockito.times(1)).save(captor.capture());
      Assertions.assertEquals(expected_AccessLogEntry, captor.getValue());
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(identifierServiceMock, Mockito.times(1)).generateRandomUuid();
    }

    @Test
    void givenRepositorySavesAccessLog_whenLogAccessWithUnknownRequestor_thenLogAccess() {
      Authentication authentication =
          Authentication.of(RequestorType.UNKNOWN, "id", "basic-fmis", Collections.emptyList());
      AccessLogEntry expected_AccessLogEntry =
          AccessLogEntry.of(
              "eccebfa0-80cc-4143-8fda-c83a17f5820c",
              Instant.parse("2022-06-22T17:11:12.00Z"),
              RequestorIdentity.of("id", "basic-fmis", null, "basic-fmis"),
              "twinId-1",
              "twinName-1",
              TwinResource.ARABLE_AREA,
              "/GEOMETRIES/arable-area",
              Action.READ);
      ArgumentCaptor<AccessLogEntry> captor = ArgumentCaptor.forClass(AccessLogEntry.class);
      Mockito.when(accessLogEntryRepositoryMock.save(ArgumentMatchers.any(AccessLogEntry.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));
      Mockito.when(timeServiceMock.getCurrentTime())
          .thenReturn(Instant.parse("2022-06-22T17:11:12.00Z"));
      Mockito.when(identifierServiceMock.generateRandomUuid())
          .thenReturn(UUID.fromString("eccebfa0-80cc-4143-8fda-c83a17f5820c"));

      testSubject.logAccess(
          authentication,
          "twinId-1",
          "twinName-1",
          TwinResource.ARABLE_AREA,
          "/GEOMETRIES/arable-area", Action.READ);

      Mockito.verify(accessLogEntryRepositoryMock, Mockito.times(1)).save(captor.capture());
      Assertions.assertEquals(expected_AccessLogEntry, captor.getValue());
      Mockito.verify(timeServiceMock, Mockito.times(1)).getCurrentTime();
      Mockito.verify(identifierServiceMock, Mockito.times(1)).generateRandomUuid();
    }
  }

  @Nested
  class GetAccessLogsTests {
    @Test
    void givenRepositoryReturnsAccessLogs_whenGetAccessLogs_thenReturnData() {
      Authentication authentication =
          Authentication.of(
              RequestorType.CLIENT_USING_USER_ACCOUNT,
              "user-123",
              "ads-platform-frontend",
              Collections.emptyList());
      Pageable pageable = PageRequest.of(0, 20);
      List<AccessLogEntry> accessLogEntries =
          new ArrayList<>(
              List.of(
                  AccessLogEntry.of(
                      "1",
                      Instant.parse("2022-04-28T00:11:12.00Z"),
                      RequestorIdentity.of("userId-1", "clientId-1", "userName-1", "clientName-1"),
                      "twinId-1",
                      "twinName-1",
                      TwinResource.ARABLE_AREA,
                      "/GEOMETRIES/arable-area",
                      Action.READ)));
      final List<String> listOfTwinsOwnedByUser = List.of("twin-123");
      Mockito.when(twinOwnershipServiceMock.getTwinIdsByOwner("user-123"))
          .thenReturn(listOfTwinsOwnedByUser);
      final Page<AccessLogEntry> mockedPage =
          new PageImpl<>(accessLogEntries, PageRequest.of(0, 20), 1);
      Mockito.when(accessLogEntryRepositoryMock.findByTwinIdIn(listOfTwinsOwnedByUser, pageable))
          .thenReturn(mockedPage);

      Page<AccessLogEntry> entries = testSubject.getAccessLogs(pageable, authentication);

      Assertions.assertEquals(1, entries.getTotalPages());
      Assertions.assertEquals(1, entries.getTotalElements());
      Assertions.assertTrue(entries.get().findFirst().isPresent());
      Assertions.assertEquals("twinId-1", entries.get().findFirst().get().getTwinId());
      Assertions.assertEquals("twinName-1", entries.get().findFirst().get().getTwinName());
      Assertions.assertEquals(
          RequestorIdentity.of("userId-1", "clientId-1", "userName-1", "clientName-1"),
          entries.get().findFirst().get().getRequestorIdentity());
      Assertions.assertEquals(
          TwinResource.ARABLE_AREA, entries.get().findFirst().get().getTwinResource());
      Assertions.assertEquals(
          "/GEOMETRIES/arable-area",
          entries.get().findFirst().get().getTwinResourcePath());
      Assertions.assertEquals(Action.READ, entries.get().findFirst().get().getAction());
      Mockito.verify(twinOwnershipServiceMock, Mockito.times(1)).getTwinIdsByOwner("user-123");
      Mockito.verify(accessLogEntryRepositoryMock, Mockito.times(1))
          .findByTwinIdIn(listOfTwinsOwnedByUser, pageable);
    }
  }
}
