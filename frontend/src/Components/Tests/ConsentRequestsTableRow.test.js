/* eslint-disable no-shadow */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import ConsentRequestsTableRowWrapper from '../ConsentRequestsTableRow';
import formatUtils from '../../Utils/FormatUtils';
import { consentRequest } from '../../Mockdata';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Consent Requests Table Row', () => {
  const RouteButton = jest.fn(() => <div className="route-button" />);

  const ConsentRequestsTableRow = ConsentRequestsTableRowWrapper({ formatUtils, RouteButton });
  const wrapper = mount(
    <table>
      <tbody>
        <ConsentRequestsTableRow item={consentRequest} />
      </tbody>
    </table>
  );

  const expectedValues = [
    '28.04.2022, 12:11',
    consentRequest.requestorIdentity.clientName,
    consentRequest.requestorIdentity.userName,
    '28.04.2022, 12:11',
    '28.04.2024, 12:11',
    '',
  ];

  it('renders the consent row with the correct values', () => {
    const tableCells = wrapper.find('td');
    const routeButton = wrapper.find('.route-button').parent();
    expectedValues.forEach((value, index) => {
      expect(tableCells.at(index).text()).toEqual(value);
    });
    expect(tableCells.at(tableCells.length - 1).hasClass('icon-cell')).toEqual(true);
    expect(routeButton.prop('link')).toEqual(`/consent-requests/admin/${consentRequest.id}`);
  });
});
