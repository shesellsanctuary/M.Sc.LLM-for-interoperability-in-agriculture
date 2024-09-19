import React, { Component } from 'react';
import {
  FaAngleLeft
} from 'react-icons/fa';
import { useTranslation } from 'react-i18next';

const ConsentRequestsPageWrapper = ({ components, adsPlatformApiHandler }) => {
  const {
    Loading, GenericTable, ConsentRequestsTableRow, RouteButton
  } = components;
  const { getConsentRequests } = adsPlatformApiHandler;
  const { t } = useTranslation();
  class ConsentRequestsPage extends Component {
    constructor(props) {
      super(props);
      this.state = {
        requests: [],
        isLoading: true
      };
    }

    componentDidMount() {
      this.fetchData();
    }

    fetchData() {
      getConsentRequests()
        .then((requests) => this.setState({ requests, isLoading: false }))
        .catch(() => this.setState({ isLoading: false }));
    }

    render() {
      const { requests, isLoading } = this.state;
      return (
        <div className="consents table-page container">
          <div className="page-header">
            <div className="page-title">
              {t('consents.requests.pageTitle')}
            </div>
            <RouteButton
              link="/consents"
              variant="outline-primary"
              content={(
                <>
                  <FaAngleLeft className="icon" />
                  Back
                </>
                )}
            />
          </div>

          <div className="toolbar" />
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
                      `${t('consents.permissionLevel')}`
                    ]}
                    items={requests}
                    TableRowElement={ConsentRequestsTableRow}
                  />
                )}
            </div>
          </div>
        </div>
      );
    }
  }

  return ConsentRequestsPage;
};

export default ConsentRequestsPageWrapper;
