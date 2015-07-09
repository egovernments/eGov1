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
import java.util.List;

import org.egov.model.instrument.InstrumentVoucher;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class BankBookEntry {
	String voucherNumber;
	Date voucherDate;
	String particulars;
	BigDecimal amount;
	private BigDecimal creditAmount;
	private BigDecimal debitAmount;
	String chequeNumber;
	String chequeDate;
	String type;
	private String chequeDetail;
	private String glCode;
	private BigDecimal receiptAmount;
	private BigDecimal paymentAmount;
	private String instrumentStatus;
	private BigDecimal voucherId;
	private List<InstrumentVoucher>	instrumentVouchers;
	
	public BankBookEntry(){};
	
	public BankBookEntry(String particulars, BigDecimal amount, String type,
			BigDecimal receiptAmount, BigDecimal paymentAmount) {
		super();
		this.particulars = particulars;
		this.amount = amount;
		this.type = type;
		this.receiptAmount = receiptAmount;
		this.paymentAmount = paymentAmount;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	public Date getVoucherDate() {
		return voucherDate;
	}
	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getChequeNumber() {
		return chequeNumber;
	}
	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setChequeDetail(String chequeDetail) {
		this.chequeDetail = chequeDetail;
	}
	public String getChequeDetail() {
		return chequeDetail;
	}
	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}
	public BigDecimal getCreditAmount() {
		return creditAmount;
	}
	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}
	public BigDecimal getDebitAmount() {
		return debitAmount;
	}
	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}
	public String getGlCode() {
		return glCode;
	}
	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}
	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setInstrumentStatus(String instrumentStatus) {
		this.instrumentStatus = instrumentStatus;
	}

	public String getInstrumentStatus() {
		return instrumentStatus;
	}

	public void setVoucherId(BigDecimal voucherId) {
		this.voucherId = voucherId;
	}

	public BigDecimal getVoucherId() {
		return voucherId;
	}

	public void setInstrumentVouchers(List<InstrumentVoucher> chequeDetails) {
		this.instrumentVouchers=chequeDetails;
		
	}
	public List<InstrumentVoucher> getInstrumentVouchers() {
		return instrumentVouchers;
	}
}
