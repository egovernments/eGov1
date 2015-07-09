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
import java.util.Date;

public class TDSEntry {
	String natureOfDeduction = "";
	private String remittedOn;
	String voucherNumber = "";
	String voucherDate;
	String partyName = "";
	String partyCode = "";
	private String panNo = "";
	private String paymentVoucherNumber = "";
	private String chequeNumber = "";
	private String drawnOn;
	BigDecimal chequeAmount;
	private BigDecimal amount;
	private String month;
	private BigDecimal totalDeduction = BigDecimal.ZERO;
	private BigDecimal totalRemitted = BigDecimal.ZERO;
	private Integer egRemittanceGlDtlId;
	
	public Integer getEgRemittanceGlDtlId() {
		return egRemittanceGlDtlId;
	}
	public void setEgRemittanceGlDtlId(Integer egRemittanceGlDtlId) {
		this.egRemittanceGlDtlId = egRemittanceGlDtlId;
	}
	public String getNatureOfDeduction() {
		return natureOfDeduction;
	}
	public void setNatureOfDeduction(String departmentCode) {
		this.natureOfDeduction = departmentCode;
	}
	public String getRemittedOn() {
		return remittedOn;
	}
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String functionCode) {
		this.voucherNumber = functionCode;
	}
	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}
	public String getVoucherDate() {
		return voucherDate;
	}
	public String getPartyName() {
		return partyName;
	}
	public void setPartyName(String partyName) {
		this.partyName = partyName == null?"":partyName;
	}
	public String getPartyCode() {
		return partyCode;
	}
	public void setPartyCode(String partyCode) {
		this.partyCode = partyCode == null?"":partyCode;
	}
	public BigDecimal getChequeAmount() {
		return chequeAmount;
	}
	public void setChequeAmount(BigDecimal budgetEstimate) {
		this.chequeAmount = budgetEstimate;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}
	public String getChequeNumber() {
		return chequeNumber;
	}
	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}
	public String getPanNo() {
		return panNo;
	}
	public void setRemittedOn(String remittedOn) {
		this.remittedOn = remittedOn;
	}
	public void setPaymentVoucherNumber(String paymentVoucherNumber) {
		this.paymentVoucherNumber = paymentVoucherNumber;
	}
	public String getPaymentVoucherNumber() {
		return paymentVoucherNumber;
	}
	public void setDrawnOn(String drawnOn) {
		this.drawnOn = drawnOn;
	}
	public String getDrawnOn() {
		return drawnOn;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getMonth() {
		return month;
	}
	public void setTotalDeduction(BigDecimal totalDeduction) {
		this.totalDeduction = totalDeduction;
	}
	public BigDecimal getTotalDeduction() {
		return totalDeduction;
	}
	public void setTotalRemitted(BigDecimal totalRemitted) {
		this.totalRemitted = totalRemitted;
	}
	public BigDecimal getTotalRemitted() {
		return totalRemitted;
	}
}

