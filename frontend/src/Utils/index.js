import PrivateRoute from './PrivateRoute';
import PublicRoute from './PublicRoute';
import AuthService from './AuthService';
import formatUtils from './FormatUtils';

const Utils = ({ config, engine }) => {
  const authService = AuthService({ config, engine });
  return {
    PrivateRoute,
    PublicRoute,
    authService,
    formatUtils
  };
};

export default Utils;
