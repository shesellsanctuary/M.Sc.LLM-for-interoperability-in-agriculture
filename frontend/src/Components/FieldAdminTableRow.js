import React from 'react';
import Button from 'react-bootstrap/Button';
import { FaDownload } from 'react-icons/fa';
import PropTypes from 'prop-types';

const FieldAdminTableRowWrapper = ({ DeleteFieldButton }) => {
  const FieldAdminTableRow = (props) => {
    const { item, successCallback } = props;

    return (
      <tr>
        <td>
          {item.name}
        </td>
        <td className="icon-cell">
          <Button disabled className="export-field-button" variant="secondary">
            <FaDownload className="icon" />
          </Button>
        </td>
        <td className="icon-cell">
          <DeleteFieldButton fieldInfo={item} successCallback={successCallback} />
        </td>
      </tr>
    );
  };

  FieldAdminTableRow.propTypes = {
    item: PropTypes.shape({
      name: PropTypes.string,
      id: PropTypes.string
    }).isRequired,
    successCallback: PropTypes.func.isRequired
  };

  return FieldAdminTableRow;
};

export default FieldAdminTableRowWrapper;
