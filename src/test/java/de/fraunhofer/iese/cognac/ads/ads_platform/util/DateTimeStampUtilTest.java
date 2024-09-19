package de.fraunhofer.iese.cognac.ads.ads_platform.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class DateTimeStampUtilTest {

  @Test
  public void nowReturnsISORepresentationOfCurrentPointInTime() {
    final String result = DateTimeStampUtil.now();
    Assertions.assertNotNull(result);
    final Instant parsedInstant = Instant.parse(result);
    final String parsedToString = parsedInstant.toString();
    Assertions.assertEquals(parsedToString.substring(0, 20), result.substring(0, 20));
    Assertions.assertTrue(parsedInstant.isBefore(Instant.now().plusSeconds(1)));
    Assertions.assertTrue(parsedInstant.isAfter(Instant.now().minusSeconds(60)));
  }

}