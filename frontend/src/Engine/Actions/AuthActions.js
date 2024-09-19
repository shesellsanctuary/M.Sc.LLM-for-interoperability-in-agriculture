const AuthActions = () => {
  const userLoaded = (payload) => ({
    type: 'USER_LOADED',
    payload
  });

  const userUnloaded = () => ({
    type: 'USER_UNLOADED',
  });

  return {
    userLoaded,
    userUnloaded
  };
};

export default AuthActions;
