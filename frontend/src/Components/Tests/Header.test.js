import React from 'react';
import { cleanup } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { mount } from 'enzyme';

import HeaderWrapper from '../Header';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('Header', () => {
  const components = {
    UserDropdown: jest.fn(() => <div className="user-dropdown" />),
    Title: jest.fn(() => <div className="title" />)
  };
  const Header = HeaderWrapper(components);
  const wrapper = mount(
    <MemoryRouter initialEntries={[{ pathname: '/fields' }]}>
      <Header />
    </MemoryRouter>
  );
  const links = [
    '/fields', '/consents', '/access-logs'
  ];
  const titles = [
    'header.twins', 'header.consents', 'header.accessLogs'
  ];

  it('renders elements and nav links with right title text and link', () => {
    const navLinks = wrapper.find('NavLink');

    navLinks.forEach((node, index) => {
      expect(node.prop('to')).toEqual(links[index]);
      expect(node.text()).toEqual(titles[index]);
    });
    expect(wrapper.find('.app-name').first().text()).toEqual('Twin Hub');
    expect(wrapper.find('.user-dropdown').exists()).toBe(true);
  });

  it('should render basic header when in login path', () => {
    const wrapperEmpty = mount(
      <MemoryRouter initialEntries={['/login']}>
        <Header />
      </MemoryRouter>
    );
    const basicTitle = wrapperEmpty.find('.title').parent();

    expect(basicTitle.prop('text')).toEqual('Twin Hub');
    expect(wrapperEmpty.find('.basic-header').exists()).toEqual(true);
  });
});
