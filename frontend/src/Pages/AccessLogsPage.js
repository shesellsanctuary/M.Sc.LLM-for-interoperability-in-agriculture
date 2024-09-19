import React from 'react';
import { useTranslation } from 'react-i18next';

const AccessLogsPageWrapper = ({ components, adsPlatformApiHandler }) => {
  const {
    Loading, GenericTable, AccessLogsTableRow, Pagination
  } = components;
  const { getAccessLogs } = adsPlatformApiHandler;
  const { t } = useTranslation();

  class AccessLogsPage extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        isLoading: true,
        data: null,
        currentPage: 1,
        recordsPerPage: 20
      };
      this.setCurrentPage = this.setCurrentPage.bind(this);
      this.setRecordsPerPage = this.setRecordsPerPage.bind(this);
    }

    componentDidMount() {
      this.getData();
    }

    componentDidUpdate(prevProps, prevState) {
      const { currentPage, recordsPerPage } = this.state;
      if (prevState.currentPage !== currentPage
        || prevState.recordsPerPage !== recordsPerPage) {
        this.getData(currentPage - 1, recordsPerPage);
      }
    }

    setCurrentPage(pgNumber) {
      this.setState({ currentPage: pgNumber });
    }

    setRecordsPerPage(number) {
      this.setState({ recordsPerPage: number });
    }

    getData(page, recordsPerPage) {
      getAccessLogs(page, recordsPerPage)
        .then((data) => {
          this.setState({ data, isLoading: false });
        })
        .catch(() => this.setState({ isLoading: false }));
    }

    render() {
      const {
        isLoading, data, currentPage
      } = this.state;

      if (isLoading === false && data === null) {
        return (
          <div className="access-logs table-page container">
            <div className="page-header">
              <div className="page-title">
                {t('accessLogs.pageTitle')}
              </div>
            </div>
            <p className="message">
              {t('noDataMessage')}
            </p>
          </div>
        );
      }

      return (
        <div className="access-logs table-page container">
          <div className="page-header">
            <div className="page-title">
              {t('accessLogs.pageTitle')}
            </div>
          </div>
          <div className="toolbar" />
          {isLoading ? <Loading text="Fetching Data" />
            : (
              <>
                <div
                  className="list-container"
                >
                  <div className="access-logs table-div">
                    <>
                      <GenericTable
                        headers={[
                          `${t('accessLogs.date')}`,
                          `${t('accessLogs.accessedBy')}`,
                          `${t('consents.user')}`,
                          `${t('accessLogs.field')}`,
                          `${t('accessLogs.data')}`,
                          `${t('accessLogs.action')}`
                        ]}
                        items={data.content}
                        TableRowElement={AccessLogsTableRow}
                      />
                    </>
                  </div>
                </div>
                <Pagination
                  nPages={data.totalPages}
                  currentPage={currentPage}
                  setCurrentPage={this.setCurrentPage}
                  setRecordsPerPage={this.setRecordsPerPage}
                />
              </>
            )}
        </div>
      );
    }
  }

  return AccessLogsPage;
};

export default AccessLogsPageWrapper;
