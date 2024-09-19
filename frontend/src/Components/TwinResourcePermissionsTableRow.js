import React from 'react';
import { useTranslation } from 'react-i18next';
import {
  FaCheck, FaTimes
} from 'react-icons/fa';
import PropTypes from 'prop-types';

const TwinResourcePermissionsTableRow = (props) => {
  const { t } = useTranslation();
  const { item: { id }, consent } = props;
  const {
    grantFullAccess,
    requestFullAccess,
    grantAllTwinResourcePermissions,
    requestAllTwinResourcePermissions,
    twinResourcePermissions
  } = consent;
  const name = t(`consents.admin.twinResources.${id}`) || id.replace('/_/g', ' ');

  const isFullPermission = grantFullAccess
  || grantAllTwinResourcePermissions
  || requestFullAccess
  || requestAllTwinResourcePermissions;

  const getValue = (type) => {
    if (isFullPermission) {
      return <FaCheck className="icon check" />;
    }
    if (twinResourcePermissions && twinResourcePermissions[id]?.find((value) => value === type)) {
      return <FaCheck className="icon check" />;
    }
    return <FaTimes className="icon danger" />;
  };

  const isPermissionEmpty = twinResourcePermissions && (twinResourcePermissions[id] === null
    || twinResourcePermissions[id]?.find((value) => value === 'READ'
      || value === 'CREATE'
      || value === 'UPDATE'
      || value === 'DELETE') === undefined);

  if (!isFullPermission && isPermissionEmpty) {
    return null;
  }

  return (
    <tr className="table-row">
      <td>
        {name}
      </td>
      <td className="icon-cell">
        {getValue('READ')}
      </td>
      <td className="icon-cell">
        {getValue('CREATE')}
      </td>
      <td className="icon-cell">
        {getValue('UPDATE')}
      </td>
      <td className="icon-cell">
        {getValue('DELETE')}
      </td>
    </tr>
  );
};

TwinResourcePermissionsTableRow.propTypes = {
  item: PropTypes.shape({
    id: PropTypes.string
  }).isRequired,
  consent: PropTypes.shape({
    grantFullAccess: PropTypes.bool,
    grantAllTwinResourcePermissions: PropTypes.bool,
    requestFullAccess: PropTypes.bool,
    requestAllTwinResourcePermissions: PropTypes.bool,
    twinResourcePermissions: PropTypes.object
  }).isRequired
};

export default TwinResourcePermissionsTableRow;
