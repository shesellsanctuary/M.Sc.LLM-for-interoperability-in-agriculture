import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

const PublicRoute = (props) => {
  const {
    Component, redirect, isAuthenticated, ...rest
  } = props;
  return !isAuthenticated ? <Route {...rest} component={Component} />
    : <Redirect to={{ pathname: redirect, state: { from: props.location } }} />;
};

PublicRoute.propTypes = {
  Component: PropTypes.elementType.isRequired,
  location: PropTypes.object,
  redirect: PropTypes.string.isRequired,
  isAuthenticated: PropTypes.bool.isRequired,
};

PublicRoute.defaultProps = {
  location: {}
};
const mapStateToProps = (state) => ({
  isAuthenticated: state.auth.isAuthenticated
});

export default connect(mapStateToProps)(PublicRoute);
