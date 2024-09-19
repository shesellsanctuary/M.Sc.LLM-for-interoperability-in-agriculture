package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.IdentifierService;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.TimeService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.service.TwinOwnershipService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.AccessLogEntry;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.repository.AccessLogEntryRepository;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.util.RequestorIdentityUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessLogServiceImpl implements AccessLogService {
  private static final Logger logger = LoggerFactory.getLogger(AccessLogServiceImpl.class);

  private final AccessLogEntryRepository accessLogRepository;
  private final TwinOwnershipService twinOwnershipService;
  private final TimeService timeService;
  private final IdentifierService identifierService;

  @Autowired
  public AccessLogServiceImpl(
      final AccessLogEntryRepository accessLogRepository,
      final TwinOwnershipService twinOwnershipService,
      final TimeService timeService,
      final IdentifierService identifierService) {
    this.accessLogRepository = accessLogRepository;
    this.twinOwnershipService = twinOwnershipService;
    this.timeService = timeService;
    this.identifierService = identifierService;
  }

  @Override
  public Page<AccessLogEntry> getAccessLogs(Pageable pageable, final Authentication authentication) {
    final List<String> twinsOwnedByUser =
        twinOwnershipService.getTwinIdsByOwner(authentication.getSubject());
    return accessLogRepository.findByTwinIdIn(twinsOwnedByUser, pageable);
  }

  @Override
  public void logAccess(
      final Authentication authentication,
      final String twinId,
      @Nullable final String twinName,
      @Nullable final TwinResource twinResource,
      @Nullable final String twinResourcePath,
      final Action action) {
    final AccessLogEntry entry =
        AccessLogEntry.of(
            identifierService.generateRandomUuid().toString(),
            timeService.getCurrentTime(),
            RequestorIdentityUtil.determineRequestorIdentity(authentication),
            twinId,
            twinName,
            twinResource,
            twinResourcePath,
            action);
    logger.debug("logAccess: {}", entry);
    this.accessLogRepository.save(entry);
  }

}
