import i18n from 'i18next';

const { t } = i18n;

const trimString = (string, length = 50) => {
  if (string.length > length) {
    return `${string.substring(0, length)}...`;
  }
  return string;
};

const deDate = (options) => new Intl.DateTimeFormat('de-DE', options);

const formatDate = (date, options = { dateStyle: 'medium', timeStyle: 'short' }) => {
  try {
    return deDate(options).format(new Date(date));
  } catch (error) {
    return date;
  }
};

const getConsentPermissionLevel = ({
  grantFullAccess,
  requestFullAccess,
  grantAllTwinResourcePermissions,
  requestAllTwinResourcePermissions,
  twinResourcePermissions
}) => {
  if (grantFullAccess || requestFullAccess) {
    return t('consents.permissionLevels.fullAccess');
  }

  if (grantAllTwinResourcePermissions || requestAllTwinResourcePermissions) {
    return t('consents.permissionLevels.morePrivileged');
  }

  if (twinResourcePermissions) {
    const permssions = Object.values(twinResourcePermissions).map((actions) => {
      if (actions.find((action) => action !== 'READ')) {
        return 1;
      }
      return 0;
    });
    return permssions.find((permission) => permission !== 0)
      ? t('consents.permissionLevels.morePrivileged')
      : t('consents.permissionLevels.readOnly');
  }
  return t('consents.permissionLevels.readOnly');
};

export default {
  trimString,
  formatDate,
  getConsentPermissionLevel
};
