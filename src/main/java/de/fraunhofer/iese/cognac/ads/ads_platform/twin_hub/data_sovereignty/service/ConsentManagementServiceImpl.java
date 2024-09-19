package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.IdentifierService;
import de.fraunhofer.iese.cognac.ads.ads_platform.service.TimeService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Consent;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequest;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequestAnswer;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequestDecision;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentState;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.repository.ConsentRepository;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.repository.ConsentRequestRepository;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.util.RequestorIdentityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsentManagementServiceImpl implements ConsentService, ConsentRequestService {
  private final ConsentRepository consentRepository;
  private final ConsentRequestRepository consentRequestRepository;
  private final TimeService timeService;
  private final IdentifierService identifierService;

  @Autowired
  public ConsentManagementServiceImpl(
      final ConsentRepository consentRepository,
      final ConsentRequestRepository consentRequestRepository,
      final TimeService timeService,
      final IdentifierService identifierService) {
    this.consentRepository = consentRepository;
    this.consentRequestRepository = consentRequestRepository;
    this.timeService = timeService;
    this.identifierService = identifierService;
  }

  @Override
  public List<Consent> getConsents(final Authentication authentication) {
    return this.consentRepository.findByConsentGiverIdOrderByCreatedAtDesc(authentication.getSubject());
  }

  private void createConsent(final Consent consent, final Authentication authentication) {
    consent.setId(identifierService.generateRandomUuid().toString());
    consent.setConsentGiverId(authentication.getSubject());
    consent.setCreatedAt(timeService.getCurrentTime());
    consent.setState(ConsentState.ACTIVE);
    this.consentRepository.save(consent);
  }

  @Override
  public Optional<Consent> getConsent(final String consentId, final Authentication authentication) {
    return this.consentRepository.findByIdAndConsentGiverId(consentId, authentication.getSubject());
  }

  @Override
  public void revokeConsent(final String consentId, final Authentication authentication)
      throws DoesNotExistException {
    final Optional<Consent> consentOptional =
        this.consentRepository.findByIdAndConsentGiverId(consentId, authentication.getSubject());
    if (consentOptional.isEmpty()) {
      throw new DoesNotExistException();
    }
    final Consent consent = consentOptional.get();
    consent.setState(ConsentState.REVOKED);
    this.consentRepository.save(consent);
  }

  @Override
  public List<Consent> getConsentsByConsentGiverId(final String consentGiverId) {
    return this.consentRepository.findByConsentGiverIdOrderByCreatedAtDesc(consentGiverId);
  }

  @Override
  public List<ConsentRequest> getConsentRequests(final Authentication authentication) {
    return this.consentRequestRepository.findByReceiverIdOrderByCreatedAtDesc(authentication.getSubject());
  }

  @Override
  public ConsentRequest createConsentRequest(
      final ConsentRequest consentRequest, final Authentication authentication) {
    // TODO make sure to allow creation only for valid / existing receivers
    consentRequest.setId(identifierService.generateRandomUuid().toString());
    consentRequest.setCreatedAt(timeService.getCurrentTime());
    consentRequest.setRequestorIdentity(RequestorIdentityUtil.determineRequestorIdentity(authentication));
    return this.consentRequestRepository.save(consentRequest);
  }

  @Override
  public Optional<ConsentRequest> getConsentRequest(
      final String consentRequestId, final Authentication authentication) {
    return this.consentRequestRepository.findByIdAndReceiverId(
        consentRequestId, authentication.getSubject());
  }

  @Override
  public void answerConsentRequest(
      final String consentRequestId,
      final ConsentRequestAnswer consentRequestAnswer,
      final Authentication authentication)
      throws DoesNotExistException {
    final Optional<ConsentRequest> consentRequestOptional =
        this.consentRequestRepository.findByIdAndReceiverId(
            consentRequestId, authentication.getSubject());
    if (consentRequestOptional.isEmpty()) {
      throw new DoesNotExistException();
    }
    final ConsentRequest consentRequest = consentRequestOptional.get();
    if (consentRequestAnswer.getDecision() == ConsentRequestDecision.ACCEPT) {
      final Consent consent =
          constructConsentFromRequestAndAnswer(consentRequest, consentRequestAnswer);
      createConsent(consent, authentication);
    }
    this.consentRequestRepository.deleteById(consentRequestId);
  }

  private Consent constructConsentFromRequestAndAnswer(
      final ConsentRequest consentRequest, final ConsentRequestAnswer consentRequestAnswer) {
    final Consent consent = new Consent();
    consent.setRequestorIdentity(consentRequest.getRequestorIdentity());
    consent.setStartTime(consentRequest.getStartTime());
    consent.setEndTime(consentRequest.getEndTime());
    consent.setGrantFullAccess(consentRequest.isRequestFullAccess());
    consent.setGrantAccessToAllTwins(consentRequestAnswer.isGrantAccessToAllTwins());
    consent.setTwinIds(consentRequestAnswer.getTwinIds());
    consent.setGrantAllTwinResourcePermissions(
        consentRequest.isRequestAllTwinResourcePermissions());
    consent.setTwinResourcePermissions(consentRequest.getTwinResourcePermissions());
    consent.setDataUsageStatement(consentRequest.getDataUsageStatement());
    consent.setAdditionalNotes(consentRequestAnswer.getAdditionalNotes());
    return consent;
  }
}
