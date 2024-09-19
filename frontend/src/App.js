import React from 'react';
import { Provider } from 'react-redux';
import { HashRouter as Router, Switch } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import PropTypes from 'prop-types';
import { useTranslation } from 'react-i18next';
import Components from './Components';
import Infra from './Infra';
import Pages from './Pages';
import Utils from './Utils';
import Config from './Config';
import Engine from './Engine';

const App = ({ appEnv }) => {
  useTranslation();
  const config = Config({ appEnv, uiLocation: window.location });
  const engine = Engine({ storage: window.sessionStorage });
  const { store } = engine;
  const {
    PrivateRoute, PublicRoute, authService, formatUtils
  } = Utils({ config, engine });
  const { adsPlatformApiHandler } = Infra({
    config,
    store
  });
  const handlers = {
    adsPlatformApiHandler,
    authService,
    formatUtils
  };
  const components = Components({ handlers, formatUtils });
  const { Header } = components;
  const {
    ConsentsPage,
    AccessLogsPage,
    LoginPage,
    FieldAdministrationPage,
    DashboardPage,
    ConsentAdministrationPage,
    ConsentRequestsPage,
    ConsentRequestAdministrationPage
  } = Pages({
    components,
    handlers
  });

  return (
    <Provider store={store}>
      <ToastContainer />
      <Router>
        <div className="page">
          <Header />
          <Switch>
            <PrivateRoute path="/" exact Component={DashboardPage} redirect="/login" />
            <PrivateRoute path="/consents" exact Component={ConsentsPage} redirect="/" />
            <PrivateRoute path="/access-logs" Component={AccessLogsPage} redirect="/" />
            <PrivateRoute path="/fields" Component={FieldAdministrationPage} redirect="/" />
            <PublicRoute path="/login" Component={LoginPage} redirect="/" />
            <PrivateRoute path="/consents/admin/:id" Component={ConsentAdministrationPage} redirect="/" />
            <PrivateRoute path="/consent-requests" exact Component={ConsentRequestsPage} redirect="/" />
            <PrivateRoute path="/consent-requests/admin/:id" Component={ConsentRequestAdministrationPage} redirect="/" />
          </Switch>
        </div>
      </Router>
    </Provider>
  );
};

App.propTypes = {
  appEnv: PropTypes.object
};

App.defaultProps = {
  appEnv: {}
};

export default App;
