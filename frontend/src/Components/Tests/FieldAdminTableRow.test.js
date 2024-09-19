import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import { Button } from 'react-bootstrap';
import FieldAdminTableRowWrapper from '../FieldAdminTableRow';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Field Admin Table Row', () => {
  describe('When data is received', () => {
    const field = {
      id: 'id',
      name: 'fieldName'
    };
    const successCallback = jest.fn();
    const components = {
      DeleteFieldButton: jest.fn(() => <div className="delete-field-button" />)
    };

    const props = { item: field, successCallback };
    const FieldAdminTableRow = FieldAdminTableRowWrapper(components);
    const wrapper = mount(<table><tbody><FieldAdminTableRow {...props} /></tbody></table>);

    it('renders row with the correct values', () => {
      const tableCells = wrapper.find('td');
      expect(tableCells.at(0).text()).toEqual(field.name);

      expect(tableCells.at(1).hasClass('icon-cell')).toEqual(true);
      expect(tableCells.at(1).childAt(0)
        .hasClass('export-field-button')).toEqual(true);
      expect(tableCells.at(1).childAt(0)
        .type()).toEqual(Button);

      expect(tableCells.at(2).hasClass('icon-cell')).toEqual(true);
      expect(tableCells.at(2).childAt(0)
        .childAt(0)
        .hasClass('delete-field-button')).toEqual(true);
    });
  });
});
