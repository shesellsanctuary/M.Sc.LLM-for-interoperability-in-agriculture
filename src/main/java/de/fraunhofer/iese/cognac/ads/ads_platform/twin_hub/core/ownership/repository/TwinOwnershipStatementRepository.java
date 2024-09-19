package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.repository;

import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.model.TwinOwnershipStatement;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TwinOwnershipStatementRepository extends MongoRepository<TwinOwnershipStatement, String> {

  List<TwinOwnershipStatement> findByOwnerId(String ownerId);

  List<TwinOwnershipStatement> findByTwinId(String twinId);

  void deleteByTwinId(String twinId);


}
