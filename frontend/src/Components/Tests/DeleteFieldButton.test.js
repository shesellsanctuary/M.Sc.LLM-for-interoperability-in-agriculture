/* eslint-disable no-shadow */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { shallow } from 'enzyme';
import { toast } from 'react-toastify';
import DeleteFieldButtonWrapper from '../DeleteFieldButton';

jest.mock('react-toastify');
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

afterEach(cleanup);

describe('Delete Field Button', () => {
  const adsPlatformApiHandler = {
    deleteField: jest.fn(() => Promise.resolve({ status: 204 }))
  };

  const ConfirmationModal = jest.fn(() => <div className="confirmation-modal" />);

  const fieldInfo = {
    id: 'id',
    name: 'Field Name',
    farmId: 'farmId'
  };

  const props = {
    successCallback: jest.fn()
  };

  const DeleteFieldButton = DeleteFieldButtonWrapper({
    ConfirmationModal, adsPlatformApiHandler
  });
  const wrapper = shallow(<DeleteFieldButton fieldInfo={fieldInfo} {...props} />);

  const deleteButton = () => wrapper.find('[name="deleteBtn"]');
  const confirmationModal = () => wrapper.find('[name="deleteConfirmationModal"]');

  const successToastSpy = jest.spyOn(toast, 'success');

  it('should have a delete button with icon', () => {
    expect(deleteButton().text()).toEqual('<FaTrashAlt />');
  });

  it('should NOT show confirmation modal when not clicked', () => {
    expect(confirmationModal().prop('show')).toEqual(false);
  });

  it('should show confirmation modal when delete button is clicked', () => {
    deleteButton().props().onClick();
    expect(confirmationModal().prop('show')).toEqual(true);
  });

  it('should hide modal when close button is clicked', () => {
    confirmationModal().props().onHide();

    expect(confirmationModal().prop('show')).toEqual(false);
  });

  describe('When field delete request is successfull', () => {
    confirmationModal().props().onConfirm();

    it('should call delete field request', () => {
      expect(adsPlatformApiHandler.deleteField)
        .toHaveBeenCalledWith(fieldInfo.id);
      expect(props.successCallback).toHaveBeenCalledTimes(1);
    });

    it('should display successfull toast and redirect to home page', () => {
      expect(successToastSpy).toHaveBeenCalledWith('fieldsAdministration.deletedSuccessfullyToast');
    });
  });

  describe('When request fails', () => {
    beforeAll(() => {
      jest.clearAllMocks();
    });

    const adsPlatformApiHandler = {
      deleteField: jest.fn(() => Promise.reject(new Error('test reject')))
    };

    const DeleteFieldButton = DeleteFieldButtonWrapper({
      ConfirmationModal, adsPlatformApiHandler
    });
    const failWrapper = shallow(<DeleteFieldButton fieldInfo={fieldInfo} {...props} />);

    const deleteButton = () => failWrapper.find('[name="deleteBtn"]');
    const confirmationModal = () => failWrapper.find('[name="deleteConfirmationModal"]');

    const errorToastSpy = jest.spyOn(toast, 'error');

    it('should call delete field request', () => {
      deleteButton().props().onClick();
      confirmationModal().props().onConfirm();

      expect(adsPlatformApiHandler.deleteField)
        .toHaveBeenCalledWith(fieldInfo.id);
    });

    it('should display error toast, not redirect page and hide modal', () => {
      expect(errorToastSpy).toHaveBeenCalledWith('fieldsAdministration.failureToDeleteToast');
      expect(props.successCallback).toHaveBeenCalledTimes(0);
      expect(confirmationModal().prop('show')).toEqual(false);
    });
  });
});
