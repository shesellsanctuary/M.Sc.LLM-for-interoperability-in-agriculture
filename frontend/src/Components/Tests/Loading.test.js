import React from 'react';
import { cleanup } from '@testing-library/react';
import { shallow } from 'enzyme';

import Loading from '../Loading';

afterEach(cleanup);

describe('Loading', () => {
  it('renders with a text', () => {
    const wrapper = shallow(<Loading text="Fetching data" />);
    expect(wrapper.text()).toBe('Fetching data');
  });

  it('renders without a text', () => {
    const wrapper = shallow(<Loading />);
    expect(wrapper.text()).toBe('');
  });
});
