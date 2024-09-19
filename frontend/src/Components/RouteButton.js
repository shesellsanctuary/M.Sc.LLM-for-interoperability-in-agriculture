import React from 'react';
import Button from 'react-bootstrap/Button';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';

const RouteButton = (props) => {
  const {
    link, content, className, variant
  } = props;

  return (
    <Link to={`${link}`} className={`route-button button-box ${className}`} style={{ textDecoration: 'unset' }}>
      <Button variant={`${variant}`} size="sm">
        {content}
      </Button>
    </Link>
  );
};

RouteButton.defaultProps = {
  className: '',
  variant: 'primary'
};

RouteButton.propTypes = {
  link: PropTypes.string.isRequired,
  content: PropTypes.object.isRequired,
  className: PropTypes.string,
  variant: PropTypes.string
};

export default RouteButton;
