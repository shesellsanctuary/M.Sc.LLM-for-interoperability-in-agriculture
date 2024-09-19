import React, { Component } from 'react';
import {
  Form, Button
} from 'react-bootstrap';
import Collapse from 'react-bootstrap/Collapse';
import { toast } from 'react-toastify';
import {
  FaCheck, FaTimes, FaAngleLeft
} from 'react-icons/fa';
import { useTranslation } from 'react-i18next';
import { withRouter } from 'react-router-dom';
import PropTypes from 'prop-types';

const ConsentRequestAdministrationPageWrapper = ({
  components,
  formatUtils,
  adsPlatformApiHandler
}) => {
  const {
    Loading, GenericTable, TwinResourcePermissionsTableRow, RouteButton
  } = components;
  const { formatDate } = formatUtils;
  const {
    getConsentRequest, getTwinResources, getFields, answerConsentRequest
  } = adsPlatformApiHandler;
  const { t } = useTranslation();
  class ConsentRequestAdministrationPage extends Component {
    constructor(props) {
      super(props);
      this.state = {
        consentRequest: null,
        isLoading: true,
        requestId: null,
        twinResources: [],
        fields: [],
        grantAccessToAllTwins: true,
        selectedFields: [],
        additionalNotes: null
      };
      this.handleFieldSelect = this.handleFieldSelect.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount() {
      const id = decodeURIComponent(this.props.match.params.id);
      this.setState({ requestId: id });
      getTwinResources()
        .then((twinResources) => this.setState({ twinResources }))
        .catch(() => toast.error(t('consents.admin.failureToRetrieveDataReloadPage')));
      getFields()
        .then((fields) => this.setState({ fields }))
        .catch(() => toast.error(t('consents.admin.failureToRetrieveDataReloadPage')));
      this.fetchData(id);
    }

    handleSubmit(decision) {
      const {
        grantAccessToAllTwins, selectedFields, requestId, additionalNotes
      } = this.state;

      const twinIds = grantAccessToAllTwins ? null : selectedFields;

      answerConsentRequest(requestId, {
        additionalNotes, twinIds, grantAccessToAllTwins, decision
      })
        .then(() => {
          this.props.history.push('/consents');
          toast.success(t('consents.requestsAdmin.answeredSuccessfullyToast'));
        })
        .catch(() => {
          toast.error(t('consents.requestsAdmin.failureToAnswerToast'));
        });
    }

    handleFieldSelect(event) {
      const { target: { id, checked } } = event;
      const selectedFieldsList = [...this.state.selectedFields];
      const indexInList = selectedFieldsList.findIndex((elem) => elem === id);
      if (checked) {
        if (indexInList === -1) {
          selectedFieldsList.push(id);
          this.setState({ selectedFields: selectedFieldsList });
        }
      } else if (indexInList !== -1) {
        selectedFieldsList.splice(indexInList, 1);
        this.setState({ selectedFields: selectedFieldsList });
      }
    }

    reloadState = () => {
      this.setState({ isLoading: true });
    };

    fetchData(id) {
      getConsentRequest(id)
        .then((consentRequest) => this.setState({ consentRequest, isLoading: false }))
        .catch(() => {
          this.setState({ isLoading: false });
        });
    }

    render() {
      const {
        consentRequest, isLoading, twinResources, fields, grantAccessToAllTwins
      } = this.state;

      if (isLoading) {
        return (
          <div className="consents table-page container">
            <div className="page-header">
              <div className="page-title">
                {t('consents.requestsAdmin.pageTitle')}
              </div>
            </div>
            <Loading text="Fetching Data" />
          </div>
        );
      }

      if (consentRequest === null) {
        return (
          <div className="consents table-page container">
            <div className="page-header">
              <div className="page-title">
                {t('consents.requestsAdmin.pageTitle')}
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
        }, startTime, endTime, dataUsageStatement, createdAt, requestFullAccess
      } = consentRequest;

      const togglegrantAccessToAllTwins = () => this.setState({
        grantAccessToAllTwins: !grantAccessToAllTwins
      });

      return (
        <div className="consent-request-admin container">
          <div className="page-header">
            <div className="page-title">
              {t('consents.requestsAdmin.pageTitle')}
            </div>
            <RouteButton
              link="/consent-requests"
              variant="outline-primary"
              content={(
                <>
                  <FaAngleLeft className="icon" />
                  {t('back')}
                </>
              )}
            />
          </div>

          <div className="content">
            <Form className="consent-form">
              <div className="form-row">
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
              </div>
              <div className="form-row">
                <Form.Group className="info-group">
                  <Form.Label name="consent-requestFullAccess-label">
                    {t('consents.requestsAdmin.requestsFullAccessLabel')}
                  </Form.Label>
                  <Form.Control
                    readOnly
                    name="consent-requestFullAccess-input"
                    value={requestFullAccess ? t('yes') : t('no')}
                    className="consentInfoField"
                  />
                </Form.Group>
                <Form.Group className="info-group">
                  <Form.Label name="consent-createdAt-label">{t('createdAt')}</Form.Label>
                  <Form.Control
                    readOnly
                    name="consent-createdAt-input"
                    value={createdAt ? formatDate(createdAt, { dateStyle: 'medium', timeStyle: 'short' }) : t('consents.noDate')}
                    className="consentInfoField"
                  />
                </Form.Group>
              </div>
              <div className="form-row">
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
              </div>
              <div className="data-usage-statement">
                <div className="data-usage">
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
              </div>
            </Form>
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
                  rowProps={{ consent: consentRequest }}
                />
              </div>
            </div>
            <div className="select-fields">
              <Form className="consent-request-answer-form" onSubmit={this.handleSubmit}>
                <Form.Label className="select-fields-label">{t('consents.requestsAdmin.selectFieldsLabel')}</Form.Label>
                <Form.Check
                  type="switch"
                  id="custom-switch"
                  label={t('consents.requestsAdmin.allFieldsLabel')}
                  checked={grantAccessToAllTwins}
                  onChange={() => togglegrantAccessToAllTwins()}
                  aria-expanded={grantAccessToAllTwins}
                  name="grantAccessToAllTwins"
                />
                <Collapse name="fields-collapse" in={!grantAccessToAllTwins}>
                  <div className="fields-checkboxes">
                    {fields.map((field) => (
                      <Form.Check
                        className="field-checkbox"
                        type="checkbox"
                        key={field.id}
                        id={`${field.id}`}
                        label={`${field.name}`}
                        onChange={this.handleFieldSelect}
                      />
                    ))}
                  </div>
                </Collapse>
                <Form.Group className="additional-notes-text-area">
                  <Form.Label>{t('consents.admin.additionalNotes')}</Form.Label>
                  <Form.Control name="additionalNotes" as="textarea" rows={2} onChange={(e) => this.setState({ additionalNotes: e.target.value })} />
                </Form.Group>
                <div className="form-buttons">
                  <Button variant="secondary" type="button" size="sm" name="accept" onClick={() => this.handleSubmit('ACCEPT')}>
                    <FaCheck className="icon" />
                    {t('consents.requestsAdmin.acceptRequest')}
                  </Button>
                  <Button variant="danger" type="button" size="sm" name="decline" onClick={() => this.handleSubmit('DECLINE')}>
                    <FaTimes className="icon" />
                    {t('consents.requestsAdmin.declineRequest')}
                  </Button>
                </div>
              </Form>
            </div>
          </div>
        </div>
      );
    }
  }

  ConsentRequestAdministrationPage.propTypes = {
    match: PropTypes.shape({
      params: PropTypes.shape({
        id: PropTypes.string.isRequired
      })
    }).isRequired,
    history: PropTypes.object.isRequired
  };

  return withRouter(ConsentRequestAdministrationPage);
};

export default ConsentRequestAdministrationPageWrapper;
