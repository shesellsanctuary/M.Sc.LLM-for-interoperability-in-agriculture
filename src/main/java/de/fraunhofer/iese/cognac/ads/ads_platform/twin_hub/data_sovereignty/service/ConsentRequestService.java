package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequest;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequestAnswer;

import java.util.List;
import java.util.Optional;

public interface ConsentRequestService {
  List<ConsentRequest> getConsentRequests(Authentication authentication);

  ConsentRequest createConsentRequest(ConsentRequest consentRequest, Authentication authentication);

  Optional<ConsentRequest> getConsentRequest(String consentRequestId, Authentication authentication);

  void answerConsentRequest(String consentRequestId, ConsentRequestAnswer consentRequestAnswer, Authentication authentication) throws DoesNotExistException;
}
