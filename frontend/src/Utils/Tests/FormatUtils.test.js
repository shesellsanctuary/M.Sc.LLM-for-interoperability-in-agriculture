/* eslint-disable no-shadow */
import formatUtils from '../FormatUtils';

jest.mock('i18next', () => ({
  t: (key) => key
}));

describe('Format Utils', () => {
  const { formatDate, trimString, getConsentPermissionLevel } = formatUtils;

  it('formats ISO date to Germany timezone and format', () => {
    const string = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis';
    const expectedString = 'Lorem ipsum dolor sit amet, consectetur adipiscing...';

    expect(trimString(string)).toEqual(expectedString);
  });

  it('trims given string if it has more than 50 characters and adds ellipsis to the end', () => {
    const isoDate = '2021-10-01T09:20:45.723972Z';
    const expectedDate = '01.10.2021, 11:20';
    expect(formatDate(isoDate)).toEqual(expectedDate);
  });

  describe('getConsentPermissionLevel', () => {
    const defaultConsent = {
      grantFullAccess: false,
      grantAllTwinResourcePermissions: false,
      twinResourcePermissions: null,
      requestFullAccess: null,
      requestAllTwinResourcePermissions: null
    };

    it('returns fullAccess when grantFullAccess is true', () => {
      const consent = { ...defaultConsent, grantFullAccess: true };
      expect(getConsentPermissionLevel(consent)).toEqual('consents.permissionLevels.fullAccess');
    });

    it('returns morePrivileged when grantAllTwinResourcePermissions is true', () => {
      const consent = { ...defaultConsent, grantAllTwinResourcePermissions: true };
      expect(getConsentPermissionLevel(consent)).toEqual('consents.permissionLevels.morePrivileged');
    });

    it('returns morePrivileged when any twinResource has anything other than READ permission', () => {
      const consent = {
        ...defaultConsent,
        twinResourcePermissions: {
          WORK_RECORDS_FERTILIZATION: ['READ'],
          ARABLE_AREA: [
            'UPDATE',
            'READ'
          ]
        }
      };
      expect(getConsentPermissionLevel(consent)).toEqual('consents.permissionLevels.morePrivileged');
    });

    it('returns read only when all twinResource has only READ permission', () => {
      const consent = {
        ...defaultConsent,
        twinResourcePermissions: {
          WORK_RECORDS_FERTILIZATION: ['READ'],
          ARABLE_AREA: ['READ']
        }
      };
      expect(getConsentPermissionLevel(consent)).toEqual('consents.permissionLevels.readOnly');
    });

    it('returns fullAccess when requestFullAccess is true', () => {
      const consent = { ...defaultConsent, requestFullAccess: true };
      expect(getConsentPermissionLevel(consent)).toEqual('consents.permissionLevels.fullAccess');
    });

    it('returns morePrivileged when requestAllTwinResourcePermissions is true', () => {
      const consent = { ...defaultConsent, requestAllTwinResourcePermissions: true };
      expect(getConsentPermissionLevel(consent)).toEqual('consents.permissionLevels.morePrivileged');
    });
  });
});
