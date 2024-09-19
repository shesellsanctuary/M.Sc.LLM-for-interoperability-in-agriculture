import React from 'react';
import { AiOutlineFileSearch } from 'react-icons/ai';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';

const ConsentRequestsTableRowWrapper = ({ formatUtils, RouteButton }) => {
  const { formatDate, getConsentPermissionLevel } = formatUtils;
  const { t } = useTranslation();

  const ConsentRequestsTableRow = (props) => {
    const { item } = props;
    const {
      id,
      requestorIdentity: {
        userName,
        clientName
      },
      startTime,
      endTime,
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
        <td className="icon-cell">
          <RouteButton
            link={`/consent-requests/admin/${id}`}
            content={(<AiOutlineFileSearch className="icon" />)}
          />
        </td>
      </tr>
    );
  };

  ConsentRequestsTableRow.propTypes = {
    item: PropTypes.shape({
      id: PropTypes.string,
      requestorIdentity: PropTypes.shape({
        userName: PropTypes.string,
        clientName: PropTypes.string
      }),
      startTime: PropTypes.string,
      endTime: PropTypes.string,
      createdAt: PropTypes.string
    }).isRequired,
  };

  return ConsentRequestsTableRow;
};

export default ConsentRequestsTableRowWrapper;
