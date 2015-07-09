/**
 * eGov suite of products aim to improve the internal efficiency,transparency, 
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation 
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or 
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this 
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It 
	   is required that all modified versions of this material be marked in 
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program 
	   with regards to rights under trademark law for use of the trade names 
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.infstr.services;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;

public class Page {

	final private List results;
	final private Integer pageSize;
	final private int pageNumber;

	/**
	 * @param query Hibernate Query object that is executed to obtain list
	 * @param pageNumber The page number
	 * @param pageSize No of records to be returned. If this parameter is null, all the records are returned. The page parameter will be ignored in this case.
	 */
	public Page(final Query query, Integer pageNumber, final Integer pageSize) {
		if (pageNumber < 1) {
			pageNumber = 1;
		}

		this.pageNumber = pageNumber;
		if (pageSize != null && pageSize > 0) {
			query.setFirstResult((pageNumber - 1) * pageSize);
			query.setMaxResults(pageSize + 1);
			this.pageSize = pageSize;
		} else {
			this.pageSize = -1;
		}
		this.results = query.list();
	}

	/**
	 * @param criteria Hibernate Criteria object that is executed to obtain list
	 * @param pageNumber The page number
	 * @param pageSize No of records to be returned. If this parameter is null, all the records are returned. The page parameter will be ignored in this case.
	 */
	public Page(final Criteria criteria, Integer pageNumber, final Integer pageSize) {
		if (pageNumber < 1) {
			pageNumber = 1;
		}

		this.pageNumber = pageNumber;

		if (pageSize != null && pageSize > 0) {
			criteria.setFirstResult((pageNumber - 1) * pageSize);
			criteria.setMaxResults(pageSize + 1);
			this.pageSize = pageSize;
		} else {
			this.pageSize = -1;
		}
		this.results = criteria.list();
	}

	public boolean isNextPage() {
		return (this.pageSize != -1 && this.results.size() > this.pageSize);
	}

	public boolean isPreviousPage() {
		return this.pageNumber > 0;
	}

	public List getList() {
		return isNextPage() ? this.results.subList(0, this.pageSize) : this.results;
	}

	public Integer getPageNo() {
		return this.pageNumber;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}
}
