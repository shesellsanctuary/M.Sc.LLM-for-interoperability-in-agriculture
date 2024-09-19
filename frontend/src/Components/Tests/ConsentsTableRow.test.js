/* eslint-disable no-shadow */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import ConsentsTableRowWrapper from '../ConsentsTableRow';
import formatUtils from '../../Utils/FormatUtils';

const { formatDate } = formatUtils;
afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Consents Table Row', () => {
  const RouteButton = jest.fn(() => <div className="route-button" />);

  const consent = {
    id: '086d6099-f88f-4bf5-aa57-e6094263790d',
    createdAt: '2022-07-08T12:02:02.338Z',
    consentGiverId: '85f0be83-c236-4d5e-a525-6a751bdafc68',
    requestorIdentity: {
      userId: '85f0be83-c236-4d5e-a525-6a751bdafc68',
      clientId: 'swagger',
      userName: 'bill',
      clientName: 'swagger'
    },
    startTime: '2022-04-28T10:11:12Z',
    endTime: null,
    state: 'ACTIVE',
    grantFullAccess: false,
    grantAccessToAllTwins: false,
    twinIds: [
      'bergkoppel-1',
      'großemühle-1'
    ],
    grantAllTwinResourcePermissions: false,
    twinResourcePermissions: {
      WORK_RECORDS_FERTILIZATION: ['READ'],
      GEOMETRIES: [
        'UPDATE',
        'READ'
      ]
    },
    dataUsageStatement: 'We will only use your Work Records and field geometries to provide you our service of analyzing and optimizing your seeding.\nThis is necessary to enable us to analyze your actual seeding process and compute possible alternatives.We will not do anything else with your data.\nIf this is not okay for you: Sorry, you can not use our service}',
    additionalNotes: 'as reference see contract in harvesting folder.'
  };

  const ConsentsTableRow = ConsentsTableRowWrapper({ formatUtils, RouteButton });
  const wrapper = mount(<table><tbody><ConsentsTableRow item={consent} /></tbody></table>);

  const expectedValues = [
    '08.07.2022, 14:02',
    consent.requestorIdentity.clientName,
    consent.requestorIdentity.userName,
    formatDate(consent.startTime, { dateStyle: 'medium', timeStyle: 'short' }),
    'consents.noDate',
    '',
    'consents.state.active'
  ];

  it('renders the consent row with the correct values', () => {
    const tableCells = wrapper.find('td');
    const routeButton = wrapper.find('.route-button').parent();
    expectedValues.forEach((value, index) => {
      expect(tableCells.at(index).text()).toEqual(value);
    });
    expect(tableCells.at(tableCells.length - 1).hasClass('icon-cell')).toEqual(true);
    expect(routeButton.prop('link')).toEqual(`/consents/admin/${consent.id}`);
  });
});
