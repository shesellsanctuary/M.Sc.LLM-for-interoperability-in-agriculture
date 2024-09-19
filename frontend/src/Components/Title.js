import React from 'react';
import PropTypes from 'prop-types';

const Title = (props) => {
  const { text } = props;

  return (
    <div className="title">
      <span>
        {text}
      </span>
    </div>
  );
};

Title.propTypes = {
  text: PropTypes.string.isRequired
};

export default Title;
