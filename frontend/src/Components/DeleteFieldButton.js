import React, { useState } from 'react';
import Button from 'react-bootstrap/Button';
import { toast } from 'react-toastify';
import { FaTrashAlt } from 'react-icons/fa';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';

const DeleteFieldButtonWrapper = ({ adsPlatformApiHandler, ConfirmationModal }) => {
  const { deleteField } = adsPlatformApiHandler;
  const { t } = useTranslation();

  const DeleteFieldButton = (props) => {
    const { fieldInfo: { id, name }, successCallback } = props;
    const [showModal, setShowModal] = useState(false);

    const handleDelete = () => {
      deleteField(id)
        .then(() => {
          setShowModal(false);
          toast.success(t('fieldsAdministration.deletedSuccessfullyToast'));
          successCallback();
        })
        .catch(() => {
          toast.error(t('fieldsAdministration.failureToDeleteToast'));
          setShowModal(false);
        });
    };

    return (
      <div className="delete-field-button-box">
        <Button name="deleteBtn" variant="danger" size="sm" className="delete-field-button" onClick={() => setShowModal(true)}>
          <FaTrashAlt className="icon danger" />
        </Button>
        <ConfirmationModal
          name="deleteConfirmationModal"
          show={showModal}
          title={t('fieldsAdministration.deleteField')}
          body={(
            <p>
              {t('fieldsAdministration.deleteFieldConfirmationQuestion')}
              <b>{name}</b>
              {' '}
              ?
            </p>
          )}
          onHide={() => setShowModal(false)}
          onConfirm={() => handleDelete()}
        />
      </div>
    );
  };

  DeleteFieldButton.propTypes = {
    fieldInfo: PropTypes.shape({
      id: PropTypes.string,
      name: PropTypes.string,
      farmId: PropTypes.string
    }).isRequired,
    successCallback: PropTypes.func.isRequired
  };

  return DeleteFieldButton;
};

export default DeleteFieldButtonWrapper;
