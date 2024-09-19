import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router-dom';
import { Provider } from 'react-redux';

import PublicRoute from '../PublicRoute';
import Engine from '../../Engine';

afterEach(cleanup);

describe('PublicRoute', () => {
  const Component = jest.fn(() => <div className="main-component" />);
  const link = '/redirect';
  const props = {
    path: '/fields',
    location: { pathname: '/fields' }
  };
  const storage = {};
  const { store, actions: { authActions } } = Engine({ storage });
  const wrapper = mount(
    <Provider store={store}>
      <MemoryRouter initialEntries={[props.path]}>
        <PublicRoute Component={Component} redirect={link} {...props} />
      </MemoryRouter>
    </Provider>
  );

  it('renders main component when NOT logged in', () => {
    // const authService = { isAuthenticated: jest.fn(() => false) };
    // const PublicRoute = PublicRouteWrapper({ authService });

    expect(wrapper.find('.main-component').exists()).toEqual(true);
    expect(wrapper.find('Redirect')).toHaveLength(0);
  });

  it('redirects to given url when logged in', () => {
    // const authService = { isAuthenticated: jest.fn(() => true) };
    // const PublicRoute = PublicRouteWrapper({ authService });
    store.dispatch(authActions.userLoaded('Farmer Frank'));
    // const wrapper = mount(
    //   <MemoryRouter initialEntries={[props.path]}>
    //     <PublicRoute component={Component} redirect={link} {...props} />
    //   </MemoryRouter>
    // );
    wrapper.update();
    const redirect = wrapper.find('Redirect');

    expect(wrapper.find('.main-component').exists()).toEqual(false);
    expect(redirect.prop('to')).toHaveProperty('pathname', '/redirect');
  });
});
