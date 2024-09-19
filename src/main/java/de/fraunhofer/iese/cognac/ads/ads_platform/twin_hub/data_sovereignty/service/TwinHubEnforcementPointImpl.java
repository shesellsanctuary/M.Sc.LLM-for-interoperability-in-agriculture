package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.RequestorType;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.TimeService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.service.TwinOwnershipService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Consent;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TwinHubEnforcementPointImpl implements TwinHubEnforcementPoint {
  private static final Logger logger = LoggerFactory.getLogger(TwinHubEnforcementPointImpl.class);
  public static final String TWIN_HUB_UI_CLIENT_ID = "ads-platform-frontend";

  private final TwinOwnershipService twinOwnershipService;
  private final ConsentService consentService;
  private final AccessLogService accessLogService;
  private final TimeService timeService;
  private final TwinNameProvider twinNameProvider;

  @Autowired
  public TwinHubEnforcementPointImpl(
      final TwinOwnershipService twinOwnershipService,
      final ConsentService consentService,
      final AccessLogService accessLogService,
      final TimeService timeService,
      final TwinNameProvider twinNameProvider) {
    this.twinOwnershipService = twinOwnershipService;
    this.consentService = consentService;
    this.accessLogService = accessLogService;
    this.timeService = timeService;
    this.twinNameProvider = twinNameProvider;
  }

  @Override
  public void enforceTwinResourceAccess(
      final String twinId,
      final TwinResource twinResource,
      final String twinResourcePath,
      final Action action,
      final Authentication authentication)
      throws ForbiddenException {
    // additional check for TwinHub Frontend
    if (authentication.getClientId().equals(TWIN_HUB_UI_CLIENT_ID)) {
      if (!isTwinHubFrontendAllowedToAccess(twinId, action, twinResource, authentication)) {
        throw new ForbiddenException();
      }
      // no logging for Twin Hub UI
    } else {
      if (!isAllowedToAccessTwinResource(twinId, twinResource, action, authentication)) {
        throw new ForbiddenException();
      }
      this.accessLogService.logAccess(
          authentication,
          twinId,
          twinNameProvider.getTwinName(twinId).orElse(null),
          twinResource,
          twinResourcePath,
          action);
    }
  }

  @Override
  public void enforceTwinEntityAccess(
      final String twinId, final Action action, final Authentication authentication)
      throws ForbiddenException {
    // additional check for TwinHub Frontend
    if (authentication.getClientId().equals(TWIN_HUB_UI_CLIENT_ID)) {
      if (!isTwinHubFrontendAllowedToAccess(twinId, action, null, authentication)) {
        throw new ForbiddenException();
      }
      // no logging for Twin Hub UI
    } else {
      if (!isAllowedToAccessTwinEntity(twinId, action, authentication)) {
        throw new ForbiddenException();
      }
      this.accessLogService.logAccess(
          authentication,
          twinId,
          twinNameProvider.getTwinName(twinId).orElse(null),
          null,
          null,
          action);
    }
  }

  @Override
  public void enforceTwinEntityCreation(
      final String twinId, final String twinName, final Authentication authentication)
      throws ForbiddenException {
    // additional check for TwinHub Frontend
    if (authentication.getClientId().equals(TWIN_HUB_UI_CLIENT_ID)) {
      // intentionally empty block. No need for check as Twin Hub UI does not need a consent
      // + no logging for Twin Hub UI
    } else {
      if (!isAllowedToAccessTwinEntity(twinId, Action.CREATE, authentication)) {
        throw new ForbiddenException();
      }
      this.accessLogService.logAccess(authentication, twinId, twinName, null, null, Action.CREATE);
    }
  }

  private boolean isAllowedToAccessTwinEntity(
      final String twinId, final Action action, final Authentication authentication) {
    // To create new twins, the requestor needs to act on behalf of a user (that user will be the
    // owner of the newly created twin). Services with their own identity cannot request the
    // creation of a new twin as there is no way to manually set the owner of the twin.
    if (action == Action.CREATE) {
      if (!authentication.getRequestorType().equals(RequestorType.CLIENT_USING_USER_ACCOUNT)) {
        return false;
      }
    }
    final String ownerOfTwin;
    if(action == Action.CREATE){
      ownerOfTwin = authentication.getSubject();
    } else {
      final Optional<String> ownerOfTwinOptional = this.twinOwnershipService.getOwnerOfTwin(twinId);
      if (ownerOfTwinOptional.isEmpty()) {
        return false;
      }
      ownerOfTwin = ownerOfTwinOptional.get();
    }
    final List<Consent> consentsOfOwner =
        this.consentService.getConsentsByConsentGiverId(ownerOfTwin);
    final Optional<Consent> firstMatchingConsent =
        consentsOfOwner.parallelStream()
            .filter(consent -> consentMatchesCommonFilters(consent, ownerOfTwin, authentication))
            // consent grants full access or if read-action: consent grants access to twin
            .filter(
                consent ->
                    action == Action.READ
                        ? consent.grantsAccessToTwin(twinId)
                        : consent.isFullAccess())
            .findFirst();
    return firstMatchingConsent.isPresent();
  }

  private boolean isAllowedToAccessTwinResource(
      final String twinId,
      final TwinResource twinResource,
      final Action action,
      final Authentication authentication) {
    final Optional<String> ownerOfTwinOptional = this.twinOwnershipService.getOwnerOfTwin(twinId);
    if (ownerOfTwinOptional.isEmpty()) {
      return false;
    }
    final String ownerOfTwin = ownerOfTwinOptional.get();
    final List<Consent> consentsOfOwner =
        this.consentService.getConsentsByConsentGiverId(ownerOfTwin);
    final Optional<Consent> firstMatchingConsent =
        consentsOfOwner.parallelStream()
            .filter(consent -> consentMatchesCommonFilters(consent, ownerOfTwin, authentication))
            .filter(consent -> consent.grantsAccessToTwin(twinId))
            .filter(consent -> consent.grantsTwinResourceAction(twinResource, action))
            .findFirst();
    logger.debug("firstMatchingConsent: {}", firstMatchingConsent);
    return firstMatchingConsent.isPresent();
  }

  private boolean isTwinHubFrontendAllowedToAccess(
      final String twinId,
      final Action action,
      @Nullable final TwinResource twinResource,
      final Authentication authentication) {
    if (action == Action.CREATE && twinResource == null) {
      // edge case for creating a new field as TwinHub Frontend on behalf of farmer
      return true;
    } else {
      final Optional<String> ownerOfTwinOptional = this.twinOwnershipService.getOwnerOfTwin(twinId);
      if (ownerOfTwinOptional.isEmpty()) {
        return false;
      }
      final String ownerOfTwin = ownerOfTwinOptional.get();
      return authentication.getSubject().equals(ownerOfTwin);
    }

  }

  private boolean consentMatchesCommonFilters(
      Consent consent, String ownerOfTwin, Authentication authentication) {
    return consent.isActive()
        && consent.isGivenBy(ownerOfTwin)
        && consent.authorizesUser(authentication.getSubject())
        && consent.authorizesClient(authentication.getClientId())
        && consent.isWithinValidTime(timeService.getCurrentTime());
  }
}
