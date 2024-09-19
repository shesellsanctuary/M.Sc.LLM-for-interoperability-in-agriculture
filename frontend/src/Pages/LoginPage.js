import React from 'react';
import Button from 'react-bootstrap/Button';
import { useTranslation } from 'react-i18next';
import { ReactComponent as ADSLogo } from '../Style/Icons/ads_platform_icon.svg';

const LoginPageWrapper = ({ authService }) => {
  const { login } = authService;
  const { t } = useTranslation();

  const LoginPage = () => (
    <div className="login page container">
      <div className="login-form">
        <ADSLogo className="icon" />
        <p>
          {t('login.text')}
        </p>
        <Button variant="primary" onClick={() => login()}>
          {t('login.button')}
        </Button>
      </div>
    </div>
  );

  return LoginPage;
};

export default LoginPageWrapper;
