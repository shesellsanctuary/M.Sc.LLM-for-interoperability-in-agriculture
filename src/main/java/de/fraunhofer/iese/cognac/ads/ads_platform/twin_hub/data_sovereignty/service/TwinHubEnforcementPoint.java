package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;

public interface TwinHubEnforcementPoint {

  void enforceTwinResourceAccess(
      String twinId,
      TwinResource twinResource,
      String twinResourcePath,
      Action action,
      Authentication authentication)
      throws ForbiddenException;

  void enforceTwinEntityAccess(String twinId, Action action, Authentication authentication)
      throws ForbiddenException;

  void enforceTwinEntityCreation(
      String twinId, String twinName, Authentication authentication)
      throws ForbiddenException;
}
