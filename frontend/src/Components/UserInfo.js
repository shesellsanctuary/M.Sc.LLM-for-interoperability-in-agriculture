import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

const UserInfo = (props) => (
  <div className="box">
    <span className="user-info">
      {`User: ${props.userName}`}
    </span>
  </div>
);

UserInfo.propTypes = {
  userName: PropTypes.string.isRequired
};

const mapStateToProps = (state) => {
  const { user } = state.auth;

  return {
    userName: user ? user.profile.name : ''
  };
};

export default connect(mapStateToProps)(UserInfo);
