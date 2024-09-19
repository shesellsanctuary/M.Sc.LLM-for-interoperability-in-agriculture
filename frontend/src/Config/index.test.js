import Config from '.';

const EXPECTED_DEFAULT_ADS_PLATFORM_BASE_URL = 'http://localhost:8080/api/v1';
const EXPECTED_DEFAULT_STS_AUTHORITY = 'http://localhost:8080/auth/realms/dev.ads-platform.de/';
const EXPECTED_DEFAULT_CLIENT_ID = 'client-id';
const EXPECTED_DEFAULT_CLIENT_SCOPE = 'openid profile urn:ads:twin-hub:twins:read urn:ads:twin-hub:twins:write urn:ads:twin-hub:twins:manage urn:ads:twin-hub:data-sovereignty:manage';
const EXPECTED_DEFAULT_CLIENT_ROOT = 'http://localhost:8080/';

describe('Config', () => {
  const uiLocation = { origin: 'http://localhost:8080', pathname: '/' };

  it('no appEnv', () => {
    const config = Config({ uiLocation });
    expect(config).toEqual({
      adsPlatformBaseUrl: EXPECTED_DEFAULT_ADS_PLATFORM_BASE_URL,
      stsAuthority: EXPECTED_DEFAULT_STS_AUTHORITY,
      clientId: EXPECTED_DEFAULT_CLIENT_ID,
      clientScope: EXPECTED_DEFAULT_CLIENT_SCOPE,
      clientRoot: EXPECTED_DEFAULT_CLIENT_ROOT
    });
  });

  it('empty appEnv', () => {
    const appEnv = {};
    const config = Config({ appEnv, uiLocation });
    expect(config)
      .toEqual({
        adsPlatformBaseUrl: EXPECTED_DEFAULT_ADS_PLATFORM_BASE_URL,
        stsAuthority: EXPECTED_DEFAULT_STS_AUTHORITY,
        clientId: EXPECTED_DEFAULT_CLIENT_ID,
        clientScope: EXPECTED_DEFAULT_CLIENT_SCOPE,
        clientRoot: EXPECTED_DEFAULT_CLIENT_ROOT
      });
  });

  it('appEnv no object', () => {
    const appEnv = 'Hallo Welt';
    const config = Config({ appEnv, uiLocation });
    expect(config)
      .toEqual({
        adsPlatformBaseUrl: EXPECTED_DEFAULT_ADS_PLATFORM_BASE_URL,
        stsAuthority: EXPECTED_DEFAULT_STS_AUTHORITY,
        clientId: EXPECTED_DEFAULT_CLIENT_ID,
        clientScope: EXPECTED_DEFAULT_CLIENT_SCOPE,
        clientRoot: EXPECTED_DEFAULT_CLIENT_ROOT
      });
  });

  it('appEnv set', () => {
    const appEnv = {
      adsPlatformBaseUrl: 'baseUrl',
      stsAuthority: 'authorityUrl'
    };
    const config = Config({ appEnv, uiLocation });
    expect(config)
      .toEqual({
        adsPlatformBaseUrl: 'baseUrl',
        stsAuthority: 'authorityUrl',
        clientId: EXPECTED_DEFAULT_CLIENT_ID,
        clientScope: EXPECTED_DEFAULT_CLIENT_SCOPE,
        clientRoot: EXPECTED_DEFAULT_CLIENT_ROOT
      });
  });
});
