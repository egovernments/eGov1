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
package org.egov.infstr.security.utils;

public interface SecurityConstants {

	public static final String[] SQL_INJ_BLK_LIST = { "--", ";--", ";", "/*", "*/", "*", "@@", "@", "char", "nchar", "varchar", "nvarchar", "grant", "all", "union", "permissions", "alter", "begin", "cast", "create", "cursor", "declare", "delete", "drop",
			"end", "exec", "execute", "fetch", "insert", "kill", "join", "inner", "outer", "open", "select", "sys", "sysobjects", "syscolumns", "table", "update" };
	public static final String LOCATION_FIELD = "locationId";
	public static final String COUNTER_FIELD = "counterId";
	public static final String IPADDR_FIELD = "ipAddress";
	public static final String LOGINTYPE = "loginType";
	public static final String PWD_FIELD = "j_password";
	public static final String USERNAME_FIELD = "j_username";
	public static final String LOGIN_LOG_ID = "loginLogId";
	public static final String SSO_COMPLEATED = "sso_done";
	public static final String LOGIN_URI = "/login";
	public static final String PUBLIC_URI = "/public";
	
}
