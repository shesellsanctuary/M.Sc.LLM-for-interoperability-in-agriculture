import React from 'react';
import { FaUser } from 'react-icons/fa';
import NavDropdown from 'react-bootstrap/NavDropdown';
import { connect } from 'react-redux';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';

const UserDropdownWrapper = ({ authService }) => {
  const { logout } = authService;
  const { t } = useTranslation();

  const UserDropdown = (props) => {
    const handleLogout = () => {
      logout();
    };

    return (
      <div className="user-nav-dropdown">
        <span className="username">
          {props.userName}
        </span>
        <NavDropdown
          alignRight
          title={<FaUser className="icon" />}
          id="user-dropdown"
        >
          <NavDropdown.Item id="account-nav-item">
            {t('userDropdown.manageAccount')}
          </NavDropdown.Item>
          <NavDropdown.Item id="logout-nav-item" onClick={handleLogout}>
            {t('userDropdown.loggout')}
          </NavDropdown.Item>
        </NavDropdown>
      </div>
    );
  };

  UserDropdown.propTypes = {
    userName: PropTypes.string.isRequired
  };

  return connect(mapStateToProps)(UserDropdown);
};

const mapStateToProps = (state) => {
  const { user } = state.auth;

  return {
    userName: user ? user.profile.name : ''
  };
};

export default UserDropdownWrapper;
