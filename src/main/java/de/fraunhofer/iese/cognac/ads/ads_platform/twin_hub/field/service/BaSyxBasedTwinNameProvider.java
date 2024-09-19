package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.service;

import static de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.service.BaSyxBasedFieldService.BASIC_INFORMATION_SUBMODEL_ID_SHORT;
import static de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.service.BaSyxBasedFieldService.BASIC_INFORMATION_SUBMODEL_NAME_PROPERTY_ID_SHORT;

import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.basyx.service.BaSyxAccessService;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.service.TwinNameProvider;
import de.fraunhofer.iese.cognac.ads.ads_platform.util.basyx.BaSyxIdUtil;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BaSyxBasedTwinNameProvider implements TwinNameProvider {

  private final BaSyxAccessService baSyxAccessService;

  @Autowired
  public BaSyxBasedTwinNameProvider(final BaSyxAccessService baSyxAccessService) {
    this.baSyxAccessService = baSyxAccessService;
  }

  @Override
  public Optional<String> getTwinName(final String twinId) {
    return this.baSyxAccessService.doLookupAASById(BaSyxIdUtil.fromString(twinId))
        .flatMap(aasDescriptor -> baSyxAccessService.doGetSubmodel(aasDescriptor.getIdentifier(), BASIC_INFORMATION_SUBMODEL_ID_SHORT))
        .map(submodel -> submodel.getProperties().get(BASIC_INFORMATION_SUBMODEL_NAME_PROPERTY_ID_SHORT))
        .map(ISubmodelElement::getValue)
        .map(String.class::cast);

  }
}
