import React from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { FaPlus } from 'react-icons/fa';
import { toast } from 'react-toastify';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';

const CreateFieldButtonWrapper = ({ adsPlatformApiHandler, ConfirmationModal }) => {
  const { postCreateField } = adsPlatformApiHandler;
  const { t } = useTranslation();

  class CreateFieldButton extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        showModal: false,
        fieldName: '',
        isFieldNameInvalid: false
      };
    }

    handleCreate = () => {
      const { fieldName } = this.state;
      const { successCallback } = this.props;
      const isEmptyInput = !fieldName.replace(/\s/g, '').length;

      if (isEmptyInput) {
        this.setState({ isFieldNameInvalid: true });
      } else {
        postCreateField(fieldName)
          .then(() => {
            this.setState({ showModal: false });
            toast.success(t('fieldsAdministration.createdSuccessfullyToast'));
            successCallback();
          })
          .catch(() => {
            toast.error(t('fieldsAdministration.failureToCreateToast'));
          });
      }
    };

    render() {
      const { showModal, fieldName, isFieldNameInvalid } = this.state;
      const { variant } = this.props;
      const newFieldNameInput = (
        <Form noValidate className="field-name-form">
          <Form.Group controlId="formFieldName">
            <Form.Label>{t('fieldsAdministration.newFieldFormNameLabel')}</Form.Label>
            <div className="field-name-input">
              <div style={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
                <Form.Control
                  type="text"
                  name="fieldName-input"
                  onChange={(e) => this.setState({ fieldName: e.target.value })}
                  value={fieldName}
                  isInvalid={isFieldNameInvalid}
                  placeholder={t('fieldsAdministration.newFieldFormNameLabel')}
                  required
                />
                <Form.Control.Feedback name="fieldNameFeedback" type="invalid">
                  {t('fieldsAdministration.newFieldInvalidFeedback')}
                </Form.Control.Feedback>
              </div>
            </div>
          </Form.Group>
        </Form>
      );

      return (
        <div className="create-field button-box">
          <Button name="createBtn" variant={variant} className="create-field-button" onClick={() => this.setState({ showModal: true })}>
            <FaPlus className="icon" />
            <span className="btn-text">
              {t('fieldsAdministration.newFieldButton')}
            </span>
          </Button>
          <ConfirmationModal
            name="createConfirmationModal"
            show={showModal}
            title={t('fieldsAdministration.newFieldButton')}
            body={newFieldNameInput}
            onHide={() => this.setState({ showModal: false, fieldName: '', isFieldNameInvalid: false })}
            onConfirm={this.handleCreate}
          />
        </div>
      );
    }
  }

  CreateFieldButton.propTypes = {
    successCallback: PropTypes.func.isRequired,
    variant: PropTypes.string
  };

  CreateFieldButton.defaultProps = {
    variant: 'primary'
  };

  return CreateFieldButton;
};

export default CreateFieldButtonWrapper;
