import { Log, UserManager } from 'oidc-client';

const AuthService = ({ config, engine }) => {
  const {
    stsAuthority, clientId, clientRoot, clientScope
  } = config;
  const { store: { dispatch }, actions: { authActions } } = engine;
  const { userLoaded, userUnloaded } = authActions;

  const settings = {
    authority: stsAuthority,
    client_id: clientId,
    redirect_uri: `${clientRoot}signin-callback.html`,
    silent_redirect_uri: `${clientRoot}silent-renew.html`,
    automaticSilentRenew: true,
    post_logout_redirect_uri: clientRoot,
    response_type: 'code',
    scope: clientScope,
    response_mode: 'query',
    monitorSession: false
  };
  const userManager = new UserManager(settings);

  Log.logger = console;
  Log.level = Log.ERROR;

  userManager.events.addUserLoaded((user) => dispatch(userLoaded(user)));
  userManager.events.addUserUnloaded(() => dispatch(userUnloaded()));
  userManager.events.addAccessTokenExpired(() => userManager.signinSilent()
    .catch(() => userManager.removeUser()));
  userManager.events.addUserSignedOut(() => userManager.removeUser());
  userManager.events.addSilentRenewError(() => userManager.removeUser());

  const getUser = () => userManager.getUser();

  const login = () => userManager.signinRedirect();

  const logout = () => userManager.signoutRedirect();

  userManager.getUser()
    .then((user) => {
      if (user) {
        dispatch(userLoaded(user));
      } else {
        dispatch(userUnloaded());
      }
    });

  return {
    getUser,
    login,
    logout
  };
};

export default AuthService;
