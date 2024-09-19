const DEFAULT_ADS_PLATFORM_BASE_URL = 'http://localhost:8080/api/v1';
const DEFAULT_STS_AUTHORITY = 'http://localhost:8080/auth/realms/dev.ads-platform.de/';
const DEFAULT_CLIENT_ID = 'client-id';
const DEFAULT_CLIENT_SCOPE = 'openid profile urn:ads:twin-hub:twins:read urn:ads:twin-hub:twins:write urn:ads:twin-hub:twins:manage urn:ads:twin-hub:data-sovereignty:manage';

const Config = ({ appEnv, uiLocation }) => {
  appEnv = appEnv || {};
  const { origin, pathname } = uiLocation;
  return {
    adsPlatformBaseUrl: appEnv.adsPlatformBaseUrl
      || DEFAULT_ADS_PLATFORM_BASE_URL,
    stsAuthority: appEnv.stsAuthority || DEFAULT_STS_AUTHORITY,
    clientId: appEnv.clientId || DEFAULT_CLIENT_ID,
    clientScope: DEFAULT_CLIENT_SCOPE,
    clientRoot: origin + pathname
  };
};

export default Config;
