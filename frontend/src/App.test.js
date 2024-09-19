/* eslint-disable jest/expect-expect */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { shallow } from 'enzyme';
import App from './App';

afterEach(cleanup);
jest.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key) => key }),
}));

it('appEnv not set, renders App without crashing', () => {
  shallow(<App />);
});
it('appEnv set, renders App without crashing', () => {
  const appEnv = {};
  shallow(<App appEnv={appEnv} />);
});
