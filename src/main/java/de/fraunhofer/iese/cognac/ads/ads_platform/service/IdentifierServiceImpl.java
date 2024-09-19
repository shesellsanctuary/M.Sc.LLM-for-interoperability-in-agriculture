package de.fraunhofer.iese.cognac.ads.ads_platform.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdentifierServiceImpl implements IdentifierService {
  @Override
  public UUID generateRandomUuid() {
    return UUID.randomUUID();
  }
}
