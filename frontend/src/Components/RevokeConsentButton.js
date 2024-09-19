import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';
import Button from 'react-bootstrap/Button';
import { FaRegWindowClose } from 'react-icons/fa';
import PropTypes from 'prop-types';

const RevokeConsentButtonWrapper = ({ adsPlatformApiHandler, ConfirmationModal }) => {
  const { revokeConsent } = adsPlatformApiHandler;
  const { t } = useTranslation();

  const RevokeConsentButton = (props) => {
    const {
      consent: {
        id
      },
      successCallback,
      disabled
    } = props;
    const [showModal, setShowModal] = useState(false);

    const handleRevoke = () => {
      revokeConsent(id)
        .then(() => {
          setShowModal(false);
          toast.success(t('consents.admin.revokedSuccessfullyToast'));
          successCallback();
        })
        .catch(() => {
          toast.error(t('consents.admin.failureToRevokeToast'));
          setShowModal(false);
        });
    };

    return (
      <div className="revoke-consent">
        <Button
          name="revokeBtn"
          variant="danger"
          size="small"
          className="revoke-consent-button"
          onClick={() => setShowModal(true)}
          disabled={disabled}
        >
          <FaRegWindowClose className="icon" />
          {t('consents.admin.revokeConsent')}
        </Button>
        <ConfirmationModal
          name="revokeConfirmationModal"
          show={showModal}
          title={t('consents.admin.revokeConsent')}
          body={(
            <p>
              {t('consents.admin.revokeConsentConfirmationQuestion')}
            </p>
          )}
          onHide={() => setShowModal(false)}
          onConfirm={() => handleRevoke()}
        />
      </div>
    );
  };

  RevokeConsentButton.propTypes = {
    consent: PropTypes.shape({
      id: PropTypes.string
    }).isRequired,
    successCallback: PropTypes.func.isRequired,
    disabled: PropTypes.bool
  };

  RevokeConsentButton.defaultProps = {
    disabled: false
  };

  return RevokeConsentButton;
};

export default RevokeConsentButtonWrapper;
