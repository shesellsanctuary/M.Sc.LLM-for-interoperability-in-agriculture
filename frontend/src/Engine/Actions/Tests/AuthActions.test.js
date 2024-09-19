import AuthActions from '../AuthActions';

describe('AuthActions', () => {
  const authActions = AuthActions();

  it('should create an action to load user', () => {
    const user = 'Farmer Frank';
    const expectedAction = {
      type: 'USER_LOADED',
      payload: 'Farmer Frank'
    };
    expect(authActions.userLoaded(user)).toEqual(expectedAction);
  });

  it('should create an action to unload user', () => {
    const expectedAction = {
      type: 'USER_UNLOADED'
    };
    expect(authActions.userUnloaded()).toEqual(expectedAction);
  });
});
