import React from 'react';
import {
  Nav, ListGroup, Button, DropdownButton, Dropdown, ButtonGroup
} from 'react-bootstrap';
import PropTypes from 'prop-types';

// maximum number of pages to be displayed excluding first and last pages.
const maxPages = 8;
const pageJump = 4;
class Pagination extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showTitle: 20,
      startIndex: 1,
      endIndex: maxPages,
      pageNumbers: [...Array(this.props.nPages + 1).keys()].slice(1)
    };
    this.setPage = this.setPage.bind(this);
    this.nextPage = this.nextPage.bind(this);
    this.prevPage = this.prevPage.bind(this);
    this.jumpForwardPages = this.jumpForwardPages.bind(this);
    this.jumpBackwardPages = this.jumpBackwardPages.bind(this);
  }

  setShow(e) {
    this.setState({ showTitle: e });
    this.props.setRecordsPerPage(parseInt(e, 10));
  }

  setPage(page) {
    const { nPages } = this.props;
    const { startIndex, endIndex } = this.state;
    if (page === 1) {
      this.setState({ startIndex: 1, endIndex: maxPages });
    }
    if (page === nPages) {
      this.setState({ startIndex: nPages - maxPages, endIndex: nPages - 1 });
    }
    if (page === startIndex + 1 && startIndex - 1 !== 0) {
      this.setState({ startIndex: startIndex - 1, endIndex: endIndex - 1 });
    }
    if (page === endIndex && endIndex + 1 !== nPages) {
      this.setState({ startIndex: startIndex + 1, endIndex: endIndex + 1 });
    }
    this.props.setCurrentPage(page);
  }

  nextPage() {
    const { currentPage, nPages } = this.props;
    const { startIndex, endIndex } = this.state;

    if (currentPage < nPages) { this.props.setCurrentPage(currentPage + 1); }
    if (currentPage === endIndex - 1 && endIndex + 1 !== nPages) {
      this.setState({ startIndex: startIndex + 1, endIndex: endIndex + 1 });
    }
  }

  prevPage() {
    const { currentPage } = this.props;
    const { startIndex, endIndex } = this.state;

    if (currentPage > 1) { this.props.setCurrentPage(currentPage - 1); }
    if (currentPage === startIndex + 1 && startIndex - 1 !== 0) {
      this.setState({ startIndex: startIndex - 1, endIndex: endIndex - 1 });
    }
  }

  jumpForwardPages() {
    const { startIndex, endIndex } = this.state;
    const { nPages } = this.props;

    const newEnd = nPages - endIndex > pageJump ? endIndex + pageJump : nPages - 1;
    this.setState({ startIndex: startIndex + pageJump, endIndex: newEnd });
  }

  jumpBackwardPages() {
    const { startIndex, endIndex } = this.state;
    const newStart = startIndex - pageJump > 1 ? startIndex - pageJump : 1;
    this.setState({ startIndex: newStart, endIndex: endIndex - pageJump });
  }

  displayElementIf(condition) {
    return (condition ? {} : { display: 'none' });
  }

  render() {
    const { startIndex, endIndex, pageNumbers } = this.state;
    const { currentPage, nPages } = this.props;

    return (
      <Nav className="pagination">
        <ListGroup className="pagination-list">
          <ListGroup.Item className="page-item">
            <Button name="previous-button" className="page-link" href="#" onClick={this.prevPage}>
              Previous
            </Button>
          </ListGroup.Item>
          { pageNumbers.length > 10 ? (
            <>
              <ListGroup.Item name={`item-${pageNumbers[0]}`} className="page-item" active={currentPage === pageNumbers[0]}>
                <Button name={`button-${pageNumbers[0]}`} onClick={() => this.setPage(pageNumbers[0])} className="page-link" href="#">
                  {pageNumbers[0]}
                </Button>
              </ListGroup.Item>
              <ListGroup.Item key="jumpBackward" className="page-item" style={this.displayElementIf(startIndex - 1 >= 1)}>
                <Button name="jump-backward-button" onClick={this.jumpBackwardPages} className="page-link" href="#">
                  ...
                </Button>
              </ListGroup.Item>
              { pageNumbers.slice(startIndex, endIndex).map((pgNumber) => (
                <ListGroup.Item key={pgNumber} name={`item-${pgNumber}`} className="page-item" active={currentPage === pgNumber}>
                  <Button name={`button-${pgNumber}`} onClick={() => this.setPage(pgNumber)} className="page-link" href="#">
                    {pgNumber}
                  </Button>
                </ListGroup.Item>
              ))}
              <ListGroup.Item key="jumpForward" className="page-item" style={this.displayElementIf(nPages - endIndex > 1)}>
                <Button name="jump-forward-button" onClick={this.jumpForwardPages} className="page-link" href="#">
                  ...
                </Button>
              </ListGroup.Item>
              <ListGroup.Item key={nPages} name={`item-${nPages}`} className="page-item" active={currentPage === nPages}>
                <Button name={`button-${nPages}`} onClick={() => this.setPage(nPages)} className="page-link" href="#">
                  {nPages}
                </Button>
              </ListGroup.Item>
            </>
          ) : (
            pageNumbers.map((pgNumber) => (
              <ListGroup.Item key={pgNumber} name={`item-${pgNumber}`} className="page-item" active={currentPage === pgNumber}>
                <Button name={`button-${pgNumber}`} onClick={() => this.setPage(pgNumber)} className="page-link" href="#">
                  {pgNumber}
                </Button>
              </ListGroup.Item>
            ))
          )}
          <ListGroup.Item className="page-item">
            <Button name="next-button" className="page-link" href="#" onClick={this.nextPage}>
              Next
            </Button>
          </ListGroup.Item>
        </ListGroup>
        <DropdownButton
          name="records-per-page-dropdown"
          as={ButtonGroup}
          variant="outline-primary"
          title={`Show ${this.state.showTitle}`}
          onSelect={(e) => this.setShow(e)}
        >
          <Dropdown.Item eventKey="10">Show 10</Dropdown.Item>
          <Dropdown.Item eventKey="20">Show 20</Dropdown.Item>
          <Dropdown.Item eventKey="30">Show 30</Dropdown.Item>
        </DropdownButton>
      </Nav>
    );
  }
}

Pagination.propTypes = {
  nPages: PropTypes.number.isRequired,
  currentPage: PropTypes.number.isRequired,
  setCurrentPage: PropTypes.func.isRequired,
  setRecordsPerPage: PropTypes.func.isRequired
};
export default Pagination;
