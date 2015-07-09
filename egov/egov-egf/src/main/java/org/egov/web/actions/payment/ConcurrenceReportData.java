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
package org.egov.web.actions.payment;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.transaction.annotation.Transactional;
@Transactional(readOnly=true)
public class ConcurrenceReportData {
	String departmentName;
	String functionCode;
	String billNumber;
	Date billDate;
	String bpvNumber;
	Date bpvDate;
	String uac;
	String bankName;
	String bankAccountNumber;
	String bpvAccountCode;
	BigDecimal fundId;
	BigDecimal amount;
	
	public ConcurrenceReportData(){};
	
	public ConcurrenceReportData( String bpvAccountCode, BigDecimal amount,String bpvNumber) {
		super();
		this.amount = amount;
		this.bpvAccountCode = bpvAccountCode;
		this.bpvNumber=bpvNumber;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getFunctionCode() {
		return functionCode;
	}
	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	public String getUac() {
		return uac;
	}
	public void setUac(String uac) {
		this.uac = uac;
	}
	public BigDecimal getFundId() {
		return fundId;
	}
	public void setFundId(BigDecimal fundId) {
		this.fundId = fundId;
	}
	public String getBillNumber() {
		return billNumber;
	}
	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	public Date getBillDate() {
		return billDate;
	}
	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}
	public String getBpvNumber() {
		return bpvNumber;
	}
	public void setBpvNumber(String bpvNumber) {
		this.bpvNumber = bpvNumber;
	}
	public Date getBpvDate() {
		return bpvDate;
	}
	public void setBpvDate(Date bpvDate) {
		this.bpvDate = bpvDate;
	}
	public String getBpvAccountCode() {
		return bpvAccountCode;
	}
	public void setBpvAccountCode(String bpvAccountCode) {
		this.bpvAccountCode = bpvAccountCode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
