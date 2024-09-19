package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.repository;

import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequest;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConsentRequestRepository extends MongoRepository<ConsentRequest, String> {
  List<ConsentRequest> findByReceiverIdOrderByCreatedAtDesc(String receiverId);

  Optional<ConsentRequest> findByIdAndReceiverId(String id, String receiverId);
}
