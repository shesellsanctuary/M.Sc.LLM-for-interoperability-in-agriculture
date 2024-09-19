import React from 'react';
import { cleanup } from '@testing-library/react';
import { shallow } from 'enzyme';

import ConfirmationModal from '../ConfirmationModal';

jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));
afterEach(cleanup);

describe('ConfirmationModal', () => {
  const onHide = jest.fn();
  const onConfirm = jest.fn();

  const wrapper = shallow(<ConfirmationModal
    name="confirmationModal"
    show={false}
    heading="Heading"
    title="Confirmation Modal"
    body="Confirmation Modal body"
    onHide={onHide}
    onConfirm={onConfirm}
  />);

  describe('When property show is true', () => {
    wrapper.setProps({ show: true });

    const closeButton = () => wrapper.find('[name="closeBtn"]');
    const confirmButton = () => wrapper.find('[name="confirmBtn"]');

    it('should render props correctly', () => {
      expect(wrapper.find('ModalTitle').text()).toEqual('Confirmation Modal');
      expect(wrapper.find('h4').text()).toEqual('Heading');
      expect(wrapper.find('span').text()).toEqual('Confirmation Modal body');
      expect(closeButton().text()).toEqual('confirmationModalClose');
      expect(confirmButton().text()).toEqual('confirmationModalConfirm');
    });

    it('should call onConfirm function on confirm button click', () => {
      closeButton().props().onClick();
      expect(onHide).toHaveBeenCalledTimes(1);
    });

    it('should call onHide function on close button click', () => {
      confirmButton().props().onClick();
      expect(onConfirm).toHaveBeenCalledTimes(1);
    });
  });
});
