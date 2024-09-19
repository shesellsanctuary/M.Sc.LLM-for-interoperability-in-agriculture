package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.service;

import java.util.List;
import java.util.Optional;

public interface TwinOwnershipService {
  void setOwnerOfTwin(String twinId, String ownerId);

  void unsetOwnerOfTwin(String twinId);

  Optional<String> getOwnerOfTwin(String twinId);

  List<String> getTwinIdsByOwner(String ownerId);
}
