package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.model.TwinOwnershipStatement;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.repository.TwinOwnershipStatementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TwinOwnershipServiceImpl implements TwinOwnershipService {

  private final TwinOwnershipStatementRepository twinOwnershipStatementRepository;

  @Autowired
  public TwinOwnershipServiceImpl(final TwinOwnershipStatementRepository twinOwnershipStatementRepository) {
    this.twinOwnershipStatementRepository = twinOwnershipStatementRepository;
  }

  @Override
  @Transactional
  public void setOwnerOfTwin(final String twinId, final String ownerId) {
    final TwinOwnershipStatement twinOwnershipStatement = new TwinOwnershipStatement();
    twinOwnershipStatement.setId(UUID.randomUUID().toString());
    twinOwnershipStatement.setTwinId(twinId);
    twinOwnershipStatement.setOwnerId(ownerId);
    this.twinOwnershipStatementRepository.deleteByTwinId(twinId);
    this.twinOwnershipStatementRepository.save(twinOwnershipStatement);
  }

  @Override
  public void unsetOwnerOfTwin(final String twinId) {
    this.twinOwnershipStatementRepository.deleteByTwinId(twinId);
  }

  @Override
  public Optional<String> getOwnerOfTwin(final String twinId) {
    return twinOwnershipStatementRepository.findByTwinId(twinId).stream()
        .findFirst()
        .map(TwinOwnershipStatement::getOwnerId);
  }

  @Override
  public List<String> getTwinIdsByOwner(final String ownerId) {
    return this.twinOwnershipStatementRepository.findByOwnerId(ownerId).stream().map(TwinOwnershipStatement::getTwinId).collect(Collectors.toList());
  }
}
