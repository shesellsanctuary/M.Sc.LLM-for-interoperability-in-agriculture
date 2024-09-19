import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import consents from '../../Mockdata/Consents';
import ConsentsPageWrapper from '../ConsentsPage';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Consents Page', () => {
  const components = {
    Loading: jest.fn(() => <div className="loading" />),
    GenericTable: jest.fn(() => <div className="generic-table" />),
    ConsentsTableRow: jest.fn(() => <div className="consents-row" />),
    RouteButton: jest.fn(() => <div className="route-button" />)
  };

  const expectedTableProps = {
    headers: [
      'createdAt',
      'consents.client',
      'consents.user',
      'consents.startTime',
      'consents.endTime',
      'consents.permissionLevel',
      'consents.state.state'
    ],
    items: consents,
    TableRowElement: components.ConsentsTableRow,
  };

  describe('When data is received', () => {
    const adsPlatformApiHandler = {
      getConsents: jest.fn(() => Promise.resolve(consents)),
      getConsentRequests: jest.fn(() => Promise.resolve(consents))
    };

    const ConsentsPage = ConsentsPageWrapper({ components, adsPlatformApiHandler });
    const wrapper = mount(<ConsentsPage />);

    const pageTitle = wrapper.find('.page-title');
    const viewRequestsButton = wrapper.find('.route-button').parent();
    const loading = () => wrapper.find('.loading');
    const genericTable = () => wrapper.find('.generic-table').parent();

    it('renders page with initial components and fetches data', () => {
      expect(pageTitle.text()).toEqual('consents.pageTitle');
      expect(viewRequestsButton.prop('link')).toEqual('/consent-requests');
      expect(loading().exists()).toEqual(true);
      expect(adsPlatformApiHandler.getConsents).toHaveBeenCalledTimes(1);
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
      getConsents: jest.fn(() => Promise.reject()),
      getConsentRequests: jest.fn(() => Promise.reject())
    };

    const ConsentsPage = ConsentsPageWrapper({ components, adsPlatformApiHandler });

    const failWrapper = mount(<ConsentsPage />);

    const expectedEmptyTableProps = { ...expectedTableProps, items: [] };

    const loading = () => failWrapper.find('.loading');
    const genericTable = () => failWrapper.find('.generic-table').parent();

    it('fetches data', () => {
      expect(adsPlatformApiHandler.getConsents).toHaveBeenCalledTimes(1);
      expect(adsPlatformApiHandler.getConsentRequests).toHaveBeenCalledTimes(0);
      expect(loading().exists()).toEqual(true);
    });

    it('renders default table', () => {
      failWrapper.update();
      expect(genericTable().props())
        .toEqual(expectedEmptyTableProps);
    });
  });
});
