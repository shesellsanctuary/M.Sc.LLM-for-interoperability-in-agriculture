import { createStore } from 'redux';

const Store = ({ combinedReducers, storage }) => {
  const STORAGE_KEY = 'ads-twin-hub-redux-store';

  const loadState = () => {
    try {
      const serializedState = storage.getItem(STORAGE_KEY);
      if (serializedState === null) {
        return undefined;
      }
      return JSON.parse(serializedState);
    } catch (e) {
      return undefined;
    }
  };

  const saveState = (state) => {
    try {
      const serializedState = JSON.stringify(state);
      storage.setItem(STORAGE_KEY, serializedState);
    } catch (e) {
      // Ignore write errors;
    }
  };

  const persistedState = loadState();

  const store = createStore(
    combinedReducers,
    persistedState
  );

  store.subscribe(() => {
    saveState(store.getState());
  });

  return store;
};

export default Store;
