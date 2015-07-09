/*******************************************************************************
 * eGov suite of products aim to improve the internal efficiency,transparency, 
 *    accountability and the service delivery of the government  organizations.
 * 
 *     Copyright (C) <2015>  eGovernments Foundation
 * 
 *     The updated version of eGov suite of products as by eGovernments Foundation 
 *     is available at http://www.egovernments.org
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or 
 *     http://www.gnu.org/licenses/gpl.html .
 * 
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 * 
 * 	1) All versions of this program, verbatim or modified must carry this 
 * 	   Legal Notice.
 * 
 * 	2) Any misrepresentation of the origin of the material is prohibited. It 
 * 	   is required that all modified versions of this material be marked in 
 * 	   reasonable ways as different from the original version.
 * 
 * 	3) This license does not grant any rights to any user of the program 
 * 	   with regards to rights under trademark law for use of the trade names 
 * 	   or trademarks of eGovernments Foundation.
 * 
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
package org.egov.web.actions.report;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class IEStatementEntry {
	private String glCode;
	//private String glCode;
	private String accountName;
	private String scheduleNo;
	private BigDecimal budgetAmount;
	private String majorCode;
	private Map<String ,BigDecimal> scheduleWiseTotal= new HashMap<String, BigDecimal>();
	private Map<String,BigDecimal> netAmount = new HashMap<String, BigDecimal>();
	private Map<String,BigDecimal> previousYearAmount = new HashMap<String, BigDecimal>();
	
	private boolean displayBold = false;
	
	public IEStatementEntry() {
		
	}
	
	public IEStatementEntry(String accountCode,String accountName,Map<String,BigDecimal> netAmount,Map<String,BigDecimal> previousYearAmount,boolean displayBold) {
		this.glCode = accountCode;
		this.accountName = accountName;
		this.previousYearAmount=previousYearAmount;
		this.netAmount=netAmount;
		this.displayBold = displayBold;
	}
	
   public IEStatementEntry(String accountCode,String accountName,String scheduleNo,boolean displayBold) {
		this.glCode = accountCode;
		this.accountName = accountName;
		this.scheduleNo = scheduleNo;
		this.displayBold = displayBold;
	}
   public IEStatementEntry(String accountCode,String accountName,String scheduleNo,String majorCode,boolean displayBold) {
		this.glCode = accountCode;
		this.accountName = accountName;
		this.scheduleNo = scheduleNo;
		this.majorCode=majorCode;
		this.displayBold = displayBold;
	}
   
   public IEStatementEntry(String glcode,String accountName,boolean displayBold) {
		this.glCode = glcode;
		this.accountName = accountName;
		this.displayBold = displayBold;
	}
   public BigDecimal getBudgetAmount() {
		return budgetAmount;
	}

	public void setBudgetAmount(BigDecimal budgetAmount) {
		this.budgetAmount = budgetAmount;
	}
	
	public String getGlCode() {
		return glCode;
	}
	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}
	

	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getScheduleNo() {
		return scheduleNo;
	}
	public void setScheduleNo(String scheduleNo) {
		this.scheduleNo = scheduleNo;
	}
	public Map<String, BigDecimal> getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(Map<String, BigDecimal> netAmount) {
		this.netAmount = netAmount;
	}

	public Map<String, BigDecimal> getPreviousYearAmount() {
		return previousYearAmount;
	}

	public void setPreviousYearAmount(Map<String, BigDecimal> previousYearAmount) {
		this.previousYearAmount = previousYearAmount;
	}

	public boolean isDisplayBold() {
		return displayBold;
	}
	public void setDisplayBold(boolean displayBold) {
		this.displayBold = displayBold;
	}

	public String getMajorCode() {
		return majorCode;
	}

	public void setMajorCode(String majorCode) {
		this.majorCode = majorCode;
	}
}
