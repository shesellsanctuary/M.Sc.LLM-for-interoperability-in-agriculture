package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Consent;

import java.util.List;
import java.util.Optional;

public interface ConsentService {
  List<Consent> getConsents(Authentication authentication);

  Optional<Consent> getConsent(String consentId, Authentication authentication);

  void revokeConsent(String consentId, Authentication authentication) throws DoesNotExistException;

  List<Consent> getConsentsByConsentGiverId(String consentGiverId);
}
