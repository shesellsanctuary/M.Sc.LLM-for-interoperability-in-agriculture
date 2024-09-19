import axios from 'axios';

const AdsPlatformApiHandler = ({ config, store }) => {
  const { adsPlatformBaseUrl } = config;
  const { getState } = store;
  const twinHubBaseUrl = `${adsPlatformBaseUrl}/twin-hub`;

  const getToken = () => {
    const { user } = getState().auth;
    if (user && user.access_token) {
      return user.access_token;
    }
    throw new Error('User is not logged in');
  };

  const buildOptions = (headerOptions = null) => {
    const token = getToken();
    return {
      headers: {
        Authorization: `Bearer ${token}`,
        ...headerOptions
      }
    };
  };

  const getFields = () => axios
    .get(`${twinHubBaseUrl}/fields`, buildOptions())
    .then((res) => res.data)
    .then((fields) => [...fields].sort((a, b) => (`${a.name}`).localeCompare(b.name)));

  const getFieldInfo = (id) => axios
    .get(`${twinHubBaseUrl}/fields/${encodeURIComponent(id)}`, buildOptions())
    .then((res) => res.data);

  const patchFieldName = (id, fieldName) => axios
    .patch(`${twinHubBaseUrl}/fields/${encodeURIComponent(id)}`,
      { name: fieldName },
      buildOptions({ 'Content-Type': 'application/merge-patch+json' }));

  const deleteField = (id) => axios
    .delete(`${twinHubBaseUrl}/fields/${encodeURIComponent(id)}`, buildOptions());

  const postCreateField = (fieldName) => axios
    .post(`${twinHubBaseUrl}/fields`, { name: fieldName, farmId: 'any' }, buildOptions())
    .then((res) => res.data);

  const getConsents = () => axios
    .get(`${twinHubBaseUrl}/consents`, buildOptions())
    .then((res) => res.data);

  const getConsent = (id) => axios
    .get(`${twinHubBaseUrl}/consents/${encodeURIComponent(id)}`, buildOptions())
    .then((res) => res.data);

  const revokeConsent = (id) => axios
    .post(`${twinHubBaseUrl}/consents/${encodeURIComponent(id)}/revoke`, {}, buildOptions())
    .then((res) => res.data);

  const getTwinResources = () => axios
    .get(`${twinHubBaseUrl}/twin-resources`, buildOptions())
    .then((res) => res.data);

  const getConsentRequests = () => axios
    .get(`${twinHubBaseUrl}/consent-requests`, buildOptions())
    .then((res) => res.data);

  const getConsentRequest = (id) => axios
    .get(`${twinHubBaseUrl}/consent-requests/${encodeURIComponent(id)}`, buildOptions())
    .then((res) => res.data);

  const answerConsentRequest = (id, {
    additionalNotes, decision, grantAccessToAllTwins, twinIds
  }) => axios
    .post(`${twinHubBaseUrl}/consent-requests/${encodeURIComponent(id)}/answer`, {
      additionalNotes, decision, grantAccessToAllTwins, twinIds
    }, buildOptions())
    .then((res) => res.data);

  const getAccessLogs = (page = 0, size = 20) => axios.get(`${twinHubBaseUrl}/access-logs?page=${page}&size=${size}`, buildOptions())
    .then((res) => res.data);

  return {
    getFields,
    getFieldInfo,
    patchFieldName,
    deleteField,
    postCreateField,
    getConsents,
    getConsent,
    revokeConsent,
    getTwinResources,
    getConsentRequests,
    getConsentRequest,
    answerConsentRequest,
    getAccessLogs
  };
};

export default AdsPlatformApiHandler;
