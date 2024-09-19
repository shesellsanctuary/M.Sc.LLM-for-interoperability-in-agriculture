package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.RequestorType;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper.AccessLogMapperImpl;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.AccessLogEntry;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.RequestorIdentity;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service.AccessLogService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(AccessLogController.class)
@Import(AccessLogMapperImpl.class)
@ActiveProfiles("test")
class AccessLogControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AccessLogService accessLogServiceMock;

  @AfterEach
  void teardown(){
    Mockito.verifyNoMoreInteractions(accessLogServiceMock);
  }

  @Test
  void whenGetAccessLogsWithoutAuthentication_thenUnauthorized() throws Exception {
    this.mockMvc.perform(get("/api/v1/twin-hub/access-logs")).andExpect(status().isUnauthorized());
  }

  @Test
  void whenGetAccessLogsWithoutScope_thenForbidden() throws Exception {
    this.mockMvc
        .perform(
            get("/api/v1/twin-hub/access-logs")
                .with(
                    jwt()
                        .jwt(
                            builder ->
                                builder
                                    .subject("user-123")
                                    .claim(IdTokenClaimNames.AZP, "not-ads-platform-frontend"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void givenServiceReturnsData_whenGetAccessLogs_thenReturnData() throws Exception {
    final Authentication expectedAuthentication =
        Authentication.of(
            RequestorType.CLIENT_USING_USER_ACCOUNT,
            "user-123",
            "ads-platform-frontend",
            List.of("SCOPE_urn:ads:twin-hub:data-sovereignty:manage"));
    Pageable pageable = PageRequest.of(0, 20, Sort.by("date").descending());
    List<AccessLogEntry> accessLogEntries = new ArrayList<>();
    AccessLogEntry accessLogEntry =
        AccessLogEntry.of(
            "1",
            Instant.parse("2022-04-28T00:11:12.00Z"),
            RequestorIdentity.of("userId-1", "clientId-1", "userName-1", "clientName-1"),
            "twinId-1",
            "twinName-1",
            TwinResource.ARABLE_AREA,
            "/GEOMETRIES/arable-area",
            Action.READ);
    accessLogEntries.add(accessLogEntry);

    Page<AccessLogEntry> mockedPage =
        new PageImpl<>(
            accessLogEntries,
            PageRequest.of(0, 20, Sort.by("date").descending()),
            accessLogEntries.size());
    Mockito.when(accessLogServiceMock.getAccessLogs(pageable, expectedAuthentication))
        .thenReturn(mockedPage);

    final String json =
        "{\"content\":[{\"id\":\"1\",\"date\":\"2022-04-28T00:11:12Z\",\"requestorIdentity\":{\"userId\":\"userId-1\",\"clientId\":\"clientId-1\",\"userName\":\"userName-1\",\"clientName\":\"clientName-1\"},\"twinId\":\"twinId-1\",\"twinResource\":\"ARABLE_AREA\",\"twinResourcePath\":\"/GEOMETRIES/arable-area\",\"action\":\"READ\"}],\"pageable\":{\"sort\":{\"sorted\":true,\"unsorted\":false,\"empty\":false},\"offset\":0,\"pageSize\":20,\"pageNumber\":0,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalElements\":1,\"totalPages\":1,\"size\":20,\"number\":0,\"sort\":{\"sorted\":true,\"unsorted\":false,\"empty\":false},\"first\":true,\"numberOfElements\":1,\"empty\":false}";

    this.mockMvc
        .perform(
            get("/api/v1/twin-hub/access-logs")
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
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().json(json));
    Mockito.verify(accessLogServiceMock, Mockito.times(1))
        .getAccessLogs(pageable, expectedAuthentication);
  }
}
