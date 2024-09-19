package de.fraunhofer.iese.cognac.ads.ads_platform.util.basyx;

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BaSyxIdUtilTest {

  @Test
  void fromString() {
    Assertions.assertEquals("urn:de.fraunhofer:ads.cognac:AAS:1:1:elId#elInst", BaSyxIdUtil.fromString("urn:de.fraunhofer:ads.cognac:AAS:1:1:elId#elInst").getId());
  }

  @Test
  void constructAASInstanceIdentifier() {
    Assertions.assertEquals("urn:de.fraunhofer:ads.cognac:AAS:1:1:elId#elInst", BaSyxIdUtil.constructAASInstanceIdentifier("elId", "elInst").getId());
  }

  @Test
  void constructAssetInstanceIdentifier() {
    Assertions.assertEquals("urn:de.fraunhofer:ads.cognac:ASSET:1:1:elId#elInst", BaSyxIdUtil.constructAssetInstanceIdentifier("elId", "elInst").getId());
  }

  @Test
  void constructSubmodelInstanceIdentifier() {
    Assertions.assertEquals("urn:de.fraunhofer:ads.cognac:sm:1:1:elId#elInst", BaSyxIdUtil.constructSubmodelInstanceIdentifier("sm", "elId", "elInst").getId());
  }

  @Test
  void extractElementInstance() {
    Assertions.assertEquals("elInst", BaSyxIdUtil.extractElementInstance(new ModelUrn("urn:de.fraunhofer:ads.cognac:AAS:1:1:elId#elInst")));
  }

  @Test
  void whenExtractElementInstanceWithNoInstanceInIdThenThrowException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> BaSyxIdUtil.extractElementInstance(new ModelUrn("urn:de.fraunhofer:ads.cognac:AAS:1:1:elId")));
  }

  @Test
  void extractElementId() {
    Assertions.assertEquals("elId", BaSyxIdUtil.extractElementId(new ModelUrn("urn:de.fraunhofer:ads.cognac:AAS:1:1:elId#elInst")));
  }

}