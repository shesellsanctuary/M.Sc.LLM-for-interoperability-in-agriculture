package de.fraunhofer.iese.cognac.ads.ads_platform.service;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TimeServiceImpl implements TimeService {
  @Override
  public Instant getCurrentTime() {
    return Instant.now();
  }
}
