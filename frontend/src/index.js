import React from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';
import './Style/index.scss';
import App from './App';
import * as serviceWorker from './serviceWorker';
import './i18n';

const loadAppEnv = () => axios.get(`${process.env.PUBLIC_URL}/config.json`)
  .then((response) => response.data);

const renderApp = ({ appEnv }) => {
  ReactDOM.render(
    <React.StrictMode>
      <React.Suspense fallback="... is loading">
        <App appEnv={appEnv} />
      </React.Suspense>
    </React.StrictMode>,
    document.getElementById('root'),
  );
};

loadAppEnv().then((appEnv) => renderApp({ appEnv }));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
