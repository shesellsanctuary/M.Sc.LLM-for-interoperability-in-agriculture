/* eslint-disable no-shadow */
import axios from 'axios';
import {
  staticFields, consents, accessLogsEN
} from '../../Mockdata';
import AdsPlatformApiHandler from '../AdsPlatformApiHandler';

jest.mock('axios');

const expectedCredentials = {
  headers: {
    Authorization: 'Bearer TOKEN'
  }
};

describe('AdsPlatformApiHandler', () => {
  const config = { adsPlatformBaseUrl: 'baseUrl' };
  const state = {
    auth: {
      user: {
        access_token: 'TOKEN',
        profile: {
          name: 'Farmer Frank'
        }
      }
    }
  };
  const store = {
    getState: jest.fn(() => state)
  };

  const adsPlatformApiHandler = AdsPlatformApiHandler({ config, store });
  const {
    getFields, getFieldInfo, patchFieldName, deleteField, postCreateField, getConsents,
    getConsent, revokeConsent, getTwinResources, getConsentRequests, getConsentRequest,
    answerConsentRequest, getAccessLogs
  } = adsPlatformApiHandler;

  it('throws Error when user not logged in', () => {
    const stateLoggedOut = {
      auth: {
        user: null
      }
    };
    const store = {
      getState: jest.fn(() => stateLoggedOut)
    };
    const adsPlatformApiHandler = AdsPlatformApiHandler({ config, store });
    const { getFields } = adsPlatformApiHandler;
    expect(() => {
      getFields();
    }).toThrow(new Error('User is not logged in'));
  });

  describe('Fields Controller', () => {
    beforeEach(() => {
      jest.clearAllMocks();
      axios.get.mockResolvedValue({ data: staticFields, status: 200 });
      axios.patch.mockResolvedValue({ status: 204 });
      axios.put.mockResolvedValue({ status: 200 });
      axios.delete.mockResolvedValue({ status: 204 });
      axios.post.mockResolvedValue({ status: 201, data: { fieldId: 'fieldId' } });
    });
    const fieldId = 'uri:fieldId#094';

    it('builds the request url correctly and returns data for getFields', () => getFields().then((res) => {
      expect(res).toEqual([...staticFields].sort((a, b) => (`${a.name}`).localeCompare(b.name)));
      expect(axios.get).toHaveBeenCalledWith('baseUrl/twin-hub/fields', expectedCredentials);
    }));

    it('builds the request url correctly and returns data for getFieldInfo', () => getFieldInfo(fieldId).then((fieldInfo) => {
      expect(fieldInfo).toEqual(staticFields);
      expect(axios.get).toHaveBeenCalledWith('baseUrl/twin-hub/fields/uri%3AfieldId%23094', expectedCredentials);
    }));

    it('builds the request url correctly and patches data for patchFieldName', () => {
      const expectedPatchCredentials = {
        headers: {
          Authorization: 'Bearer TOKEN',
          'Content-Type': 'application/merge-patch+json'
        }
      };
      return patchFieldName(fieldId, 'New Field Name').then(() => {
        expect(axios.patch).toHaveBeenCalledWith('baseUrl/twin-hub/fields/uri%3AfieldId%23094', { name: 'New Field Name' }, expectedPatchCredentials);
      });
    });

    it('builds the request url correctly and deletes data for deleteField', () => deleteField(fieldId).then(() => {
      expect(axios.delete).toHaveBeenCalledWith('baseUrl/twin-hub/fields/uri%3AfieldId%23094', expectedCredentials);
    }));

    it('builds the request url correctly and post data for postCreateField', () => postCreateField('newFieldName').then(({ fieldId }) => {
      expect(fieldId).toEqual('fieldId');
      expect(axios.post).toHaveBeenCalledWith('baseUrl/twin-hub/fields', { name: 'newFieldName', farmId: 'any' }, expectedCredentials);
    }));
  });

  describe('Consents Controller', () => {
    beforeEach(() => {
      jest.clearAllMocks();
      axios.get.mockResolvedValue({ data: consents, status: 200 });
      axios.post.mockResolvedValue({ status: 201, data: consentId });
    });

    const consentId = 'uri:consentId#094';

    it('builds the request url correctly and returns data for getConsents', () => getConsents().then((res) => {
      expect(res).toEqual([...consents.sort((a, b) => (`${a.createdAt}`).localeCompare(b.createdAt))]);
      expect(axios.get).toHaveBeenCalledWith('baseUrl/twin-hub/consents', expectedCredentials);
    }));

    it('builds the request url correctly and returns data for getConsent', () => getConsent(consentId).then((res) => {
      expect(res).toEqual(consents);
      expect(axios.get).toHaveBeenCalledWith('baseUrl/twin-hub/consents/uri%3AconsentId%23094', expectedCredentials);
    }));

    it('builds the request url correctly and returns data for revokeConsent', () => revokeConsent(consentId).then((res) => {
      expect(res).toEqual(consentId);
      expect(axios.post).toHaveBeenCalledWith('baseUrl/twin-hub/consents/uri%3AconsentId%23094/revoke', {}, expectedCredentials);
    }));

    it('builds the request url correctly and returns data for getTwinResources', () => getTwinResources().then((res) => {
      expect(res).toEqual(consents);
      expect(axios.get).toHaveBeenCalledWith('baseUrl/twin-hub/twin-resources', expectedCredentials);
    }));

    it('builds the request url correctly and returns data for getConsentRequests', () => getConsentRequests().then((res) => {
      expect(res).toEqual(consents);
      expect(axios.get).toHaveBeenCalledWith('baseUrl/twin-hub/consent-requests', expectedCredentials);
    }));

    it('builds the request url correctly and returns data for getConsentRequest', () => getConsentRequest(consentId).then((res) => {
      expect(res).toEqual(consents);
      expect(axios.get).toHaveBeenCalledWith('baseUrl/twin-hub/consent-requests/uri%3AconsentId%23094', expectedCredentials);
    }));

    it('builds the request url correctly and returns data for answerConsentRequest', () => answerConsentRequest(consentId, {
      additionalNotes: null, decision: 'ACCEPT', twinIds: null, grantAccessToAllTwins: true
    }).then((res) => {
      expect(res).toEqual(consentId);
      expect(axios.post).toHaveBeenCalledWith('baseUrl/twin-hub/consent-requests/uri%3AconsentId%23094/answer', {
        additionalNotes: null, decision: 'ACCEPT', twinIds: null, grantAccessToAllTwins: true
      }, expectedCredentials);
    }));
  });

  describe('Access Logs Controller', () => {
    beforeEach(() => {
      jest.clearAllMocks();
      axios.get.mockResolvedValue({ data: accessLogsEN, status: 200 });
    });

    it('builds the request url correctly and returns data for getAccessLogs', () => getAccessLogs().then((res) => {
      expect(res).toEqual(accessLogsEN);
      expect(axios.get).toHaveBeenCalledWith('baseUrl/twin-hub/access-logs?page=0&size=20', expectedCredentials);
    }));

    it('builds the request url correctly and returns data for getAccessLogs with different parameters', () => getAccessLogs(2, 30).then((res) => {
      expect(res).toEqual(accessLogsEN);
      expect(axios.get).toHaveBeenCalledWith('baseUrl/twin-hub/access-logs?page=2&size=30', expectedCredentials);
    }));
  });
});
