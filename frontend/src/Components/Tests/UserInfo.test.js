import React from 'react';
import { Provider } from 'react-redux';
import { mount } from 'enzyme';
import { cleanup } from '@testing-library/react';

import UserInfo from '../UserInfo';
import Engine from '../../Engine';

afterEach(cleanup);

describe('UserInfo', () => {
  const storage = {};
  const { store, actions: { authActions } } = Engine({ storage });
  const wrapper = mount(
    <Provider store={store}>
      <UserInfo />
    </Provider>
  );
  it('renders with empty logged in user name when nothing received', () => {
    expect(wrapper.text()).toBe('User: ');
  });

  it('renders with a logged in user name', () => {
    const mockedUser = {
      access_token: 'TOKEN',
      profile: {
        name: 'Farmer Frank'
      }
    };
    store.dispatch(authActions.userLoaded(mockedUser));
    expect(wrapper.text()).toBe('User: Farmer Frank');
  });
});
