const AuthReducer = () => {
  const initialState = {
    user: null,
    isAuthenticated: false
  };

  const reducer = (state = initialState, action) => {
    const { type, payload } = action;
    switch (type) {
      case 'USER_LOADED':
        return {
          ...state,
          user: payload,
          isAuthenticated: true
        };
      case 'USER_UNLOADED':
        return {
          ...state,
          user: null,
          isAuthenticated: false
        };
      default:
        return state;
    }
  };
  return { reducer };
};

export default AuthReducer;
