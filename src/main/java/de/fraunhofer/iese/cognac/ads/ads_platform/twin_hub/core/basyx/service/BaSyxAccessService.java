package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.core.basyx.service;

import de.fraunhofer.iese.cognac.ads.ads_platform.configuration.AdsPlatformConfigurationProperties;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.util.basyx.BaSyxIdUtil;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BaSyxAccessService {
  private final String aasServerUrl;
  private final IAASRegistry registry;
  private final ConnectedAssetAdministrationShellManager manager;

  @Autowired
  public BaSyxAccessService(final AdsPlatformConfigurationProperties properties) {
    this.aasServerUrl = properties.getBasyx().getAasServerUrl();
    this.registry = new AASRegistryProxy(properties.getBasyx().getRegistryUrl());
    this.manager = new ConnectedAssetAdministrationShellManager(this.registry);
  }

  public Optional<IAssetAdministrationShell> doGetAAS(final IIdentifier aasId) {
    try {
      return Optional.ofNullable(manager.retrieveAAS(aasId));
    } catch (ResourceNotFoundException e) {
      return Optional.empty();
    }
  }

  public Optional<Map<String, ISubmodel>> doGetSubmodels(final IIdentifier aasId) {
    try {
      return Optional.ofNullable(manager.retrieveSubmodels(aasId));
    } catch (ResourceNotFoundException e) {
      return Optional.empty();
    }
  }

  public Optional<ISubmodel> doGetSubmodel(final IIdentifier aasId, final IIdentifier submodelId) {
    try {
      return Optional.ofNullable(manager.retrieveSubmodel(aasId, submodelId));
    } catch (ResourceNotFoundException e) {
      return Optional.empty();
    }
  }

  public void doCreateSubmodel(final IIdentifier aasId, final Submodel submodel) throws DoesNotExistException {
    try {
      manager.createSubmodel(aasId, submodel);
    } catch (ResourceNotFoundException e) {
      throw new DoesNotExistException();
    }
  }

  public void doDeleteSubmodel(final IIdentifier aasId, final IIdentifier submodelId) throws DoesNotExistException {
    try {
      manager.deleteSubmodel(aasId, submodelId);
    } catch (ResourceNotFoundException e) {
      throw new DoesNotExistException();
    }
  }

  public Optional<AASDescriptor> doLookupAASById(final IIdentifier aasId) {
    try {
      return Optional.ofNullable(registry.lookupAAS(aasId));
    } catch (ResourceNotFoundException e) {
      return Optional.empty();
    }
  }

  public List<AASDescriptor> doLookupAll() {
    return registry.lookupAll();
  }

  public void doCreateAAS(final AssetAdministrationShell aas) {
    manager.createAAS(aas, this.aasServerUrl);
  }

  public void doDeleteAAS(final IIdentifier aasId) throws DoesNotExistException {
    try {
      manager.deleteAAS(aasId);
    } catch (ResourceNotFoundException e) {
      throw new DoesNotExistException();
    }
  }

  public Optional<ISubmodel> doGetSubmodel(final IIdentifier aasId, final String submodelIdShort) {
    return doLookupAASById(aasId)
        .flatMap(
            aasDescriptor -> aasDescriptor.getSubmodelDescriptors().stream()
                .filter(submodelDescriptor -> submodelDescriptor.getIdShort().equals(submodelIdShort))
                .findFirst()
                .map(SubmodelDescriptor::getIdentifier)
                .flatMap(smId -> doGetSubmodel(aasId, smId))
        );
  }

  public <T> Optional<T> doReadSimplePropertyValueFromSubmodel(final IIdentifier aasId, final String submodelIdShort, final String propertyIdShort, final Class<T> tClass) {
    return doReadSubmodelElementFromSubmodel(aasId, submodelIdShort, propertyIdShort, IProperty.class)
        .map(ISubmodelElement::getValue)
        .map(tClass::cast);
  }

  public <T> Optional<T> doReadSubmodelElementFromSubmodel(final IIdentifier aasId, final String submodelIdShort, final String submodelElementId, final Class<T> tClass) {
    return doGetSubmodel(aasId, submodelIdShort)
        .map(submodel -> submodel.getSubmodelElements().get(submodelElementId))
        .map(tClass::cast);
  }

  public void doCreateSubmodelIfNotExistsAndPutSubmodelElements(final String elementId, final String elementInstance, final String submodelIdShort, final List<ISubmodelElement> submodelElements) throws DoesNotExistException {
    final AASDescriptor aasDescriptor = doLookupAASById(BaSyxIdUtil.constructAASInstanceIdentifier(elementId, elementInstance)).orElseThrow(DoesNotExistException::new);
    aasDescriptor.getSubmodelDescriptors().stream()
        .filter(submodelDescriptor -> submodelDescriptor.getIdShort().equals(submodelIdShort))
        .findFirst()
        .map(ModelDescriptor::getIdentifier)
        .map(smId -> manager.retrieveSubmodel(aasDescriptor.getIdentifier(), smId))
        .ifPresentOrElse(submodel -> {
              // TODO check whether we need different update logic
//              final Map<String, ISubmodelElement> existingSubmodelElements = submodel.getSubmodelElements();
              for (final ISubmodelElement submodelElement : submodelElements) {
                submodel.addSubmodelElement(submodelElement);
//                if (existingSubmodelElements.containsKey(submodelElement.getIdShort())) {
//                  existingSubmodelElements.get(submodelElement.getIdShort()).setValue(submodelElement.getValue());
//                } else {
//                  submodel.addSubmodelElement(submodelElement);
//                }
              }
            }, () -> {
              final Submodel submodel = new Submodel(submodelIdShort, BaSyxIdUtil.constructSubmodelInstanceIdentifier(submodelIdShort, elementId, elementInstance));
              for (final ISubmodelElement submodelElement : submodelElements) {
                submodel.addSubmodelElement(submodelElement);
              }
              manager.createSubmodel(aasDescriptor.getIdentifier(), submodel);
            }
        );
  }

  public void doDeleteSubmodelElementFromSubmodel(final IIdentifier aasId, final String submodelIdShort, final String submodelElementIdShort) throws
      DoesNotExistException {
    final ISubmodel submodel = doGetSubmodel(aasId, submodelIdShort).orElseThrow(DoesNotExistException::new);
    final Map<String, ISubmodelElement> submodelElements = submodel.getSubmodelElements();
    if (submodelElements.containsKey(submodelElementIdShort)) {
      submodel.deleteSubmodelElement(submodelElementIdShort);
    } else {
      throw new DoesNotExistException();
    }
  }

}
