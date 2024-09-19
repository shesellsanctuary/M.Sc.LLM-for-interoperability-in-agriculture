import React from 'react';
import { MdModeEdit } from 'react-icons/md';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';

const ConsentsTableRowWrapper = ({ formatUtils, RouteButton }) => {
  const { formatDate, getConsentPermissionLevel } = formatUtils;
  const { t } = useTranslation();

  const ConsentsTableRow = (props) => {
    const { item } = props;
    const {
      id,
      requestorIdentity: {
        userName,
        clientName
      },
      startTime,
      endTime,
      state,
      createdAt
    } = item;

    return (
      <tr className="table-row">
        <td>
          {formatDate(createdAt, { dateStyle: 'medium', timeStyle: 'short' })}
        </td>
        <td>
          {clientName}
        </td>
        <td>
          {userName}
        </td>
        <td>
          {startTime ? formatDate(startTime, { dateStyle: 'medium', timeStyle: 'short' }) : t('consents.noDate')}
        </td>
        <td>
          {endTime ? formatDate(endTime, { dateStyle: 'medium', timeStyle: 'short' }) : t('consents.noDate')}
        </td>
        <td>
          {getConsentPermissionLevel(item)}
        </td>
        <td>
          {state === 'ACTIVE'
            ? t('consents.state.active')
            : t('consents.state.revoked')}
        </td>
        <td className="icon-cell">
          <RouteButton
            link={`/consents/admin/${id}`}
            content={(<MdModeEdit className="icon" />)}
          />
        </td>
      </tr>
    );
  };

  ConsentsTableRow.propTypes = {
    item: PropTypes.shape({
      id: PropTypes.string,
      requestorIdentity: PropTypes.shape({
        userName: PropTypes.string,
        clientName: PropTypes.string
      }),
      startTime: PropTypes.string,
      endTime: PropTypes.string,
      state: PropTypes.string,
      createdAt: PropTypes.string
    }).isRequired,
  };

  return ConsentsTableRow;
};

export default ConsentsTableRowWrapper;
