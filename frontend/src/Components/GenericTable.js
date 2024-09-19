import React from 'react';
import { Table } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';

const GenericTable = (props) => {
  const {
    headers, items, TableRowElement, rowProps
  } = props;
  const { t } = useTranslation();

  if (!items || items.length === 0) {
    return (
      <p className="message">
        {t('noDataMessage')}
      </p>
    );
  }

  return (
    <Table>
      <thead>
        <tr className="table-headers">
          {headers.map((key) => (
            <th key={key}>
              {key}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {items.map((item) => (
          <TableRowElement
            key={item.id}
            item={item}
            {...rowProps}
          />
        ))}
      </tbody>
    </Table>
  );
};

GenericTable.propTypes = {
  headers: PropTypes.arrayOf(PropTypes.string).isRequired,
  items: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string
    })
  ).isRequired,
  rowProps: PropTypes.object,
  TableRowElement: PropTypes.func.isRequired
};

GenericTable.defaultProps = {
  rowProps: {}
};

export default GenericTable;
