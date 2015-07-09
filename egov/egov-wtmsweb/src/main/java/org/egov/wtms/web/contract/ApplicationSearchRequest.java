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
import static org.egov.search.domain.Filter.termsStringFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.egov.search.domain.Filter;
import org.egov.search.domain.Filters;
import org.jboss.logging.Logger;

public class ApplicationSearchRequest {
	private String searchText;
    private String moduleName;
    private String applicationType;
    private String applicationNumber;
    private String applicationCode;
    private String applicationName;
    private String appMobileNo;
    private String fromDate;
    private String toDate;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dtft = new SimpleDateFormat("dd/MM/yyyy");

    private static final Logger logger = Logger.getLogger(ApplicationSearchRequest.class);

   
    public void setFromDate(final String fromDate) {
        if (null != fromDate)
            try {
                if (logger.isDebugEnabled())
                    logger.debug("Date Range From start.. :" + ft.format(dtft.parse(fromDate)));
                this.fromDate = ft.format(dtft.parse(fromDate));
            } catch (final ParseException e) {
                e.printStackTrace();
            }
    }

    public void setToDate(final String toDate) {
        if (null != toDate)
            try {
                if (logger.isDebugEnabled())
                    logger.debug("Date Range Till .. :" + ft.format(dtft.parse(toDate)));
                this.toDate = ft.format(dtft.parse(toDate));
            } catch (final ParseException e) {
                e.printStackTrace();
            }
    }
 

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public String getApplicationCode() {
		return applicationCode;
	}

	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getAppMobileNo() {
		return appMobileNo;
	}

	public void setAppMobileNo(String appMobileNo) {
		this.appMobileNo = appMobileNo;
	}

	public String getFromDate() {
		return fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	 public void setSearchText(final String searchText) {
	        this.searchText = searchText;
	    }
	   public Filters searchFilters() {
	        final List<Filter> andFilters = new ArrayList<>(0);
	        andFilters.add(queryStringFilter("searchable.applicationnumber", applicationNumber));
	        andFilters.add(termsStringFilter("clauses.modulename", moduleName));
	        andFilters.add(termsStringFilter("clauses.applicationtype", applicationType));
	        andFilters.add(queryStringFilter("searchable.applicantname", applicationName));
	        andFilters.add(queryStringFilter("searchable.consumercode", applicationCode));
	        andFilters.add(queryStringFilter("searchable.mobilenumber", appMobileNo));
	        andFilters.add(rangeFilter("searchable.applicationdate", fromDate, toDate));
	        if (logger.isDebugEnabled())
	            logger.debug("finished filters");
	        logger.info("$$$$$$$$$$$$$$$$ Filters : "+andFilters);
	        return Filters.withAndFilters(andFilters);
	    }
    public String searchQuery() {
        return searchText;
    }

    
}
