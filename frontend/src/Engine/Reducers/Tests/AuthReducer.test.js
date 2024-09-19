import AuthReducer from '../AuthReducer';

describe('AuthReducer', () => {
  const { reducer: authReducer } = AuthReducer();

  it('should return the initial state', () => {
    expect(authReducer(undefined, {})).toEqual(
      {
        user: null,
        isAuthenticated: false
      }
    );
  });

  it('should handle USER_LOADED', () => {
    expect(
      authReducer([], {
        type: 'USER_LOADED',
        payload: 'Farmer Frank'
      })
    ).toEqual(
      {
        user: 'Farmer Frank',
        isAuthenticated: true
      }
    );
  });

  it('should handle USER_UNLOADED', () => {
    expect(
      authReducer([
        {
          user: 'Farmer Frank',
          isAuthenticated: true
        }
      ], {
        type: 'USER_UNLOADED'
      })
    ).toEqual(
      {
        0: {
          isAuthenticated: true,
          user: 'Farmer Frank',
        },
        isAuthenticated: false,
        user: null,
      }
    );
  });
});
