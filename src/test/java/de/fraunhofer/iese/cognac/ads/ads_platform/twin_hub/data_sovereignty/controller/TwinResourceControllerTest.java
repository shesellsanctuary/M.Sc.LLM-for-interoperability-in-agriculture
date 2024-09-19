package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TwinResourceController.class)
@ActiveProfiles("test")
public class TwinResourceControllerTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void whenGetTwinResources_thenGetData() throws Exception {
    final String json_output =
        "["
            + "\"TRACKS\","
            + "\"ARABLE_AREA\","
            + "\"TRAFFICABLE_AREA\","
            + "\"NON_TRAFFICABLE_AREA\","
            + "\"WORK_RECORDS_FERTILIZATION\","
            + "\"WORK_RECORDS_WEED_CONTROL\","
            + "\"WORK_RECORDS_SEEDING\","
            + "\"WORK_RECORDS_HARVESTING\","
            + "\"WORK_TIME_ESTIMATES\","
            + "\"CROP_MATURITY_FORECASTS\","
            + "\"RECOMMENDATIONS\","
            + "\"SOIL_COMPOSITION_OBSERVATIONS\","
            + "\"SOIL_NUTRIENTS_OBSERVATIONS\","
            + "\"SOIL_MEASUREMENT_WORK_ORDERS\","
            + "\"SOIL_MEASUREMENT_WORK_RECORDS\","
            + "\"VEGETATION_INDEX_OBSERVATIONS\","
            + "\"CROP_MATURITY_OBSERVATIONS\","
            + "\"NITROGEN_OUTGASSING_OBSERVATIONS\","
            + "\"DATA_FILES\","
            + "\"PLANT_OBSERVATIONS\""
            + "]";
    mockMvc
        .perform(
            get("/api/v1/twin-hub/twin-resources")
                .with(
                    jwt()
                        .jwt(
                            builder ->
                                builder
                                    .subject("user-123")
                                    .claim(IdTokenClaimNames.AZP, "ads-platform-frontend"))))
        .andExpect(status().isOk())
        .andExpect(content().json(json_output));
  }

  @Test
  void whenGetTwinResourcesUnauthenticated_thenUnauthenticated() throws Exception {
    mockMvc.perform(get("/api/v1/twin-hub/twin-resources")).andExpect(status().isUnauthorized());
  }
}
