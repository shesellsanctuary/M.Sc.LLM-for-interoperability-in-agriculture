import React from 'react';
import { cleanup } from '@testing-library/react';
import { shallow } from 'enzyme';

import Title from '../Title';

afterEach(cleanup);

describe('Title', () => {
  it('renders with correct text', () => {
    const wrapper = shallow(<Title text="Title" />);
    expect(wrapper.text()).toBe('Title');
  });
});
