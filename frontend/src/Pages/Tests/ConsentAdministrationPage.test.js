/* eslint-disable no-shadow */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import { toast } from 'react-toastify';
import { consents, twinResources, staticFields } from '../../Mockdata';
import formatUtils from '../../Utils/FormatUtils';
import ConsentAdministrationPageWrapper from '../ConsentAdministrationPage';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key })
}));
jest.mock('react-toastify');

const { formatDate } = formatUtils;
describe('Consent Administration Page', () => {
  const consent = consents[0];

  const components = {
    Loading: jest.fn(() => <div className="loading" />),
    GenericTable: jest.fn(() => <div className="generic-table" />),
    TwinResourcePermissionsTableRow: jest.fn(() => <div className="consent-admin-row" />),
    RevokeConsentButton: jest.fn(() => <div className="revoke-consent-button" />)
  };

  const props = { match: { params: { id: 'id' } } };

  describe('When connection to api is successful', () => {
    const adsPlatformApiHandler = {
      getConsent: jest.fn(() => Promise.resolve(consent)),
      getTwinResources: jest.fn(() => Promise.resolve(twinResources)),
      getFields: jest.fn(() => Promise.resolve(staticFields))
    };

    const expectedState = {
      isLoading: false,
      consent,
      twinResources,
      fields: staticFields
    };

    const expectedProps = {
      headers: [
        'consents.admin.data',
        'consents.admin.read',
        'consents.admin.create',
        'consents.admin.update',
        'consents.admin.delete'
      ],
      items: twinResources.reduce((acc, curr) => {
        acc.push({ id: curr });
        return acc;
      }, []),
      TableRowElement: components.TwinResourcePermissionsTableRow,
      rowProps: { consent }
    };

    const ConsentAdministrationPage = ConsentAdministrationPageWrapper({
      components, adsPlatformApiHandler, formatUtils
    });

    const wrapper = mount(<ConsentAdministrationPage {...props} />);

    it('renders initial components and fetchs data', () => {
      const loading = wrapper.find('.loading').parent();
      const pageTitle = wrapper.find('.page-title');

      expect(pageTitle.text()).toEqual('consents.admin.pageTitle');
      expect(loading.prop('text')).toEqual('Fetching Data');
      expect(adsPlatformApiHandler.getTwinResources).toHaveBeenCalledTimes(1);
      expect(adsPlatformApiHandler.getFields).toHaveBeenCalledTimes(1);
      expect(adsPlatformApiHandler.getConsent).toHaveBeenCalledWith('id');
      expect(wrapper.state()).toEqual(expectedState);
    });

    it('renders consent info form and permission table after isLoading changes to false', () => {
      wrapper.update();
      const fieldsDropdown = wrapper.find('.fields-dropdown').last();

      const createdAtLabel = wrapper.find('[name="consent-createdAt-label"]').last();
      const clientLabel = wrapper.find('[name="consent-client-label"]').last();
      const userLabel = wrapper.find('[name="consent-user-label"]').last();
      const startTimeLabel = wrapper.find('[name="consent-startTime-label"]').last();
      const endTimeLabel = wrapper.find('[name="consent-endTime-label"]').last();
      const stateLabel = wrapper.find('[name="consent-state-label"]').last();

      const createdAtInput = wrapper.find('[name="consent-createdAt-input"]').last();
      const clientInput = wrapper.find('[name="consent-client-input"]').last();
      const userInput = wrapper.find('[name="consent-user-input"]').last();
      const startTimeInput = wrapper.find('[name="consent-startTime-input"]').last();
      const endTimeInput = wrapper.find('[name="consent-endTime-input"]').last();
      const stateInput = wrapper.find('[name="consent-state-input"]').last();

      const genericTable = wrapper.find('.generic-table').parent();
      const revokeConsentButton = wrapper.find('.revoke-consent-button').parent();

      const dataUsageStatement = wrapper.find('[name="data-usage"]');
      const additionalNotes = wrapper.find('[name="additional-notes"]');

      expect(fieldsDropdown.text()).toEqual('consents.admin.relatedFields (0)');
      expect(createdAtLabel.text()).toEqual('createdAt');
      expect(clientLabel.text()).toEqual('consents.client');
      expect(userLabel.text()).toEqual('consents.user');
      expect(startTimeLabel.text()).toEqual('consents.startTime');
      expect(endTimeLabel.text()).toEqual('consents.endTime');
      expect(stateLabel.text()).toEqual('consents.state.state');

      expect(createdAtInput.prop('value')).toEqual(formatDate(consent.createdAt, { dateStyle: 'medium', timeStyle: 'short' }));
      expect(clientInput.prop('value')).toEqual(consent.requestorIdentity.clientName);
      expect(userInput.prop('value')).toEqual(consent.requestorIdentity.userName);
      expect(startTimeInput.prop('value'))
        .toEqual(formatDate(consent.startTime, { dateStyle: 'medium' }));
      expect(endTimeInput.prop('value'))
        .toEqual(formatDate(consent.endTime, { dateStyle: 'medium' }));
      expect(stateInput.prop('value')).toEqual('consents.state.active');

      expect(genericTable.props()).toEqual(expectedProps);
      expect(revokeConsentButton.prop('consent')).toEqual(consent);

      expect(dataUsageStatement.text()).toEqual(`consents.admin.dataUsageStatement:${consent.dataUsageStatement.replaceAll('\n', '')}`);

      expect(additionalNotes.childAt(0).text()).toEqual('consents.admin.additionalNotes:');
      expect(additionalNotes.childAt(1).text()).toEqual(consent.additionalNotes);
    });
  });
  describe('When getConsent fails', () => {
    const adsPlatformApiHandler = {
      getConsent: jest.fn(() => Promise.reject(new Error('test reject'))),
      getTwinResources: jest.fn(() => Promise.resolve(twinResources)),
      getFields: jest.fn(() => Promise.resolve(staticFields))
    };

    const ConsentAdministrationPage = ConsentAdministrationPageWrapper({
      components, adsPlatformApiHandler, formatUtils
    });

    const wrapper = mount(<ConsentAdministrationPage {...props} />);

    const expectedState = {
      isLoading: false,
      consent: null,
      twinResources,
      fields: staticFields
    };

    const pageTitle = () => wrapper.find('.page-title');
    it('renders initial components and fetchs data', () => {
      const loading = wrapper.find('.loading').parent();

      expect(pageTitle().text()).toEqual('consents.admin.pageTitle');
      expect(loading.prop('text')).toEqual('Fetching Data');
      expect(adsPlatformApiHandler.getConsent).toHaveBeenCalledWith('id');
      expect(adsPlatformApiHandler.getTwinResources).toHaveBeenCalledTimes(1);
      expect(adsPlatformApiHandler.getFields).toHaveBeenCalledTimes(1);
      expect(wrapper.state()).toEqual(expectedState);
    });

    it('renders title and no data message after isLoading changes to false', () => {
      wrapper.update();
      const message = wrapper.find('.message');

      expect(pageTitle().text()).toEqual('consents.admin.pageTitle');
      expect(message.text()).toEqual('noDataMessage');
    });
  });

  describe('When getTwinResources fails', () => {
    const consent = consents[1];
    const adsPlatformApiHandler = {
      getConsent: jest.fn(() => Promise.resolve(consent)),
      getTwinResources: jest.fn(() => Promise.reject(new Error('test reject'))),
      getFields: jest.fn(() => Promise.resolve(staticFields))
    };

    const expectedState = {
      isLoading: false,
      consent,
      twinResources: [],
      fields: staticFields
    };

    const expectedProps = {
      headers: [
        'consents.admin.data',
        'consents.admin.read',
        'consents.admin.create',
        'consents.admin.update',
        'consents.admin.delete'
      ],
      items: [],
      TableRowElement: components.TwinResourcePermissionsTableRow,
      rowProps: { consent }
    };
    const ConsentAdministrationPage = ConsentAdministrationPageWrapper({
      components, adsPlatformApiHandler, formatUtils
    });

    const errorToastSpy = jest.spyOn(toast, 'error');
    const failWrapper1 = mount(<ConsentAdministrationPage {...props} />);
    const pageTitle = () => failWrapper1.find('.page-title');

    it('renders initial components and fetchs data', () => {
      const loading = failWrapper1.find('.loading').parent();

      expect(pageTitle().text()).toEqual('consents.admin.pageTitle');
      expect(loading.prop('text')).toEqual('Fetching Data');
      expect(adsPlatformApiHandler.getTwinResources).toHaveBeenCalledTimes(1);
      expect(adsPlatformApiHandler.getFields).toHaveBeenCalledTimes(1);
      expect(adsPlatformApiHandler.getConsent).toHaveBeenCalledWith('id');
      expect(errorToastSpy).toHaveBeenCalledWith('consents.admin.failureToRetrieveDataReloadPage');
      expect(failWrapper1.state()).toEqual(expectedState);
    });

    it('renders consent info form and noDataMessage for the table after isLoading changes to false', () => {
      failWrapper1.update();
      const fieldsDropdown = failWrapper1.find('.fields-dropdown').last();

      const clientLabel = failWrapper1.find('[name="consent-client-label"]').last();
      const userLabel = failWrapper1.find('[name="consent-user-label"]').last();
      const startTimeLabel = failWrapper1.find('[name="consent-startTime-label"]').last();
      const endTimeLabel = failWrapper1.find('[name="consent-endTime-label"]').last();
      const stateLabel = failWrapper1.find('[name="consent-state-label"]').last();

      const clientInput = failWrapper1.find('[name="consent-client-input"]').last();
      const userInput = failWrapper1.find('[name="consent-user-input"]').last();
      const startTimeInput = failWrapper1.find('[name="consent-startTime-input"]').last();
      const endTimeInput = failWrapper1.find('[name="consent-endTime-input"]').last();
      const stateInput = failWrapper1.find('[name="consent-state-input"]').last();

      const genericTable = failWrapper1.find('.generic-table').parent();
      const revokeConsentButton = failWrapper1.find('.revoke-consent-button').parent();

      expect(fieldsDropdown.text()).toEqual('consents.admin.relatedFields (3)');
      expect(clientLabel.text()).toEqual('consents.client');
      expect(userLabel.text()).toEqual('consents.user');
      expect(startTimeLabel.text()).toEqual('consents.startTime');
      expect(endTimeLabel.text()).toEqual('consents.endTime');
      expect(stateLabel.text()).toEqual('consents.state.state');

      expect(clientInput.prop('value')).toEqual(consent.requestorIdentity.clientName);
      expect(userInput.prop('value')).toEqual(consent.requestorIdentity.userName);
      expect(startTimeInput.prop('value'))
        .toEqual('consents.noDate');
      expect(endTimeInput.prop('value'))
        .toEqual('consents.noDate');
      expect(stateInput.prop('value')).toEqual('consents.state.revoked');

      expect(genericTable.props()).toEqual(expectedProps);
      expect(revokeConsentButton.prop('consent')).toEqual(consent);
    });
  });
});
