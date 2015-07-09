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
package org.egov.wtms.web.contract;

import static org.egov.search.domain.Filter.queryStringFilter;
import static org.egov.search.domain.Filter.rangeFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.egov.search.domain.Filter;
import org.egov.search.domain.Filters;
import org.jboss.logging.Logger;

public class ConnectionSearchRequest {
	private String searchText;
	private String consumerCode;
    private String applicationName;
    private String locality;
    private String mobileNumber;
   
    private static final Logger logger = Logger.getLogger(ConnectionSearchRequest.class);

 

	 public String getConsumerCode() {
		return consumerCode;
	}
	public void setConsumerCode(String consumerCode) {	
		this.consumerCode = consumerCode;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(final String searchText) {
	        this.searchText = searchText;
	    }
	   public Filters searchFilters() {
	        final List<Filter> andFilters = new ArrayList<>();
	        andFilters.add(queryStringFilter("searchable.consumername", applicationName));
	        andFilters.add(queryStringFilter("clauses.consumercode", consumerCode));
	        andFilters.add(queryStringFilter("searchable.locality", locality));
	        andFilters.add(queryStringFilter("clauses.mobilenumber", mobileNumber));
	        if (logger.isDebugEnabled())
	            logger.debug("finished filters");
	        return Filters.withAndFilters(andFilters);
	    }
    public String searchQuery() {
        return searchText;
    }

    
}
