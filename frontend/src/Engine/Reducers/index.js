import AuthReducer from './AuthReducer';

const Reducers = () => {
  const { reducer: auth } = AuthReducer();

  return {
    auth
  };
};

export default Reducers;
