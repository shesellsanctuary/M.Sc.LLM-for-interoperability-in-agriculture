import React from 'react';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';

const AccessLogsTableRowWrapper = ({ formatUtils }) => {
  const { formatDate } = formatUtils;
  const { t } = useTranslation();

  const AccessLogsTableRow = (props) => {
    const { item } = props;
    const {
      requestorIdentity: {
        clientName,
        userName
      },
      date,
      twinName,
      twinResource,
      action
    } = item;

    return (
      <tr className="table-row">
        <td>
          {formatDate(date, { dateStyle: 'medium', timeStyle: 'short' })}
        </td>
        <td>
          {clientName}
        </td>
        <td>
          {userName}
        </td>
        <td>
          {twinName}
        </td>
        <td>
          {twinResource ? t(`consents.admin.twinResources.${twinResource}`) : ''}
        </td>
        <td>
          {action}
        </td>
      </tr>
    );
  };

  AccessLogsTableRow.propTypes = {
    item: PropTypes.shape({
      id: PropTypes.string,
      requestorIdentity: PropTypes.shape({
        userName: PropTypes.string,
        clientName: PropTypes.string
      }),
      date: PropTypes.string,
      twinName: PropTypes.string,
      twinResource: PropTypes.string,
      action: PropTypes.string
    }).isRequired,
  };

  return AccessLogsTableRow;
};

export default AccessLogsTableRowWrapper;
