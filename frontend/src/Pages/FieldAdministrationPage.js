import React from 'react';
import { Button } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';
import {
  FaDownload, FaUpload, FaTrashAlt
} from 'react-icons/fa';

const FieldAdministrationPageWrapper = ({ components, adsPlatformApiHandler }) => {
  const {
    Loading, FieldAdminTableRow, CreateFieldButton, GenericTable
  } = components;
  const { getFields } = adsPlatformApiHandler;
  const { t } = useTranslation();
  class FieldAdministrationPage extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        fields: [],
        isLoading: true
      };
    }

    componentDidMount() {
      this.fetchData();
    }

    componentDidUpdate() {
      if (this.state.isLoading) {
        this.fetchData();
      }
    }

    reloadState = () => {
      this.setState({ isLoading: true });
    };

    fetchData() {
      getFields()
        .then((apiFields) => {
          this.setState({ fields: apiFields, isLoading: false });
        })
        .catch(() => {
          this.setState({ isLoading: false });
        });
    }

    render() {
      const { fields, isLoading } = this.state;

      return (
        <div className="fields-admin table-page container">
          <div className="page-header">
            <div className="page-title">
              {t('fieldsAdministration.pageTitle')}
            </div>
          </div>
          <div className="toolbar">
            <CreateFieldButton variant="secondary" successCallback={this.reloadState} />
          </div>
          <div className="list-container">
            <div className="table-div">
              {isLoading ? <Loading text="Fetching Data" />
                : (
                  <GenericTable
                    headers={[`${t('fieldsAdministration.fieldTableHeader')}`]}
                    items={fields}
                    TableRowElement={FieldAdminTableRow}
                    rowProps={{ successCallback: this.reloadState }}
                  />
                )}
            </div>
          </div>
          <div className="bottom-buttons">
            <Button disabled variant="primary">
              <FaDownload className="icon" />
              {t('fieldsAdministration.exportAllButton')}
            </Button>
            <Button disabled variant="primary">
              <FaUpload className="icon" />
              {t('fieldsAdministration.importFieldButton')}
            </Button>
            <Button disabled variant="outline-danger">
              <FaTrashAlt className="icon danger" />
              {t('fieldsAdministration.deleteAllButton')}
            </Button>
          </div>
        </div>
      );
    }
  }

  return FieldAdministrationPage;
};

export default FieldAdministrationPageWrapper;
