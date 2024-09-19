package de.fraunhofer.iese.cognac.ads.ads_platform.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class TimeServiceImplTest {
  private final TimeServiceImpl testSubject = new TimeServiceImpl();

  @Test
  void whenGetCurrentTime_thenReturnsApproximatelyRightTime() {
    Instant actualTime = testSubject.getCurrentTime();
    Assertions.assertTrue(actualTime.isBefore(Instant.now().plusSeconds(1)));
    Assertions.assertTrue(actualTime.isAfter(Instant.now().minusSeconds(1)));
  }
}
