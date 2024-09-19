package de.fraunhofer.iese.cognac.ads.ads_platform.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class DateTimeStampUtil {
  private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  public static String now() {
    return transformInstantToString(Instant::now);
  }

  private static String transformInstantToString(Supplier<Instant> instantSupplier) {
    return dateTimeFormatter.format(ZonedDateTime.ofInstant(instantSupplier.get(), ZoneOffset.UTC));
  }
}
