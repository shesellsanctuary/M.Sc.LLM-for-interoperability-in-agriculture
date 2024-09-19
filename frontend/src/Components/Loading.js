import React from 'react';
import Spinner from 'react-bootstrap/Spinner';
import PropTypes from 'prop-types';

const Loading = (props) => {
  const { text } = props;

  return (
    <div className="loading">
      <Spinner animation="border" role="status" variant="primary-dark" />
      {text}
    </div>
  );
};

Loading.propTypes = {
  text: PropTypes.string
};

Loading.defaultProps = {
  text: ''
};

export default Loading;
