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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.egov.utils.Constants;


public class ChequeIssueRegisterDisplay {
	private Date chequeDate;
	private String chequeNumber = "";
	private BigDecimal chequeAmount;
	private String voucherNumber = "";
	private String voucherName = "";
	private Date voucherDate;
	private String chequeStatus = "";
	private String payTo = "";
	private String billNumber = "";
	private Date billDate;
	private String type = "";
	private BigDecimal vhId;
	private String serialNo;
	private List<Long> voucherheaderId = new ArrayList<Long>();

	public String getChequeDate() {
		return chequeDate==null?"":Constants.DDMMYYYYFORMAT1.format(chequeDate);
	}
	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}
	public String getChequeNumber() {
		return chequeNumber;
	}
	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}
	public BigDecimal getChequeAmount() {
		return chequeAmount;
	}
	public void setChequeAmount(BigDecimal chequeAmount) {
		this.chequeAmount = chequeAmount;
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
	public String getChequeStatus() {
		return chequeStatus;
	}
	public void setChequeStatus(String chequeStatus) {
		this.chequeStatus = chequeStatus;
	}
	public String getPayTo() {
		return payTo;
	}
	public void setPayTo(String payTo) {
		this.payTo = payTo;
	}
	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	public String getBillNumber() {
		return billNumber;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}
	public Date getBillDate() {
		return billDate;
	}
	public void setVoucherheaderId(List<Long> voucherheaderId) {
		this.voucherheaderId = voucherheaderId;
	}
	public List<Long> getVoucherheaderId() {
		return voucherheaderId;
	}
	public String getBillNumberAndDate() {
		if("MULTIPLE".equalsIgnoreCase(billNumber))
			return "MULTIPLE"; 
		if(!"".equals(billNumber) && billDate!=null)
			billNumber = billNumber.concat(" , ").concat(Constants.DDMMYYYYFORMAT1.format(billDate));
		return billNumber;
	}
	public String getVoucherNumberAndDate() {
		if("MULTIPLE".equalsIgnoreCase(voucherNumber))
			return "MULTIPLE"; 
		if(!"".equals(voucherNumber) && voucherDate!=null)
			voucherNumber = voucherNumber.concat(" , ").concat(Constants.DDMMYYYYFORMAT1.format(voucherDate));
		return voucherNumber;
	}
	public void setVoucherName(String voucherName) {
		this.voucherName = voucherName;
	}
	public String getVoucherName() {
		return voucherName;
	}
	public BigDecimal getVhId() {
		return vhId;
	}
	public void setVhId(BigDecimal vhId) {
		this.vhId = vhId;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	
}
