import React from 'react';
import { cleanup } from '@testing-library/react';
import { shallow } from 'enzyme';

import DashboardPage from '../DashboardPage';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

describe('DashboardPage', () => {
  const links = [
    '/fields', '/consents', '/access-logs'
  ];
  const titles = [
    'dashboard.manageTwins.title',
    'dashboard.manageConsents.title',
    'dashboard.viewAccessLogs.title'
  ];

  describe('When connection to api is successfull', () => {
    const wrapper = shallow(<DashboardPage />);

    it('renders dashboard clickable elements', () => {
      const elements = wrapper.find('.dashboard-element');
      const elementTitles = wrapper.find('.element-title');

      elements.forEach((node, index) => {
        expect(node.prop('to')).toEqual(links[index]);
      });
      elementTitles.forEach((node, index) => {
        expect(node.text()).toEqual(titles[index]);
      });
    });
  });
});
