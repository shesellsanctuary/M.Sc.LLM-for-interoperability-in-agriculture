import React from 'react';
import { cleanup } from '@testing-library/react';
import { shallow } from 'enzyme';

import RouteButton from '../RouteButton';

afterEach(cleanup);

describe('RouteButton', () => {
  const props = { link: 'url', content: <div className="content" /> };
  const wrapper = shallow(<RouteButton {...props} />);

  it('renders a btn component with correct content', () => {
    expect(wrapper.find('.content').exists()).toEqual(true);
  });

  it('renders a link component with the correct url', () => {
    expect(wrapper.find('Link').prop('to')).toEqual(props.link);
  });
});
