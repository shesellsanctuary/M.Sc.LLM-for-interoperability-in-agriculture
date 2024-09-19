import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import accessLogs from '../../Mockdata/AccessLogs';
import AccessLogsPageWrapper from '../AccessLogsPage';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Access Logs Page', () => {
  const components = {
    Loading: jest.fn(() => <div className="loading" />),
    GenericTable: jest.fn(() => <div className="generic-table" />),
    AccessLogsTableRow: jest.fn(() => <div className="access-logs-row" />),
    Pagination: jest.fn(() => <div className="pagination" />),
  };

  const expectedTableProps = {
    headers: [
      'accessLogs.date',
      'accessLogs.accessedBy',
      'consents.user',
      'accessLogs.field',
      'accessLogs.data',
      'accessLogs.action'
    ],
    items: accessLogs,
    TableRowElement: components.AccessLogsTableRow,
  };

  describe('When data is received', () => {
    const adsPlatformApiHandler = {
      getAccessLogs: jest.fn(() => Promise.resolve({ content: accessLogs }))
    };

    const AccessLogsPage = AccessLogsPageWrapper({ components, adsPlatformApiHandler });
    const wrapper = mount(<AccessLogsPage />);

    const pageTitle = wrapper.find('.page-title');
    const loading = () => wrapper.find('.loading');
    const genericTable = () => wrapper.find('.generic-table').parent();
    const pagination = () => wrapper.find('.pagination').parent();

    const getDataSpy = jest.spyOn(AccessLogsPage.prototype, 'getData');

    it('renders page with initial components and fetches data', () => {
      expect(pageTitle.text()).toEqual('accessLogs.pageTitle');
      expect(loading().exists()).toEqual(true);
      expect(adsPlatformApiHandler.getAccessLogs).toHaveBeenCalledTimes(1);
    });

    it('renders table when data is received', () => {
      wrapper.update();
      expect(genericTable().props()).toEqual(expectedTableProps);
      expect(loading().exists()).toEqual(false);
      expect(pagination().exists()).toEqual(true);
    });

    it('fetches new data after setCurrentPage is called', () => {
      wrapper.instance().setCurrentPage(3);
      wrapper.update();

      expect(getDataSpy).toHaveBeenCalledWith(2, 20);
    });

    it('fetches new data after setRecordsPerPage is called', () => {
      wrapper.instance().setRecordsPerPage(10);
      wrapper.update();

      expect(getDataSpy).toHaveBeenCalledWith(2, 10);
    });
  });

  describe('When NO data is received', () => {
    const adsPlatformApiHandler = {
      getAccessLogs: jest.fn(() => Promise.reject()),
    };

    const AccessLogsPage = AccessLogsPageWrapper({ components, adsPlatformApiHandler });

    const failWrapper = mount(<AccessLogsPage />);

    const loading = () => failWrapper.find('.loading');
    const pageTitle = failWrapper.find('.page-title');

    it('fetches data', () => {
      expect(adsPlatformApiHandler.getAccessLogs).toHaveBeenCalledTimes(1);
      expect(loading().exists()).toEqual(true);
    });

    it('renders title and no data message after isLoading changes to false', () => {
      failWrapper.update();
      const message = failWrapper.find('.message');

      expect(pageTitle.text()).toEqual('accessLogs.pageTitle');
      expect(message.text()).toEqual('noDataMessage');
    });
  });
});
