import { combineReducers } from 'redux';
import Reducers from './Reducers';
import Actions from './Actions';
import Store from './Store';

const Engine = ({ storage }) => {
  const actions = Actions();
  const reducers = Reducers();
  const combinedReducers = combineReducers(reducers);
  const store = Store({ combinedReducers, storage });

  return {
    store,
    actions
  };
};

export default Engine;
