import React, { Component } from 'react';
import { Badge } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';

const ConsentsPageWrapper = ({ components, adsPlatformApiHandler }) => {
  const {
    Loading, GenericTable, ConsentsTableRow, RouteButton
  } = components;
  const { getConsents, getConsentRequests } = adsPlatformApiHandler;
  const { t } = useTranslation();
  class ConsentsPage extends Component {
    constructor(props) {
      super(props);
      this.state = {
        consents: [],
        isLoading: true,
        requests: []
      };
    }

    componentDidMount() {
      this.fetchData();
    }

    fetchData() {
      getConsents()
        .then((consents) => {
          this.setState({ consents, isLoading: false });
          getConsentRequests()
            .then((requests) => this.setState({ requests }));
        })
        .catch(() => this.setState({ isLoading: false }));
    }

    render() {
      const { consents, isLoading, requests } = this.state;
      return (
        <div className="consents table-page container">
          <div className="page-header">
            <div className="page-title">
              {t('consents.pageTitle')}
            </div>
          </div>
          <div className="toolbar">
            <RouteButton
              variant="outline-primary"
              className="view-requests-button"
              link="/consent-requests"
              content={(
                <>
                  {t('consents.viewRequestsButton')}
                  {requests.length > 0 ? <Badge pill variant="danger">{requests.length}</Badge> : <></>}
                </>
              )}
            />
          </div>
          <div className="list-container">
            <div className="table-div">
              {isLoading ? <Loading text="Fetching Data" />
                : (
                  <GenericTable
                    headers={[
                      `${t('createdAt')}`,
                      `${t('consents.client')}`,
                      `${t('consents.user')}`,
                      `${t('consents.startTime')}`,
                      `${t('consents.endTime')}`,
                      `${t('consents.permissionLevel')}`,
                      `${t('consents.state.state')}`
                    ]}
                    items={consents}
                    TableRowElement={ConsentsTableRow}
                  />
                )}
            </div>
          </div>
        </div>
      );
    }
  }

  return ConsentsPage;
};

export default ConsentsPageWrapper;
