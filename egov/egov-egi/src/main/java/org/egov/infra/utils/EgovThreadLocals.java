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
package org.egov.infra.utils;

import javax.servlet.ServletContext;

public class EgovThreadLocals {

    private static ThreadLocal<String> domainName = new ThreadLocal<>();
    private static ThreadLocal<Long> userId = new ThreadLocal<>();
    private static ThreadLocal<ServletContext> servletContext = new ThreadLocal<>();
    private static ThreadLocal<String> tenantID = new ThreadLocal<>();
    private static ThreadLocal<String> cityCode = new ThreadLocal<>();

    public static String getCityCode() {
        return cityCode.get();
    }

    public static void setCityCode(final String citiCode) {
        cityCode.set(citiCode);
    }

    public static String getTenantID() {
        return tenantID.get();
    }

    public static void setTenantID(final String tenantJNDI) {
        tenantID.set(tenantJNDI);
    }

    public static String getDomainName() {
        return domainName.get();
    }

    public static void setDomainName(final String domName) {
        domainName.set(domName);
    }

    public static Long getUserId() {
        return userId.get();
    }

    public static void setUserId(final Long userid) {
        userId.set(userid);
    }

    public static ServletContext getServletContext() {
        return servletContext.get();
    }

    public static void setServletContext(final ServletContext servlContext) {
        servletContext.set(servlContext);
    }

    public static void clearValues() {
        setDomainName(null);
        setUserId(null);
        setTenantID(null);
        setCityCode(null);
        setServletContext(null);
    }

}
