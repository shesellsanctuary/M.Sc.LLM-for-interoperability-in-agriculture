import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';

import LoginPageWrapper from '../LoginPage';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('LoginPage', () => {
  const authService = {
    login: jest.fn()
  };

  const LoginPage = LoginPageWrapper({ authService });
  const wrapper = mount(<LoginPage />);

  it('renders all needed components', () => {
    expect(wrapper.find('ForwardRef(SvgAdsPlatformIcon)').exists()).toEqual(true);
    expect(wrapper.find('p').text()).toEqual('login.text');
    expect(wrapper.find('Button').text()).toEqual('login.button');
  });

  it('calls login method on button click', () => {
    wrapper.find('Button').props().onClick();

    expect(authService.login).toHaveBeenCalledTimes(1);
  });
});
