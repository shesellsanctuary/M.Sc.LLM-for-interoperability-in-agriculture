package de.fraunhofer.iese.cognac.ads.ads_platform.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IdentifierServiceImplTest {
  private final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

  private final IdentifierServiceImpl testSubject = new IdentifierServiceImpl();

  @Test
  void whenGenerateUuid_thenReturnsCorrectlyFormattedUUID() {
    String actualUuid = testSubject.generateRandomUuid().toString();
    Assertions.assertTrue(actualUuid.matches(UUID_REGEX));
  }
}
