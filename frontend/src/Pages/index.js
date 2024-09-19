import ConsentsPageWrapper from './ConsentsPage';
import AccessLogsPageWrapper from './AccessLogsPage';
import LoginPageWrapper from './LoginPage';
import FieldAdministrationPageWrapper from './FieldAdministrationPage';
import DashboardPage from './DashboardPage';
import ConsentAdministrationPageWrapper from './ConsentAdministrationPage';
import ConsentRequestsPageWrapper from './ConsentRequestsPage';
import ConsentRequestAdministrationPageWrapper from './ConsentRequestAdministrationPage';

const Pages = ({ components, handlers }) => {
  const { adsPlatformApiHandler, authService, formatUtils } = handlers;
  const ConsentsPage = ConsentsPageWrapper({
    components,
    formatUtils,
    adsPlatformApiHandler
  });
  const AccessLogsPage = AccessLogsPageWrapper({
    components, formatUtils, adsPlatformApiHandler
  });
  const FieldAdministrationPage = FieldAdministrationPageWrapper({
    components, adsPlatformApiHandler
  });
  const LoginPage = LoginPageWrapper({ authService });
  const ConsentAdministrationPage = ConsentAdministrationPageWrapper({
    components,
    formatUtils,
    adsPlatformApiHandler
  });
  const ConsentRequestsPage = ConsentRequestsPageWrapper({ adsPlatformApiHandler, components });
  const ConsentRequestAdministrationPage = ConsentRequestAdministrationPageWrapper({
    components,
    formatUtils,
    adsPlatformApiHandler
  });

  return {
    ConsentsPage,
    AccessLogsPage,
    LoginPage,
    FieldAdministrationPage,
    DashboardPage,
    ConsentAdministrationPage,
    ConsentRequestsPage,
    ConsentRequestAdministrationPage
  };
};

export default Pages;
