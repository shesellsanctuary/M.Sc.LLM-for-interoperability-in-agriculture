import React, { Component } from 'react';
import {
  Form, Dropdown
} from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';

const ConsentAdministrationPageWrapper = ({ components, formatUtils, adsPlatformApiHandler }) => {
  const {
    Loading, GenericTable, TwinResourcePermissionsTableRow, RevokeConsentButton
  } = components;
  const { formatDate } = formatUtils;
  const { getConsent, getTwinResources, getFields } = adsPlatformApiHandler;
  const { t } = useTranslation();
  class ConsentAdministrationPage extends Component {
    constructor(props) {
      super(props);
      this.state = {
        consent: null,
        isLoading: true,
        twinResources: [],
        fields: []
      };
    }

    componentDidMount() {
      const id = decodeURIComponent(this.props.match.params.id);
      getTwinResources()
        .then((twinResources) => this.setState({ twinResources }))
        .catch(() => toast.error(t('consents.admin.failureToRetrieveDataReloadPage')));
      getFields()
        .then((fields) => this.setState({ fields }))
        .catch(() => toast.error(t('consents.admin.failureToRetrieveDataReloadPage')));
      this.fetchData(id);
    }

    componentDidUpdate() {
      const id = decodeURIComponent(this.props.match.params.id);
      if (this.state.isLoading) {
        this.fetchData(id);
      }
    }

    reloadState = () => {
      this.setState({ isLoading: true });
    };

    getRelatedFields = ({ twinIds, grantFullAccess, grantAccessToAllTwins }) => {
      const { fields } = this.state;
      if (grantFullAccess || grantAccessToAllTwins) {
        return fields;
      }
      if (twinIds) {
        return fields.filter((field) => twinIds.includes(field.id));
      }
      return [];
    };

    fetchData(id) {
      getConsent(id)
        .then((consent) => this.setState({ consent, isLoading: false }))
        .catch(() => {
          this.setState({ isLoading: false });
        });
    }

    render() {
      const {
        consent, isLoading, twinResources
      } = this.state;

      if (isLoading) {
        return (
          <div className="consents table-page container">
            <div className="page-header">
              <div className="page-title">
                {t('consents.admin.pageTitle')}
              </div>
            </div>
            <Loading text="Fetching Data" />
          </div>
        );
      }

      if (consent === null) {
        return (
          <div className="consents table-page container">
            <div className="page-header">
              <div className="page-title">
                {t('consents.admin.pageTitle')}
              </div>
            </div>
            <p className="message">
              {t('noDataMessage')}
            </p>
          </div>
        );
      }

      const {
        requestorIdentity: {
          userName,
          clientName
        }, startTime, endTime, state, dataUsageStatement, additionalNotes, createdAt
      } = consent;
      const relatedFields = this.getRelatedFields(consent);
      return (
        <div className="consents table-page container">
          <div className="page-header">
            <div className="page-title">
              {t('consents.admin.pageTitle')}
            </div>
          </div>

          <div className="toolbar">
            <Form className="consent-form">
              <Dropdown>
                <Dropdown.Toggle variant="light" className="fields-dropdown">
                  {`${t('consents.admin.relatedFields')} (${relatedFields.length})`}
                </Dropdown.Toggle>
                <Dropdown.Menu>
                  {relatedFields.length > 0
                    ? relatedFields.map((field) => (
                      <Dropdown.Item disabled key={field.id} className="fields-dropdown-item">{field.name}</Dropdown.Item>
                    ))
                    : <Dropdown.Item disabled key={consent.id} className="fields-dropdown-item">{t('consents.admin.noCurrentRelatedFields')}</Dropdown.Item>}
                </Dropdown.Menu>
              </Dropdown>
              <Form.Group className="info-group">
                <Form.Label name="consent-createdAt-label">{t('createdAt')}</Form.Label>
                <Form.Control
                  readOnly
                  name="consent-createdAt-input"
                  value={formatDate(createdAt, { dateStyle: 'medium', timeStyle: 'short' })}
                  className="consentInfoField"
                />
              </Form.Group>
              <Form.Group className="info-group">
                <Form.Label name="consent-client-label">{t('consents.client')}</Form.Label>
                <Form.Control
                  readOnly
                  name="consent-client-input"
                  value={clientName}
                  className="consentInfoField"
                />
              </Form.Group>
              <Form.Group className="info-group">
                <Form.Label name="consent-user-label">{t('consents.user')}</Form.Label>
                <Form.Control
                  readOnly
                  name="consent-user-input"
                  value={userName}
                  className="consentInfoField"
                />
              </Form.Group>
              <Form.Group className="info-group">
                <Form.Label name="consent-startTime-label">{t('consents.startTime')}</Form.Label>
                <Form.Control
                  readOnly
                  name="consent-startTime-input"
                  value={startTime ? formatDate(startTime, { dateStyle: 'medium' }) : t('consents.noDate')}
                  className="consentInfoField"
                />
              </Form.Group>
              <Form.Group className="info-group">
                <Form.Label name="consent-endTime-label">{t('consents.endTime')}</Form.Label>
                <Form.Control
                  readOnly
                  name="consent-endTime-input"
                  value={endTime ? formatDate(endTime, { dateStyle: 'medium' }) : t('consents.noDate')}
                  className="consentInfoField"
                />
              </Form.Group>
              <Form.Group className="info-group">
                <Form.Label name="consent-state-label">{t('consents.state.state')}</Form.Label>
                <Form.Control
                  readOnly
                  name="consent-state-input"
                  value={state === 'ACTIVE'
                    ? t('consents.state.active')
                    : t('consents.state.revoked')}
                  className="consentInfoField"
                />
              </Form.Group>
            </Form>
          </div>
          <div className="list-container">
            <div className="table-div">
              <GenericTable
                headers={[
                  `${t('consents.admin.data')}`,
                  `${t('consents.admin.read')}`,
                  `${t('consents.admin.create')}`,
                  `${t('consents.admin.update')}`,
                  `${t('consents.admin.delete')}`
                ]}
                items={twinResources.reduce((acc, curr) => {
                  acc.push({ id: curr });
                  return acc;
                }, [])}
                TableRowElement={TwinResourcePermissionsTableRow}
                rowProps={{ consent }}
              />
            </div>
          </div>
          <div className="bottom-buttons">
            <RevokeConsentButton disabled={state === 'REVOKED'} consent={consent} successCallback={this.reloadState} />
          </div>
          <div className="data-usage-statement">
            <div name="data-usage">
              <span>
                {t('consents.admin.dataUsageStatement')}
                :
              </span>
              {dataUsageStatement
                ? (
                  dataUsageStatement.split('\n').map((text, index) => (
                    <p key={`data_usage_${new Date().getTime() * index}`}>
                      {text}
                      <br />
                    </p>
                  ))
                )
                : <p>{t('noDataMessage')}</p>}
            </div>
            <div name="additional-notes">
              <span>
                {t('consents.admin.additionalNotes')}
                :
              </span>
              {additionalNotes
                ? (
                  <p>{additionalNotes}</p>
                )
                : <p>{t('noDataMessage')}</p>}
            </div>
          </div>
        </div>
      );
    }
  }

  ConsentAdministrationPage.propTypes = {
    match: PropTypes.shape({
      params: PropTypes.shape({
        id: PropTypes.string.isRequired
      })
    }).isRequired
  };

  return ConsentAdministrationPage;
};

export default ConsentAdministrationPageWrapper;
