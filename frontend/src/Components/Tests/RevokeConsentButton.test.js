/* eslint-disable no-shadow */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { shallow } from 'enzyme';
import { toast } from 'react-toastify';
import RevokeConsentButtonWrapper from '../RevokeConsentButton';

jest.mock('react-toastify');
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

afterEach(cleanup);

describe('Revoke Consent Button', () => {
  const adsPlatformApiHandler = {
    revokeConsent: jest.fn(() => Promise.resolve({ status: 200 }))
  };

  const ConfirmationModal = jest.fn(() => <div className="confirmation-modal" />);
  const consent = {
    id: 'consentId'
  };
  const props = {
    consent,
    successCallback: jest.fn(),
    disabled: true
  };

  const RevokeConsentButton = RevokeConsentButtonWrapper({
    adsPlatformApiHandler,
    ConfirmationModal
  });

  const wrapper = shallow(<RevokeConsentButton {...props} />);
  const revokeButton = () => wrapper.find('[name="revokeBtn"]');
  const confirmationModal = () => wrapper.find('[name="revokeConfirmationModal"]');

  const successToastSpy = jest.spyOn(toast, 'success');

  it('should have a button with icon', () => {
    expect(revokeButton().text()).toEqual('<FaRegWindowClose />consents.admin.revokeConsent');
  });

  it('should have the button disabled as received in props', () => {
    expect(revokeButton().prop('disabled')).toEqual(true);
  });

  it('should NOT show confirmation modal when not clicked', () => {
    wrapper.setProps({ disabled: false });
    wrapper.update();
    expect(revokeButton().prop('disabled')).toEqual(false);
    expect(confirmationModal().prop('show')).toEqual(false);
  });

  it('should show confirmation modal when delete button is clicked', () => {
    revokeButton().props().onClick();
    expect(confirmationModal().prop('show')).toEqual(true);
  });

  it('should hide modal when close button is clicked', () => {
    confirmationModal().props().onHide();

    expect(confirmationModal().prop('show')).toEqual(false);
  });

  describe('When revoke consent request is successfull', () => {
    confirmationModal().props().onConfirm();

    it('should call revoke consent request', () => {
      expect(adsPlatformApiHandler.revokeConsent)
        .toHaveBeenCalledWith(consent.id);
      expect(props.successCallback).toHaveBeenCalledTimes(1);
    });

    it('should display successfull toast and redirect to home page', () => {
      expect(successToastSpy).toHaveBeenCalledWith('consents.admin.revokedSuccessfullyToast');
    });
  });

  describe('When request fails', () => {
    beforeAll(() => {
      jest.clearAllMocks();
    });

    const adsPlatformApiHandler = {
      revokeConsent: jest.fn(() => Promise.reject(new Error('test reject')))
    };

    const RevokeConsentButton = RevokeConsentButtonWrapper({
      ConfirmationModal, adsPlatformApiHandler
    });
    const failWrapper = shallow(<RevokeConsentButton {...props} />);

    const revokeButton = () => failWrapper.find('[name="revokeBtn"]');
    const confirmationModal = () => failWrapper.find('[name="revokeConfirmationModal"]');

    const errorToastSpy = jest.spyOn(toast, 'error');

    it('should call revoke consent request', () => {
      revokeButton().props().onClick();
      confirmationModal().props().onConfirm();

      expect(adsPlatformApiHandler.revokeConsent)
        .toHaveBeenCalledWith(consent.id);
    });

    it('should display error toast, not redirect page and hide modal', () => {
      expect(errorToastSpy).toHaveBeenCalledWith('consents.admin.failureToRevokeToast');
      expect(props.successCallback).toHaveBeenCalledTimes(0);
      expect(confirmationModal().prop('show')).toEqual(false);
    });
  });
});
