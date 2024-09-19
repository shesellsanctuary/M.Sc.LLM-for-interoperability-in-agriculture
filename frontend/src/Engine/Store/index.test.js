import { combineReducers } from 'redux';
import Store from '.';
import Reducers from '../Reducers';
import Actions from '../Actions';

describe('Store', () => {
  const reducers = Reducers();
  const { authActions: { userLoaded } } = Actions();
  const combinedReducers = combineReducers(reducers);

  it('loads initial state on initialization - case nothing there', () => {
    const storage = {
      getItem: jest.fn(() => null),
    };
    const store = Store({ combinedReducers, storage });
    const initialState = { auth: { user: null, isAuthenticated: false } };

    expect(store.getState()).toEqual(initialState);
    expect(storage.getItem).toHaveBeenCalledTimes(1);
  });

  it('loads initial state on initialization - case something there', () => {
    const storage = {
      getItem: jest.fn(() => '{"auth":{"user":"test","isAuthenticated":false}}'),
    };
    const store = Store({ combinedReducers, storage });
    const initialState = { auth: { user: 'test', isAuthenticated: false } };

    expect(store.getState()).toEqual(initialState);
    expect(storage.getItem).toHaveBeenCalledTimes(1);
  });

  it('loads initial state on initialization - case something corrupted', () => {
    const storage = {
      getItem: jest.fn(() => '{intendedBroken]'),
    };
    const store = Store({ combinedReducers, storage });
    const initialState = { auth: { user: null, isAuthenticated: false } };

    expect(store.getState()).toEqual(initialState);
    expect(storage.getItem).toHaveBeenCalledTimes(1);
  });

  it('saves and loads new state', () => {
    const storageContent = {};
    const storage = {
      getItem: jest.fn((key) => ((storageContent[key] !== undefined) ? storageContent[key] : null)),
      setItem: jest.fn((key, value) => { storageContent[key] = value; }),
    };
    const store = Store({ combinedReducers, storage });
    const mockedUser = {
      access_token: 'TOKEN',
      profile: {
        name: 'Farmer Frank'
      }
    };
    const currentState = { auth: { user: mockedUser, isAuthenticated: true } };
    store.dispatch(userLoaded(mockedUser));
    expect(store.getState()).toEqual(currentState);
    expect(storage.setItem).toHaveBeenCalledWith(expect.anything(), '{"auth":{"user":{"access_token":"TOKEN","profile":{"name":"Farmer Frank"}},"isAuthenticated":true}}');
    const anotherStoreInstance = Store({ combinedReducers, storage });
    expect(anotherStoreInstance.getState()).toEqual(currentState);
  });
});
