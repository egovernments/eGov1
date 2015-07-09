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
package org.egov.commons.dao;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccounts;
import org.egov.exceptions.EGOVException;
import org.egov.infstr.ValidationException;
import org.egov.infstr.dao.GenericDAO;

public interface ChartOfAccountsDAO extends GenericDAO{
//	public Collection getAccountCodeList();
	@Deprecated
	public Collection getAccountCodeListForDetails();
	@Deprecated
	public CChartOfAccounts findCodeByPurposeId(int purposeId) throws Exception;
	public CChartOfAccounts getCChartOfAccountsByGlCode(String glCode);
	@Deprecated
	public List getChartOfAccountsForTds();
	@Deprecated
	public int getDetailTypeId(String glCode,Connection connection) throws Exception;
	@Deprecated
	public int getDetailTypeIdByName(String glCode,Connection connection,String name) throws Exception;
	public List getGlcode(String minGlcode,String maxGlcode,String majGlcode) throws Exception;
	public List<CChartOfAccounts> getActiveAccountsForType(char c)throws EGOVException;
	public List<CChartOfAccounts> getAccountCodeByPurpose(Integer purposeId) throws EGOVException;
	public List<CChartOfAccounts> getNonControlCodeList() throws EGOVException;
	public List<Accountdetailtype> getAccountdetailtypeListByGLCode(final String glCode) throws EGOVException;
	public Accountdetailtype getAccountDetailTypeIdByName(String glCode,String name) throws Exception;
	public List<CChartOfAccounts> getDetailedAccountCodeList()throws EGOVException;
	public List<CChartOfAccounts> getActiveAccountsForTypes(char[] type)throws ValidationException;
	public List<CChartOfAccounts> getAccountCodeByListOfPurposeId(Integer[] purposeId) throws ValidationException;
	public List<CChartOfAccounts> getListOfDetailCode(final String glCode) throws ValidationException;
	public List<CChartOfAccounts> getBankChartofAccountCodeList();
	}