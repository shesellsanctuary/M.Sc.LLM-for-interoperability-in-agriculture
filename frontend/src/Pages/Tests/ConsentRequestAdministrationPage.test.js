/* eslint-disable no-shadow */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import { toast } from 'react-toastify';
import { twinResources, staticFields, consentRequest } from '../../Mockdata';
import formatUtils from '../../Utils/FormatUtils';
import ConsentRequestAdministrationPageWrapper from '../ConsentRequestAdministrationPage';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key })
}));
jest.mock('react-toastify');
const errorToastSpy = jest.spyOn(toast, 'error');
const successToastSpy = jest.spyOn(toast, 'success');

describe('Consent Request Administration Page', () => {
  const components = {
    Loading: jest.fn(() => <div className="loading" />),
    GenericTable: jest.fn(() => <div className="generic-table" />),
    TwinResourcePermissionsTableRow: jest.fn(() => <div className="consent-admin-row" />),
    RouteButton: jest.fn(() => <div className="route-button" />)
  };

  const props = {
    match: { params: { id: 'id' } },
    history: {
      push: jest.fn()
    }
  };

  describe('When connection to api is successful', () => {
    const adsPlatformApiHandler = {
      getConsentRequest: jest.fn(() => Promise.resolve(consentRequest)),
      getTwinResources: jest.fn(() => Promise.resolve(twinResources)),
      getFields: jest.fn(() => Promise.resolve(staticFields)),
      answerConsentRequest: jest.fn(() => Promise.resolve({ status: 204 }))
    };

    const expectedState = {
      consentRequest,
      isLoading: false,
      requestId: 'id',
      twinResources,
      fields: staticFields,
      grantAccessToAllTwins: true,
      selectedFields: [],
      additionalNotes: null
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
      rowProps: { consent: consentRequest }
    };

    const expectedInputs = {
      createdAt: '28.04.2022, 12:11',
      startTime: '28.04.2022',
      endTime: '28.04.2024',
      requestsFullAccess: 'no'
    };

    const expectedData = {
      decision: 'ACCEPT',
      additionalNotes: 'Additional Notes',
      twinIds: ['urn:naming.ads.cognac:fields:09bc2173-0934-4fae-9026-76bf8b770b29'],
      grantAccessToAllTwins: false,
    };

    const ConsentRequestAdministrationPage = ConsentRequestAdministrationPageWrapper({
      components, adsPlatformApiHandler, formatUtils
    });

    const wrapper = mount(<ConsentRequestAdministrationPage.WrappedComponent {...props} />);

    const grantAccessToAllTwinsSwitch = () => wrapper.find('[name="grantAccessToAllTwins"]').last();
    const fieldsCheckboxesCollapse = () => wrapper.find('[name="fields-collapse"]').first();

    const fieldsCheckboxes = () => wrapper.find('.form-check-input');
    const additionalNotesTextArea = () => wrapper.find('.additional-notes-text-area').last();

    const acceptButton = () => wrapper.find('[name="accept"]').last();
    const declineButton = () => wrapper.find('[name="decline"]').last();
    it('renders initial components and fetchs data', () => {
      const loading = wrapper.find('.loading').parent();
      const pageTitle = wrapper.find('.page-title');

      expect(pageTitle.text()).toEqual('consents.requestsAdmin.pageTitle');
      expect(loading.prop('text')).toEqual('Fetching Data');
      expect(adsPlatformApiHandler.getTwinResources).toHaveBeenCalledTimes(1);
      expect(adsPlatformApiHandler.getFields).toHaveBeenCalledTimes(1);
      expect(adsPlatformApiHandler.getConsentRequest).toHaveBeenCalledWith('id');
      expect(wrapper.state()).toEqual(expectedState);
    });

    it('renders consent info form and permission table after isLoading changes to false', () => {
      wrapper.update();
      const clientLabel = wrapper.find('[name="consent-client-label"]').last();
      const userLabel = wrapper.find('[name="consent-user-label"]').last();
      const requestsFullAccessLabel = wrapper.find('[name="consent-requestFullAccess-label"]').last();
      const createdAtLabel = wrapper.find('[name="consent-createdAt-label"]').last();
      const startTimeLabel = wrapper.find('[name="consent-startTime-label"]').last();
      const endTimeLabel = wrapper.find('[name="consent-endTime-label"]').last();

      const clientInput = wrapper.find('[name="consent-client-input"]').last();
      const userInput = wrapper.find('[name="consent-user-input"]').last();
      const requestsFullAccessInput = wrapper.find('[name="consent-requestFullAccess-input"]').last();
      const createdAtInput = wrapper.find('[name="consent-createdAt-input"]').last();
      const startTimeInput = wrapper.find('[name="consent-startTime-input"]').last();
      const endTimeInput = wrapper.find('[name="consent-endTime-input"]').last();

      const genericTable = wrapper.find('.generic-table').parent();
      const routeButton = wrapper.find('.route-button').parent();

      const dataUsageStatement = wrapper.find('.data-usage');

      expect(routeButton.prop('link')).toEqual('/consent-requests');

      expect(clientLabel.text()).toEqual('consents.client');
      expect(userLabel.text()).toEqual('consents.user');
      expect(requestsFullAccessLabel.text()).toEqual('consents.requestsAdmin.requestsFullAccessLabel');
      expect(createdAtLabel.text()).toEqual('createdAt');
      expect(startTimeLabel.text()).toEqual('consents.startTime');
      expect(endTimeLabel.text()).toEqual('consents.endTime');

      expect(clientInput.prop('value')).toEqual(consentRequest.requestorIdentity.clientName);
      expect(userInput.prop('value')).toEqual(consentRequest.requestorIdentity.userName);
      expect(requestsFullAccessInput.prop('value')).toEqual(expectedInputs.requestsFullAccess);
      expect(createdAtInput.prop('value')).toEqual(expectedInputs.createdAt);
      expect(startTimeInput.prop('value'))
        .toEqual(expectedInputs.startTime);
      expect(endTimeInput.prop('value'))
        .toEqual(expectedInputs.endTime);

      expect(genericTable.props()).toEqual(expectedProps);

      expect(dataUsageStatement.text()).toEqual(`consents.admin.dataUsageStatement:${consentRequest.dataUsageStatement.replaceAll('\n', '')}`);

      expect(grantAccessToAllTwinsSwitch().prop('checked')).toEqual(true);
      expect(fieldsCheckboxesCollapse().prop('in')).toEqual(false);
      expect(fieldsCheckboxes().length).toEqual(3);

      expect(additionalNotesTextArea().childAt(0).text()).toEqual('consents.admin.additionalNotes');

      expect(acceptButton().text()).toEqual('consents.requestsAdmin.acceptRequest');
      expect(declineButton().text()).toEqual('consents.requestsAdmin.declineRequest');
    });

    it('collapses fields checkboxes when grantAccessToAllTwinsSwitch is switched off', () => {
      grantAccessToAllTwinsSwitch().simulate('change');
      wrapper.update();
      expect(fieldsCheckboxesCollapse().prop('in')).toEqual(true);
    });

    it('posts accept answer on accept button click', () => {
      fieldsCheckboxes().at(0).simulate('change', {
        target: {
          checked: true,
          id: 'urn:naming.ads.cognac:fields:09bc2173-0934-4fae-9026-76bf8b770b29'
        }
      });
      additionalNotesTextArea().childAt(1).simulate('change', {
        target: { value: 'Additional Notes' }
      });
      wrapper.update();
      acceptButton().simulate('click');
      wrapper.update();

      expect(adsPlatformApiHandler.answerConsentRequest)
        .toHaveBeenCalledWith('id', expectedData);
    });

    it('should display Answer request success toast', () => {
      expect(successToastSpy).toHaveBeenCalledWith('consents.requestsAdmin.answeredSuccessfullyToast');
    });

    it('should redirect to consents page', () => {
      wrapper.update();
      expect(props.history.push).toHaveBeenCalledWith('/consents');
    });
  });

  describe('When getConsentRequest fails', () => {
    const adsPlatformApiHandler = {
      getConsentRequest: jest.fn(() => Promise.reject(new Error('test reject'))),
      getTwinResources: jest.fn(() => Promise.resolve(twinResources)),
      getFields: jest.fn(() => Promise.resolve(staticFields))
    };

    const ConsentRequestAdministrationPage = ConsentRequestAdministrationPageWrapper({
      components, adsPlatformApiHandler, formatUtils
    });

    const wrapper = mount(<ConsentRequestAdministrationPage.WrappedComponent {...props} />);

    const expectedState = {
      consentRequest: null,
      isLoading: false,
      requestId: 'id',
      twinResources,
      fields: staticFields,
      grantAccessToAllTwins: true,
      selectedFields: [],
      additionalNotes: null
    };

    const pageTitle = () => wrapper.find('.page-title');
    it('renders initial components and fetchs data', () => {
      const loading = wrapper.find('.loading').parent();

      expect(pageTitle().text()).toEqual('consents.requestsAdmin.pageTitle');
      expect(loading.prop('text')).toEqual('Fetching Data');
      expect(adsPlatformApiHandler.getConsentRequest).toHaveBeenCalledWith('id');
      expect(adsPlatformApiHandler.getTwinResources).toHaveBeenCalledTimes(1);
      expect(adsPlatformApiHandler.getFields).toHaveBeenCalledTimes(1);
      expect(wrapper.state()).toEqual(expectedState);
    });

    it('renders title and no data message after isLoading changes to false', () => {
      wrapper.update();
      const message = wrapper.find('.message');

      expect(pageTitle().text()).toEqual('consents.requestsAdmin.pageTitle');
      expect(message.text()).toEqual('noDataMessage');
    });
  });

  describe('When answerConsentRequest fails', () => {
    beforeAll(() => {
      jest.clearAllMocks();
    });

    const adsPlatformApiHandler = {
      getConsentRequest: jest.fn(() => Promise.resolve(consentRequest)),
      getTwinResources: jest.fn(() => Promise.resolve(twinResources)),
      answerConsentRequest: jest.fn(() => Promise.reject(new Error('test reject'))),
      getFields: jest.fn(() => Promise.resolve(staticFields))
    };
    const expectedData = {
      decision: 'ACCEPT',
      additionalNotes: null,
      grantAccessToAllTwins: true,
      twinIds: null,
    };
    const ConsentRequestAdministrationPage = ConsentRequestAdministrationPageWrapper({
      components, adsPlatformApiHandler, formatUtils
    });

    const failWrapper = mount(<ConsentRequestAdministrationPage.WrappedComponent {...props} />);

    const acceptButton = () => failWrapper.find('[name="accept"]').last();

    it('should display loading spinner and request patch', () => {
      failWrapper.update();
      acceptButton().simulate('click');
      failWrapper.update();

      expect(adsPlatformApiHandler.answerConsentRequest)
        .toHaveBeenCalledWith('id', expectedData);
    });

    it('should display Answer request failure toast', () => {
      expect(errorToastSpy).toHaveBeenCalledWith('consents.requestsAdmin.failureToAnswerToast');
    });

    it('should stay info edit mode, hide the loading spinner and display failure toast', () => {
      failWrapper.update();
      expect(props.history.push).toHaveBeenCalledTimes(0);
    });
  });
});
