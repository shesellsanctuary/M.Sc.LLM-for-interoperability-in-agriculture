package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.repository;

import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Consent;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConsentRepository extends MongoRepository<Consent, String> {
  List<Consent> findByConsentGiverIdOrderByCreatedAtDesc(String consentGiverId);

  Optional<Consent> findByIdAndConsentGiverId(String id, String consentGiverId);
}
