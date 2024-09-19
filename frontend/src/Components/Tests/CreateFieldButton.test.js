/* eslint-disable no-shadow */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import { toast } from 'react-toastify';
import CreateFieldButtonWrapper from '../CreateFieldButton';
import ConfirmationModal from '../ConfirmationModal';

jest.mock('react-toastify');
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));
afterEach(cleanup);

describe('Create Field Button', () => {
  const adsPlatformApiHandler = {
    postCreateField: jest.fn(() => Promise.resolve({ id: 'id' }))
  };

  const props = {
    successCallback: jest.fn()
  };

  const CreateFieldButton = CreateFieldButtonWrapper({
    adsPlatformApiHandler, ConfirmationModal
  });
  const wrapper = mount(<CreateFieldButton {...props} />);

  const createButton = () => wrapper.find('[name="createBtn"]').first();
  const confirmationModal = () => wrapper.find('[name="createConfirmationModal"]').first();
  const fieldNameInput = () => wrapper.find('[name="fieldName-input"]').first();
  const fieldNameFeedback = () => wrapper.find('[name="fieldNameFeedback"]').first();
  const successToastSpy = jest.spyOn(toast, 'success');

  it('should have a plus button', () => {
    expect(createButton().exists()).toEqual(true);
    expect(createButton().text()).toEqual('fieldsAdministration.newFieldButton');
    expect(wrapper.find('.icon').first().exists()).toEqual(true);
  });

  it('should show confirmation modal when delete button is clicked', () => {
    createButton().props().onClick();
    wrapper.update();
    expect(confirmationModal().prop('show')).toEqual(true);
    expect(wrapper.find('.form-label').text()).toEqual('fieldsAdministration.newFieldFormNameLabel');
    expect(fieldNameFeedback().text()).toEqual('fieldsAdministration.newFieldInvalidFeedback');
    expect(confirmationModal().prop('title')).toEqual('fieldsAdministration.newFieldButton');
  });

  it('should hide modal and reset state when close button is clicked', () => {
    confirmationModal().props().onHide();
    wrapper.update();

    expect(confirmationModal().prop('show')).toEqual(false);
    expect(wrapper.state()).toEqual({ showModal: false, fieldName: '', isFieldNameInvalid: false });
  });

  describe('When empty field name is submitted', () => {
    it('should indicate invalid input and not submit value or redirect page', () => {
      createButton().props().onClick();
      wrapper.update();
      fieldNameInput().simulate('change', { target: { value: ' ' } });
      confirmationModal().props().onConfirm();

      expect(adsPlatformApiHandler.postCreateField).toHaveBeenCalledTimes(0);
      expect(props.successCallback).toHaveBeenCalledTimes(0);
      expect(wrapper.state()).toEqual({ showModal: true, fieldName: ' ', isFieldNameInvalid: true });
    });
  });

  describe('When create field post request is successfull', () => {
    beforeAll(() => {
      jest.clearAllMocks();
    });

    it('should call create field request and rerender parent component', () => {
      createButton().props().onClick();
      wrapper.update();
      fieldNameInput().simulate('change', { target: { value: 'new Field Name' } });
      confirmationModal().props().onConfirm();

      expect(adsPlatformApiHandler.postCreateField)
        .toHaveBeenCalledWith('new Field Name');
    });

    it('should display successfull toast', () => {
      expect(props.successCallback).toHaveBeenCalledTimes(1);
      expect(successToastSpy).toHaveBeenCalledWith('fieldsAdministration.createdSuccessfullyToast');
    });
  });

  describe('When request fails', () => {
    beforeAll(() => {
      jest.clearAllMocks();
    });

    const adsPlatformApiHandler = {
      postCreateField: jest.fn(() => Promise.reject(new Error('test reject')))
    };

    const CreateFieldButton = CreateFieldButtonWrapper({
      ConfirmationModal, adsPlatformApiHandler
    });
    const failWrapper = mount(<CreateFieldButton {...props} />);

    const createButton = () => failWrapper.find('[name="createBtn"]').first();
    const confirmationModal = () => failWrapper.find('[name="createConfirmationModal"]').first();
    const fieldNameInput = () => failWrapper.find('[name="fieldName-input"]').first();

    const errorToastSpy = jest.spyOn(toast, 'error');

    it('should call create field request', () => {
      createButton().props().onClick();
      failWrapper.update();
      fieldNameInput().simulate('change', { target: { value: 'new Field Name' } });
      confirmationModal().props().onConfirm();

      expect(adsPlatformApiHandler.postCreateField)
        .toHaveBeenCalledWith('new Field Name');
    });

    it('should display error toast, not redirect page and keep modal open', () => {
      expect(errorToastSpy).toHaveBeenCalledWith('fieldsAdministration.failureToCreateToast');
      expect(props.successCallback).toHaveBeenCalledTimes(0);
      expect(confirmationModal().prop('show')).toEqual(true);
    });
  });
});
