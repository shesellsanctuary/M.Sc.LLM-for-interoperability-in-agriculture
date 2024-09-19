import React from 'react';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';

const ConfirmationModal = (props) => {
  const {
    title, heading, body, onHide, onConfirm, show, name
  } = props;
  const { t } = useTranslation();

  return (
    <Modal
      show={show}
      name={name}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      animation={false}
      onHide={onHide}
      centered
    >
      <Modal.Header>
        <Modal.Title id="contained-modal-title-vcenter">
          {title}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <h4>{heading}</h4>
        <span>
          {body}
        </span>
      </Modal.Body>
      <Modal.Footer>
        <Button name="closeBtn" variant="outline-primary" onClick={onHide}>{t('confirmationModalClose')}</Button>
        <Button name="confirmBtn" variant="primary" onClick={onConfirm}>
          {t('confirmationModalConfirm')}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

ConfirmationModal.propTypes = {
  title: PropTypes.string.isRequired,
  heading: PropTypes.string,
  body: PropTypes.any.isRequired,
  onHide: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
  show: PropTypes.bool.isRequired,
  name: PropTypes.string.isRequired
};

ConfirmationModal.defaultProps = {
  heading: ''
};

export default ConfirmationModal;
