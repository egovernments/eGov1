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
package org.egov.model.advance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.egov.commons.Accountdetailtype;
import org.egov.model.recoveries.Recovery;

public class EgAdvanceReqPayeeDetails implements Serializable{
	private Long id;
	private Date lastUpdatedTime;
	private EgAdvanceRequisitionDetails egAdvanceRequisitionDetails;
	private Recovery recovery;
	private Accountdetailtype accountDetailType;
	private Integer accountdetailKeyId;
	private BigDecimal debitAmount;
	private BigDecimal creditAmount;	
	private String narration;
	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}


	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}


	public EgAdvanceRequisitionDetails getEgAdvanceRequisitionDetails() {
		return egAdvanceRequisitionDetails;
	}


	public void setEgAdvanceRequisitionDetails(
			EgAdvanceRequisitionDetails egAdvanceRequisitionDetails) {
		this.egAdvanceRequisitionDetails = egAdvanceRequisitionDetails;
	}

	public Recovery getRecovery() {
		return recovery;
	}

	public void setRecovery(Recovery recovery) {
		this.recovery = recovery;
	}

	public Accountdetailtype getAccountDetailType() {
		return accountDetailType;
	}


	public void setAccountDetailType(Accountdetailtype accountDetailType) {
		this.accountDetailType = accountDetailType;
	}


	public Integer getAccountdetailKeyId() {
		return accountdetailKeyId;
	}


	public void setAccountdetailKeyId(Integer accountdetailKeyId) {
		this.accountdetailKeyId = accountdetailKeyId;
	}


	public BigDecimal getDebitAmount() {
		return debitAmount;
	}


	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}


	public BigDecimal getCreditAmount() {
		return creditAmount;
	}


	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}


	public String getNarration() {
		return narration;
	}


	public void setNarration(String narration) {
		this.narration = narration;
	}


	public EgAdvanceReqPayeeDetails(Long id, Date lastUpdatedTime,
			EgAdvanceRequisitionDetails egAdvanceRequisitionDetails, Recovery recovery,
			Accountdetailtype accountDetailType, Integer accountdetailKeyId,
			BigDecimal debitAmount, BigDecimal creditAmount, String narration) {
		super();
		this.id = id;
		this.lastUpdatedTime = lastUpdatedTime;
		this.egAdvanceRequisitionDetails = egAdvanceRequisitionDetails;
		this.recovery = recovery;
		this.accountDetailType = accountDetailType;
		this.accountdetailKeyId = accountdetailKeyId;
		this.debitAmount = debitAmount;
		this.creditAmount = creditAmount;
		this.narration = narration;
	}


	public EgAdvanceReqPayeeDetails() {
		super();
		
	}
	
	
}
