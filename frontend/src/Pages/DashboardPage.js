import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const DashboardPage = () => {
  const { t } = useTranslation();
  return (
    <div className="dashboard container">
      <Link className="dashboard-element" to="/fields">
        <span className="element-title">{t('dashboard.manageTwins.title')}</span>
        {/* <span className="element-description">
            {t('dashboard.manageTwins.description')}</span> */}
      </Link>
      <Link className="dashboard-element" to="/consents">
        <span className="element-title">{t('dashboard.manageConsents.title')}</span>
        {/* <span className="element-description">
            {t('dashboard.manageConsents.description')}</span> */}
      </Link>
      <Link className="dashboard-element" to="/access-logs">
        <span className="element-title">{t('dashboard.viewAccessLogs.title')}</span>
        {/* <span className="element-description">
            {t('dashboard.viewAccessLogs.description')}</span> */}
      </Link>
    </div>
  );
};

export default DashboardPage;
