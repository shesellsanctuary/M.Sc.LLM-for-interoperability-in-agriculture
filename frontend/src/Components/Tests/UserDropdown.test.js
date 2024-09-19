import React from 'react';
import { Provider } from 'react-redux';
import { mount } from 'enzyme';
import { cleanup } from '@testing-library/react';
import { act } from 'react-dom/test-utils';

import Engine from '../../Engine';
import UserDropdownWrapper from '../UserDropdown';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('UserDropdown', () => {
  const authService = { logout: jest.fn(() => {}) };
  const UserDropdown = UserDropdownWrapper({ authService });
  const storage = {};
  const { store, actions: { authActions } } = Engine({ storage });
  const wrapper = mount(
    <Provider store={store}>
      <UserDropdown />
    </Provider>
  );
  const mockedUser = {
    access_token: 'TOKEN',
    profile: {
      name: 'Farmer Frank'
    }
  };
  store.dispatch(authActions.userLoaded(mockedUser));

  const userDropdown = () => wrapper.find('[id="user-dropdown"]').last();
  const logoutNavItem = () => wrapper.find('[id="logout-nav-item"]').first();
  const accountNavItem = () => wrapper.find('[id="account-nav-item"]').first();

  it('renders with a logged in user name and user icon', () => {
    expect(wrapper.text()).toBe('Farmer Frank');
    expect(wrapper.find('FaUser').exists()).toEqual(true);
    expect(userDropdown().prop('aria-expanded')).toEqual(false);
  });

  it('should show dropdown with correct item on click', async () => {
    userDropdown().simulate('click');

    await act(
      () => new Promise((resolve) => {
        setTimeout(() => {
          wrapper.update();
          resolve();
        }, 0);
      })
    );

    expect(userDropdown().prop('aria-expanded')).toEqual(true);
    expect(wrapper.find('DropdownMenu').exists()).toEqual(true);
    expect(wrapper.find('DropdownItem').length).toEqual(2);
    expect(logoutNavItem().text()).toEqual('userDropdown.loggout');
    expect(accountNavItem().text()).toEqual('userDropdown.manageAccount');
  });

  it('should hide dropdown with correct item on click again', async () => {
    userDropdown().simulate('click');

    expect(userDropdown().prop('aria-expanded')).toEqual(false);
  });

  it('should call handleLogout on logout click', () => {
    logoutNavItem().props().onClick();

    expect(authService.logout).toHaveBeenCalledTimes(1);
  });
});
