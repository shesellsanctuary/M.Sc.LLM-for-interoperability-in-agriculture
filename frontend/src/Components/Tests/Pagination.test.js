/* eslint-disable no-shadow */
import React from 'react';
import { cleanup } from '@testing-library/react';
import { mount } from 'enzyme';

import Pagination from '../Pagination';

afterEach(cleanup);

describe('Pagination', () => {
  const props = {
    nPages: 10,
    currentPage: 1,
    setCurrentPage: jest.fn(),
    setRecordsPerPage: jest.fn(),
  };

  const expectedInitalList = [
    'Previous',
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    '10',
    'Next'
  ];

  beforeEach(() => {
    props.setCurrentPage.mockClear();
  });

  const wrapper = mount(<Pagination {...props} />);

  it('renders list group with 12 items', () => {
    expect(wrapper.find('ListGroupItem').length).toEqual(12);
    wrapper.find('ListGroupItem').forEach((listItem, index) => {
      expect(listItem.find('.page-link').at(0).text()).toEqual(expectedInitalList[index]);
    });
    expect(wrapper.find('[name="item-1"]').first().prop('active')).toEqual(true);
  });

  it('goes to next page on next button click', () => {
    wrapper.find('[name="next-button"]').last().simulate('click');
    wrapper.update();
    expect(props.setCurrentPage).toHaveBeenCalledWith(2);
  });

  it('goes to selected page on page click', () => {
    wrapper.find('[name="button-5"]').last().simulate('click');
    wrapper.update();
    expect(props.setCurrentPage).toHaveBeenCalledWith(5);
  });

  describe('When more than 10 pages are given', () => {
    const props = {
      nPages: 20,
      currentPage: 3,
      setCurrentPage: jest.fn(),
      setRecordsPerPage: jest.fn(),
    };

    const expectedInitalList = [
      'Previous',
      '1',
      '...',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '...',
      '20',
      'Next'
    ];

    beforeEach(() => {
      props.setCurrentPage.mockClear();
    });

    const wrapper = mount(<Pagination {...props} />);

    it('renders list group with 12 items', () => {
      expect(wrapper.find('ListGroupItem').length).toEqual(13);
      wrapper.find('ListGroupItem').forEach((listItem, index) => {
        expect(listItem.find('.page-link').at(0).text()).toEqual(expectedInitalList[index]);
      });
      expect(wrapper.find('[name="item-3"]').first().prop('active')).toEqual(true);
      expect(wrapper.find('[name="jump-backward-button"]').first().parent()
        .prop('style')).toEqual({ display: 'none' });
      expect(wrapper.find('[name="jump-forward-button"]').first().parent()
        .prop('style')).toEqual({});
    });

    it('goes to previous page on previous button click', () => {
      wrapper.find('[name="previous-button"]').last().simulate('click');
      wrapper.update();
      expect(props.setCurrentPage).toHaveBeenCalledWith(2);
    });

    it('jumps to following pages on jump forward button click', () => {
      wrapper.find('[name="jump-forward-button"]').first().simulate('click');
      wrapper.find('[name="jump-forward-button"]').first().simulate('click');
      wrapper.update();
      expect(wrapper.state('startIndex')).toEqual(9);
      expect(wrapper.state('endIndex')).toEqual(16);
    });

    it('jumps to previous pages on jump backwards button click', () => {
      wrapper.find('[name="jump-backward-button"]').first().simulate('click');
      wrapper.update();
      expect(wrapper.state('startIndex')).toEqual(5);
      expect(wrapper.state('endIndex')).toEqual(12);
    });

    it('updates indexes on previous page next to startIndex click', () => {
      wrapper.setProps({ currentPage: 6 });
      wrapper.find('[name="previous-button"]').last().simulate('click');
      wrapper.update();

      expect(props.setCurrentPage).toHaveBeenCalledWith(5);
      expect(wrapper.state('startIndex')).toEqual(4);
      expect(wrapper.state('endIndex')).toEqual(11);
    });

    it('updates indexes on next page next to endIndex click', () => {
      wrapper.setProps({ currentPage: 10 });
      wrapper.find('[name="next-button"]').last().simulate('click');
      wrapper.update();

      expect(props.setCurrentPage).toHaveBeenCalledWith(11);
      expect(wrapper.state('startIndex')).toEqual(5);
      expect(wrapper.state('endIndex')).toEqual(12);
    });

    describe('setPage', () => {
      it('goes to first page on page click and updates indexes', () => {
        wrapper.find('[name="button-1"]').last().simulate('click');
        wrapper.update();
        expect(props.setCurrentPage).toHaveBeenCalledWith(1);
        expect(wrapper.state('startIndex')).toEqual(1);
        expect(wrapper.state('endIndex')).toEqual(8);
      });

      it('goes to last page on page click and updates indexes', () => {
        wrapper.find('[name="button-20"]').last().simulate('click');
        wrapper.update();
        expect(props.setCurrentPage).toHaveBeenCalledWith(20);
        expect(wrapper.state('startIndex')).toEqual(12);
        expect(wrapper.state('endIndex')).toEqual(19);
      });

      it('decreases indexes on page next to startIndex click', () => {
        wrapper.find('[name="button-13"]').last().simulate('click');
        wrapper.update();
        expect(props.setCurrentPage).toHaveBeenCalledWith(13);
        expect(wrapper.state('startIndex')).toEqual(11);
        expect(wrapper.state('endIndex')).toEqual(18);
      });

      it('increases indexes on page next to endIndex click', () => {
        wrapper.find('[name="button-18"]').last().simulate('click');
        wrapper.update();
        expect(props.setCurrentPage).toHaveBeenCalledWith(18);
        expect(wrapper.state('startIndex')).toEqual(12);
        expect(wrapper.state('endIndex')).toEqual(19);
      });
    });

    it('calls setRecordsPerPage on dropdown select', () => {
      wrapper.find('.dropdown-toggle').last().simulate('click');
      wrapper.update();
      wrapper.find('.dropdown-item').at(1).simulate('click');
      wrapper.update();
      expect(props.setRecordsPerPage).toHaveBeenCalledWith(10);
    });
  });
});
