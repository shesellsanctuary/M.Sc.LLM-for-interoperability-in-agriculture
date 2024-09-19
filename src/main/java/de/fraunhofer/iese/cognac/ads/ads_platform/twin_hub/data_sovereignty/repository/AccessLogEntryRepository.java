package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.repository;

import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.AccessLogEntry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface AccessLogEntryRepository extends MongoRepository<AccessLogEntry, String> {
  Page<AccessLogEntry> findByTwinIdIn(Collection<String> twinIds, Pageable pageable);
}
