package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.AccessLogEntry;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface AccessLogService {
  Page<AccessLogEntry> getAccessLogs(Pageable pageable, Authentication authentication);

  void logAccess(
      final Authentication authentication,
      final String twinId,
      @Nullable final String twinName,
      @Nullable final TwinResource twinResource,
      @Nullable final String twinResourcePath,
      final Action action);
}
