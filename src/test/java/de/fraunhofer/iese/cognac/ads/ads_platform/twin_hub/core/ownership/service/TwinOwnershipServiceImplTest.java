package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.model.TwinOwnershipStatement;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.ownership.repository.TwinOwnershipStatementRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class TwinOwnershipServiceImplTest {

  private final TwinOwnershipStatementRepository repoMock = Mockito.mock(TwinOwnershipStatementRepository.class);
  private final TwinOwnershipService testSubject = new TwinOwnershipServiceImpl(repoMock);

  @Test
  void givenThereIsNoStatementForTwin_whenGetOwnerOfTwin_thenReturnEmptyOptional() {
    Mockito.when(repoMock.findByTwinId("id")).thenReturn(Collections.emptyList());

    final Optional<String> ownerOptional = testSubject.getOwnerOfTwin("id");

    Assertions.assertTrue(ownerOptional.isEmpty());
  }

  @Test
  void givenThereIsStatementForTwin_whenGetOwnerOfTwin_thenReturnOptionalWithCorrectOwnerId() {
    final TwinOwnershipStatement statement = new TwinOwnershipStatement();
    statement.setId("12");
    statement.setTwinId("id");
    statement.setOwnerId("owner");
    Mockito.when(repoMock.findByTwinId("id")).thenReturn(Collections.singletonList(statement));

    final Optional<String> ownerOptional = testSubject.getOwnerOfTwin("id");

    Assertions.assertTrue(ownerOptional.isPresent());
    Assertions.assertEquals("owner", ownerOptional.get());
  }

  @Test
  void whenUnsetOwnerOfTwin_thenCallDeleteOnRepository() {
    testSubject.unsetOwnerOfTwin("twin");

    Mockito.verify(repoMock, Mockito.times(1)).deleteByTwinId("twin");
  }

  @Test
  void whenSetOwnerOfTwinThenAllExistingOwnershipStatementsForThatTwinAreDeletedAndTheNewOneIsStored() {
    testSubject.setOwnerOfTwin("twin", "owner");

    final InOrder inOrder = Mockito.inOrder(repoMock);
    inOrder.verify(repoMock, Mockito.times(1)).deleteByTwinId("twin");
    final ArgumentCaptor<TwinOwnershipStatement> argumentCaptor = ArgumentCaptor.forClass(TwinOwnershipStatement.class);
    inOrder.verify(repoMock, Mockito.times(1)).save(argumentCaptor.capture());
    final TwinOwnershipStatement capturedElement = argumentCaptor.getValue();
    Assertions.assertNotNull(capturedElement);
    Assertions.assertEquals("twin", capturedElement.getTwinId());
    Assertions.assertEquals("owner", capturedElement.getOwnerId());

  }

  @Test
  void givenThereAreTwinsOwnedByOwnerWhenGetTwinIdsByOwnerThenReturnIdsOfThoseTwins() {

    final TwinOwnershipStatement statement = new TwinOwnershipStatement();
    statement.setId("12");
    statement.setTwinId("twin");
    statement.setOwnerId("owner");
    Mockito.when(repoMock.findByOwnerId("owner")).thenReturn(Collections.singletonList(statement));

    List<String> result = testSubject.getTwinIdsByOwner("owner");
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("twin", result.get(0));
  }

  @Test
  void givenThereAreNoTwinsOwnedByOwnerWhenGetTwinIdsByOwnerThenReturnEmptyList() {
    Mockito.when(repoMock.findByOwnerId("owner")).thenReturn(Collections.emptyList());
    List<String> result = testSubject.getTwinIdsByOwner("owner");
    Assertions.assertEquals(0, result.size());
  }
}