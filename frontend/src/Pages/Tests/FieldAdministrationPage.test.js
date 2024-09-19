import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import { staticFields } from '../../Mockdata';

import FieldAdministrationPageWrapper from '../FieldAdministrationPage';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Field Administration Page', () => {
  const components = {
    Loading: jest.fn(() => <div className="loading" />),
    DeleteFieldButton: jest.fn(() => <div className="delete-field-button" />),
    CreateFieldButton: jest.fn(() => <div className="create-field-button" />),
    GenericTable: jest.fn(() => <div className="generic-table" />),
    FieldAdminTableRow: jest.fn(() => <div className="field-admin-row" />)
  };

  describe('When api response is successfull', () => {
    const adsPlatformApiHandler = {
      getFields: jest.fn(() => Promise.resolve(staticFields))
    };

    const FieldAdministrationPage = FieldAdministrationPageWrapper({
      components, adsPlatformApiHandler
    });
    const wrapper = mount(<FieldAdministrationPage />);

    const bottomButtonsTexts = [
      'fieldsAdministration.exportAllButton',
      'fieldsAdministration.importFieldButton',
      'fieldsAdministration.deleteAllButton'
    ];

    const expectedDefaultTableProps = {
      headers: ['fieldsAdministration.fieldTableHeader'],
      items: staticFields,
      TableRowElement: components.FieldAdminTableRow,
      rowProps: { successCallback: expect.any(Function) },
    };

    const pageTitle = wrapper.find('.page-title');
    const loading = () => wrapper.find('.loading');
    const createFieldButton = wrapper.find('.create-field-button');
    const genericTable = () => wrapper.find('.generic-table').parent();
    const bottomButtons = wrapper.find('Button');

    it('renders initial components and fetches data', () => {
      expect(pageTitle.text()).toEqual('fieldsAdministration.pageTitle');
      expect(createFieldButton.exists()).toEqual(true);
      expect(loading().exists()).toEqual(true);
      expect(adsPlatformApiHandler.getFields).toHaveBeenCalledTimes(1);

      expect(bottomButtons.length).toEqual(3);
      bottomButtons.forEach((button, index) => {
        expect(button.text()).toEqual(bottomButtonsTexts[index]);
      });
    });

    it('displays data when received', () => {
      wrapper.update();
      expect(loading().exists()).toEqual(false);
      expect(genericTable().props()).toEqual(expectedDefaultTableProps);
    });
  });

  describe('When api response fails', () => {
    const adsPlatformApiHandler = {
      getFields: jest.fn(() => Promise.reject(new Error('test reject'))),
    };

    const FieldAdministrationPage = FieldAdministrationPageWrapper({
      components, adsPlatformApiHandler
    });

    const expectedEmptyDefaultTableProps = {
      headers: ['fieldsAdministration.fieldTableHeader'],
      items: [],
      TableRowElement: components.FieldAdminTableRow,
      rowProps: { successCallback: expect.any(Function) },
    };
    const failWrapper = mount(<FieldAdministrationPage />);

    it('fetches data', () => {
      expect(failWrapper.find('.loading').exists()).toEqual(true);
      expect(adsPlatformApiHandler.getFields).toHaveBeenCalledTimes(1);
    });

    it('renders default table', () => {
      failWrapper.update();
      expect(failWrapper.find('.generic-table').parent().props())
        .toEqual(expectedEmptyDefaultTableProps);
    });
  });
});
