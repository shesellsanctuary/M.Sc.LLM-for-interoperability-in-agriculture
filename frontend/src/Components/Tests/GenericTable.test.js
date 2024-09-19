import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import { staticFields } from '../../Mockdata';

import GenericTable from '../GenericTable';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Generic Table', () => {
  const headers = ['header1'];
  const TableRowElement = jest.fn(() => <tr className="table-row-element" />);

  describe('When data is received', () => {
    const props = {
      headers, items: staticFields, TableRowElement, rowProps: { successCallback: jest.fn() }
    };

    const wrapper = mount(<GenericTable {...props} />);

    it('renders headers and rows with their respective values', () => {
      const header = wrapper.find('th');

      const tableRows = () => wrapper.find('.table-row-element').parent();
      expect(header.text()).toEqual(headers[0]);

      expect(tableRows()).toHaveLength(3);
    });
  });

  describe('When no data is received', () => {
    const emptyProps = {
      headers, items: [], TableRowElement, rowProps: { successCallback: jest.fn() }
    };
    const emptyWrapper = mount(<GenericTable {...emptyProps} />);

    it('renders message', () => {
      expect(emptyWrapper.find('.message').text()).toEqual('noDataMessage');
      expect(emptyWrapper.find('.table').exists()).toEqual(false);
    });
  });
});
