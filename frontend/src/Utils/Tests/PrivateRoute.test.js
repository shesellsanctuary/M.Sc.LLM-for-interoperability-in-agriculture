import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router-dom';
import { Provider } from 'react-redux';

import PrivateRoute from '../PrivateRoute';
import Engine from '../../Engine';

afterEach(cleanup);

describe('PrivateRoute', () => {
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
        <PrivateRoute Component={Component} redirect={link} {...props} />
      </MemoryRouter>
    </Provider>
  );

  it('redirects to given url when NOT logged in', () => {
    const redirect = wrapper.find('Redirect');

    expect(wrapper.find('.main-component').exists()).toEqual(false);
    expect(redirect.prop('to')).toHaveProperty('pathname', '/redirect');
  });

  it('renders main component when logged in', () => {
    store.dispatch(authActions.userLoaded('Farmer Frank'));
    wrapper.update();

    expect(wrapper.find('.main-component').exists()).toEqual(true);
    expect(wrapper.find('Redirect')).toHaveLength(0);
  });
});
