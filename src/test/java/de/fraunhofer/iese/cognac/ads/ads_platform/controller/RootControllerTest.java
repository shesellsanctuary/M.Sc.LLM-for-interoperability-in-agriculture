package de.fraunhofer.iese.cognac.ads.ads_platform.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(RootController.class)
@ActiveProfiles("test")
class RootControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void testGetConfig() throws Exception {
    this.mockMvc.perform(get("/config.json"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.content().json("{\"stsAuthority\":\"http://auth.ads-platform-intern.iese.de/auth/realms/dev.ads-platform.de\",\"clientId\":\"ads-platform-frontend\",\"adsPlatformBaseUrl\":\"/api/v1\"}"));

  }
}