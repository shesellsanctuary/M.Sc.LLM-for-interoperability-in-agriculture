import React from 'react';
import { cleanup } from '@testing-library/react';
import { shallow } from 'enzyme';
import TwinResourcePermissionsTableRow from '../TwinResourcePermissionsTableRow';

jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

afterEach(cleanup);

describe('Twin Resource Permissions Table Row', () => {
  const consent = {
    grantFullAccess: false,
    grantAllTwinResourcePermissions: false,
    twinResourcePermissions: {
      WORK_RECORDS_FERTILIZATION: ['READ'],
      ARABLE_AREA: [
        'UPDATE',
        'READ'
      ]
    }
  };

  it('should render whole row with all permission when grantFullAccess is true', () => {
    const item = { id: 'ARABLE_AREA' };
    const wrapper = shallow(<TwinResourcePermissionsTableRow
      item={item}
      consent={{ ...consent, grantFullAccess: true }}
    />);
    const rowCells = wrapper.find('td');

    expect(rowCells.at(0).text()).toEqual('consents.admin.twinResources.ARABLE_AREA');
    expect(rowCells.at(1).text()).toEqual('<FaCheck />');
    expect(rowCells.at(2).text()).toEqual('<FaCheck />');
    expect(rowCells.at(3).text()).toEqual('<FaCheck />');
    expect(rowCells.at(4).text()).toEqual('<FaCheck />');
  });

  it('should render whole row with all permission when grantAllTwinResourcePermissions is true', () => {
    const item = { id: 'DATA_FILES' };
    const wrapper = shallow(<TwinResourcePermissionsTableRow
      item={item}
      consent={{ ...consent, grantAllTwinResourcePermissions: true }}
    />);
    const rowCells = wrapper.find('td');
    expect(rowCells.at(0).text()).toEqual('consents.admin.twinResources.DATA_FILES');
    expect(rowCells.at(1).text()).toEqual('<FaCheck />');
    expect(rowCells.at(2).text()).toEqual('<FaCheck />');
    expect(rowCells.at(3).text()).toEqual('<FaCheck />');
    expect(rowCells.at(4).text()).toEqual('<FaCheck />');
  });

  it('should render row according to the twinResourcePermissions object', () => {
    const item = { id: 'ARABLE_AREA' };
    const wrapper = shallow(<TwinResourcePermissionsTableRow
      item={item}
      consent={{ ...consent }}
    />);
    const rowCells = wrapper.find('td');

    expect(rowCells.at(0).text()).toEqual('consents.admin.twinResources.ARABLE_AREA');
    expect(rowCells.at(1).text()).toEqual('<FaCheck />');
    expect(rowCells.at(2).text()).toEqual('<FaTimes />');
    expect(rowCells.at(3).text()).toEqual('<FaCheck />');
    expect(rowCells.at(4).text()).toEqual('<FaTimes />');
  });

  it('should not render row when there is no permission in the twinResourcePermissions object', () => {
    const item = { id: 'RECOMMENDATIONS' };
    const wrapper = shallow(<TwinResourcePermissionsTableRow
      item={item}
      consent={{ ...consent }}
    />);

    expect(wrapper).toEqual({});
  });
});
