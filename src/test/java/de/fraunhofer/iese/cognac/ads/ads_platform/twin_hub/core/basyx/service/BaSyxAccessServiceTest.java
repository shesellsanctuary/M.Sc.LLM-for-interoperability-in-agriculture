package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.basyx.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.util.basyx.BaSyxIdUtil;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class BaSyxAccessServiceTest {

  @Autowired
  BaSyxAccessService testSubject;

  @Test
  void givenAASDoesNotExist_whenDoGetAAS_thenReturnEmptyOptional() {
    final Optional<IAssetAdministrationShell> aas = testSubject.doGetAAS(new ModelUrn("urn:me-does-not-exist"));
    Assertions.assertTrue(aas.isEmpty());
  }

  @Test
  void givenAASDoesExist_whenDoGetAAS_thenReturnAAS() {
    final IIdentifier aasId = BaSyxIdUtil.constructAASInstanceIdentifier("test", "1");
    {
      final Asset asset = new Asset("testAsset", BaSyxIdUtil.constructAssetInstanceIdentifier("test", "1"), AssetKind.INSTANCE);
      final AssetAdministrationShell aas = new AssetAdministrationShell("testAas", aasId, asset);
      testSubject.doCreateAAS(aas);
    }

    final Optional<IAssetAdministrationShell> aas = testSubject.doGetAAS(aasId);
    Assertions.assertTrue(aas.isPresent());
    final IAssetAdministrationShell shell = aas.get();
    Assertions.assertEquals("testAsset", shell.getAsset().getIdShort());
  }

}