import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import { consentRequests } from '../../Mockdata';
import ConsentRequestsPageWrapper from '../ConsentRequestsPage';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Consent Requests Page', () => {
  const components = {
    Loading: jest.fn(() => <div className="loading" />),
    GenericTable: jest.fn(() => <div className="generic-table" />),
    ConsentRequestsTableRow: jest.fn(() => <div className="consent-requests-row" />),
    RouteButton: jest.fn(() => <div className="route-button" />)
  };

  const expectedTableProps = {
    headers: [
      'createdAt',
      'consents.client',
      'consents.user',
      'consents.startTime',
      'consents.endTime',
      'consents.permissionLevel'
    ],
    items: consentRequests,
    TableRowElement: components.ConsentRequestsTableRow,
  };

  describe('When data is received', () => {
    const adsPlatformApiHandler = {
      getConsentRequests: jest.fn(() => Promise.resolve(consentRequests))
    };

    const ConsentRequestsPage = ConsentRequestsPageWrapper({ components, adsPlatformApiHandler });
    const wrapper = mount(<ConsentRequestsPage />);

    const pageTitle = wrapper.find('.page-title');
    const loading = () => wrapper.find('.loading');
    const genericTable = () => wrapper.find('.generic-table').parent();

    it('renders page with initial components and fetches data', () => {
      expect(pageTitle.text()).toEqual('consents.requests.pageTitle');
      expect(loading().exists()).toEqual(true);
      expect(adsPlatformApiHandler.getConsentRequests).toHaveBeenCalledTimes(1);
    });

    it('renders table when data is received', () => {
      wrapper.update();

      expect(genericTable().props()).toEqual(expectedTableProps);
      expect(loading().exists()).toEqual(false);
    });
  });

  describe('When NO data is received', () => {
    const adsPlatformApiHandler = {
      getConsentRequests: jest.fn(() => Promise.reject())
    };

    const ConsentRequestsPage = ConsentRequestsPageWrapper({ components, adsPlatformApiHandler });

    const failWrapper = mount(<ConsentRequestsPage />);

    const expectedEmptyTableProps = { ...expectedTableProps, items: [] };

    const loading = () => failWrapper.find('.loading');
    const genericTable = () => failWrapper.find('.generic-table').parent();

    it('fetches data', () => {
      expect(adsPlatformApiHandler.getConsentRequests).toHaveBeenCalledTimes(1);
      expect(loading().exists()).toEqual(true);
    });

    it('renders default table', () => {
      failWrapper.update();
      expect(genericTable().props())
        .toEqual(expectedEmptyTableProps);
    });
  });
});
