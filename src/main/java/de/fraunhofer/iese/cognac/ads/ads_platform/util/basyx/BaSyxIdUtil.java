package de.fraunhofer.iese.cognac.ads.ads_platform.util.basyx;

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

public class BaSyxIdUtil {

  private static final String LEGAL_ENTITY = "de.fraunhofer";
  private static final String SUB_UNIT = "ads.cognac";
  private static final String VERSION = "1";
  private static final String REVISION = "1";
  private static final String AAS = "AAS";
  private static final String ASSET = "ASSET";

  private BaSyxIdUtil() {
  }

  public static IIdentifier fromString(final String id) {
    return new ModelUrn(id);
  }

  public static IIdentifier constructAASInstanceIdentifier(final String elementId, final String elementInstance) {
    return new ModelUrn(LEGAL_ENTITY, SUB_UNIT, AAS, VERSION, REVISION, elementId, elementInstance);
  }

  public static IIdentifier constructAssetInstanceIdentifier(final String elementId, final String elementInstance) {
    return new ModelUrn(LEGAL_ENTITY, SUB_UNIT, ASSET, VERSION, REVISION, elementId, elementInstance);
  }

  public static IIdentifier constructSubmodelInstanceIdentifier(final String subModel, final String elementId, final String elementInstance) {
    return new ModelUrn(LEGAL_ENTITY, SUB_UNIT, subModel, VERSION, REVISION, elementId, elementInstance);
  }

  public static String extractElementInstance(final IIdentifier identifier) {
    final String[] split = identifier.getId().split("#");
    if (split.length == 2) {
      return split[1];
    } else {
      throw new IllegalArgumentException("cannot extract elementInstance from " + identifier);
    }
  }

  public static String extractElementId(final IIdentifier identifier) {
    final String[] split1 = identifier.getId().split("#");
    final String[] split2 = split1[0].split(":");
    return split2[split2.length - 1];
  }
}
