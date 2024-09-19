import AdsPlatformApiHandler from './AdsPlatformApiHandler';

const Infra = ({ config, store }) => {
  const adsPlatformApiHandler = AdsPlatformApiHandler(
    { config, store }
  );

  return {
    adsPlatformApiHandler
  };
};

export default Infra;
