import { UserManager } from 'oidc-client';
import AuthService from '../AuthService';

jest.mock('oidc-client');
afterEach(jest.clearAllMocks);

describe('AuthService', () => {
  const config = {
    stsAuthority: 'authorityUrl',
    clientId: 'client-id',
    clientRoot: 'clientUrl',
    clientScope: 'openid '
  };

  const engine = {
    store: {
      dispatch: jest.fn()
    },
    actions: {
      authActions: {
        userLoaded: jest.fn(),
        userUnloaded: jest.fn()
      }
    }
  };

  const mockedUser = {
    access_token: 'TOKEN',
    profile: {
      name: 'Farmer Frank'
    }
  };

  function makeState(initialValue) {
    let value = initialValue;
    return [
      () => value,
      (newValue) => {
        value = newValue;
      }
    ];
  }

  const [getUserLoadedCallback, setUserLoadedCallback] = makeState(null);
  const [getUserUnloadedCallback, setUserUnloadedCallback] = makeState(null);
  const [getUserSignedOutCallback, setUserSignedOutCallback] = makeState(null);
  const [getSilentRenewErrorCallback, setSilentRenewErrorCallback] = makeState(null);
  const [getAccessTokenExpiredCallback, setAccessTokenExpiredCallback] = makeState(null);
  const userManagerMock = {
    signinRedirect: jest.fn(),
    signoutRedirect: jest.fn(),
    signinSilent: jest.fn(() => Promise.reject(new Error('test reject'))),
    removeUser: jest.fn(() => Promise.resolve()),
    getUser: jest.fn(() => Promise.resolve(null)),
    events: {
      addUserLoaded: jest.fn((theFun) => setUserLoadedCallback(theFun)),
      addUserUnloaded: jest.fn((theFun) => setUserUnloadedCallback(theFun)),
      addUserSignedOut: jest.fn((theFun) => setUserSignedOutCallback(theFun)),
      addSilentRenewError: jest.fn((theFun) => setSilentRenewErrorCallback(theFun)),
      addAccessTokenExpired: jest.fn((theFun) => setAccessTokenExpiredCallback(theFun)),
    }
  };
  const userManagerCtorMock = jest.fn(() => userManagerMock);
  UserManager.mockImplementation(userManagerCtorMock);
  const authService = AuthService({
    config,
    engine
  });

  it('userManager constructed okay', () => {
    expect(authService)
      .toBeDefined();
    expect(userManagerCtorMock)
      .toHaveBeenCalledWith({
        authority: 'authorityUrl',
        automaticSilentRenew: true,
        client_id: 'client-id',
        redirect_uri: 'clientUrlsignin-callback.html',
        silent_redirect_uri: 'clientUrlsilent-renew.html',
        post_logout_redirect_uri: 'clientUrl',
        response_type: 'code',
        scope: 'openid ',
        response_mode: 'query',
        monitorSession: false
      });
    expect(getUserLoadedCallback()).not.toBeNull();
    expect(getUserUnloadedCallback()).not.toBeNull();
    expect(getUserSignedOutCallback()).not.toBeNull();
    expect(getSilentRenewErrorCallback()).not.toBeNull();
    expect(getAccessTokenExpiredCallback()).not.toBeNull();
    expect(engine.actions.authActions.userUnloaded).toHaveBeenCalledTimes(1);
    expect(engine.store.dispatch).toHaveBeenCalledWith(engine.actions.authActions.userUnloaded());
  });

  it('test that userLoaded event listener behaves correctly', () => {
    getUserLoadedCallback()(mockedUser);
    expect(engine.actions.authActions.userLoaded).toHaveBeenCalledTimes(1);
    expect(engine.store.dispatch)
      .toHaveBeenCalledWith(engine.actions.authActions.userLoaded(mockedUser));
  });

  it('test that userUnloaded event listener behaves correctly', () => {
    getUserUnloadedCallback()();
    expect(engine.actions.authActions.userUnloaded).toHaveBeenCalledTimes(1);
    expect(engine.store.dispatch).toHaveBeenCalledWith(engine.actions.authActions.userUnloaded());
  });

  it('test that accessTokenExpired event listener behaves correctly', () => getAccessTokenExpiredCallback()()
    .then(() => {
      expect(userManagerMock.signinSilent).toHaveBeenCalledTimes(1);
      expect(userManagerMock.removeUser).toHaveBeenCalledTimes(1);
    }));

  it('test that userSignedOut event listener behaves correctly', () => getUserSignedOutCallback()()
    .then(() => {
      expect(userManagerMock.removeUser).toHaveBeenCalledTimes(1);
    }));

  it('test that silentRenewError event listener behaves correctly', () => getSilentRenewErrorCallback()()
    .then(() => {
      expect(userManagerMock.removeUser).toHaveBeenCalledTimes(1);
    }));

  it('login triggers signinRedirect routine', () => {
    authService.login();
    expect(userManagerMock.signinRedirect).toHaveBeenCalled();
  });

  it('logout triggers signoutRedirect routine', () => {
    authService.logout();
    expect(userManagerMock.signoutRedirect).toHaveBeenCalled();
  });

  it('getUser returns null user when user not logged in', () => authService.getUser()
    .then((user) => {
      expect(user).toEqual(null);
      expect(userManagerMock.getUser).toHaveBeenCalled();
    }));

  it('getUser returns user when user is logged in', () => {
    const userManagerMockLoggedIn = {
      ...userManagerMock,
      getUser: jest.fn(() => Promise.resolve(mockedUser)),
    };
    const userManagerCtorMockLoggedIn = jest.fn(() => userManagerMockLoggedIn);
    UserManager.mockImplementation(userManagerCtorMockLoggedIn);
    const authServiceLoggedIn = AuthService({
      config,
      engine
    });

    return authServiceLoggedIn.getUser()
      .then((user) => {
        expect(user).toEqual(mockedUser);
        expect(userManagerMockLoggedIn.getUser).toHaveBeenCalled();
        expect(engine.actions.authActions.userLoaded).toHaveBeenCalledTimes(1);
      });
  });
});
