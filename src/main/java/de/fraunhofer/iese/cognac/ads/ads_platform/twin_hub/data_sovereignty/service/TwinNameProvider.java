package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import java.util.Optional;

public interface TwinNameProvider {
  Optional<String> getTwinName(String twinId);
}
