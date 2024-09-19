import React from 'react';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import { useTranslation } from 'react-i18next';
import { withRouter, Link } from 'react-router-dom';
import PropTypes from 'prop-types';

const HeaderWrapper = (components) => {
  const { Title, UserDropdown } = components;
  const { t } = useTranslation();

  const Header = (props) => {
    const { pathname } = props.location;
    const path = pathname.replace(/\/|(urn%3A.*)/gi, '');

    if (path === 'login') {
      return (
        <div className="basic-header">
          <Title text="Twin Hub" />
        </div>
      );
    }

    return (
      <Navbar collapseOnSelect expand="lg" bg="primary" variant="dark" className="header">
        <Navbar.Toggle aria-controls="responsive-navbar-nav" />
        <Navbar.Brand className="app-name" as={Link} to="/">Twin Hub</Navbar.Brand>
        <Navbar.Collapse id="responsive-navbar-nav">
          <Nav className="navbar-links me-auto">
            <Nav.Link eventKey="1" as={Link} to="/fields">{t('header.twins')}</Nav.Link>
            <Nav.Link eventKey="2" as={Link} to="/consents">{t('header.consents')}</Nav.Link>
            <Nav.Link eventKey="3" as={Link} to="/access-logs">{t('header.accessLogs')}</Nav.Link>
          </Nav>
        </Navbar.Collapse>
        <Nav className="user-nav">
          <UserDropdown />
        </Nav>
      </Navbar>
    );
  };

  Header.propTypes = {
    location: PropTypes.shape({
      pathname: PropTypes.string
    }).isRequired
  };

  return withRouter(Header);
};

export default HeaderWrapper;
