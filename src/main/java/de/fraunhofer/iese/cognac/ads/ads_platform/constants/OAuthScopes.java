package de.fraunhofer.iese.cognac.ads.ads_platform.constants;

public interface OAuthScopes {
  String TWIN_HUB_TWINS_READ = "urn:ads:twin-hub:twins:read";
  String TWIN_HUB_TWINS_WRITE = "urn:ads:twin-hub:twins:write";
  String TWIN_HUB_TWINS_MANAGE = "urn:ads:twin-hub:twins:manage";

  String TWIN_HUB_DATA_SOVEREIGNTY_REQUEST_CONSENT = "urn:ads:twin-hub:data-sovereignty:request-consent";
  String TWIN_HUB_DATA_SOVEREIGNTY_MANAGE = "urn:ads:twin-hub:data-sovereignty:manage";

  String CONNECTOR_CONNECTOR_CREDENTIALS_MANAGE = "urn:ads:connector:connector-credentials:manage";

  String ML_HUB_LABELED_DATA_READ = "urn:ads:ml-hub:labeled-data:read";
  String ML_HUB_LABELED_DATA_WRITE = "urn:ads:ml-hub:labeled-data:write";
  String ML_HUB_ML_MODELS_READ = "urn:ads:ml-hub:ml-models:read";
  String ML_HUB_ML_MODELS_WRITE = "urn:ads:ml-hub:ml-models:write";
}
