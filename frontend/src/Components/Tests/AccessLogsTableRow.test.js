/* eslint-disable no-shadow */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import AccessLogsTableRowWrapper from '../AccessLogsTableRow';
import formatUtils from '../../Utils/FormatUtils';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Access Logs Table Row', () => {
  const log = {
    id: 'iuri1',
    date: '2022-04-01T10:42:03.470559Z',
    requestorIdentity: {
      clientName: 'basic-fmis',
      userName: 'Bill'
    },
    twinName: 'Große Mühle',
    action: 'UPDATE',
    twinResource: 'ARABLE_AREA'
  };

  const AccessLogsTableRow = AccessLogsTableRowWrapper({ formatUtils });
  const wrapper = mount(<table><tbody><AccessLogsTableRow item={log} /></tbody></table>);

  const expectedValues = [
    '01.04.2022, 12:42',
    log.requestorIdentity.clientName,
    log.requestorIdentity.userName,
    log.twinName,
    'consents.admin.twinResources.ARABLE_AREA',
    log.action,
  ];

  it('renders the access log row with the correct values', () => {
    const tableCells = wrapper.find('td');
    expectedValues.forEach((value, index) => {
      expect(tableCells.at(index).text()).toEqual(value);
    });
  });
});
