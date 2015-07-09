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
package org.egov.model.budget;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.commons.EgwStatus;
import org.egov.infra.workflow.entity.StateAware;

public class BudgetReAppropriation extends StateAware{
	private Long id = null;
	private BudgetDetail budgetDetail;
	private BigDecimal additionAmount = new BigDecimal("0.0");
	private BigDecimal deductionAmount = new BigDecimal("0.0");
	private BigDecimal originalAdditionAmount = new BigDecimal("0.0");
	private BigDecimal originalDeductionAmount = new BigDecimal("0.0");
	private BigDecimal anticipatoryAmount = new BigDecimal("0.0");
	private EgwStatus status;
	private Date asOnDate;
	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(EgwStatus status) {
		this.status = status;
	}

	private BudgetReAppropriationMisc reAppropriationMisc;

	public BudgetReAppropriationMisc getReAppropriationMisc() {
		return reAppropriationMisc;
	}

	public void setReAppropriationMisc(BudgetReAppropriationMisc reAppropriationMisc) {
		this.reAppropriationMisc = reAppropriationMisc;
	}

	public BigDecimal getAnticipatoryAmount() {
		return anticipatoryAmount;
	}

	public void setAnticipatoryAmount(BigDecimal anticipatoryAmount) {
		this.anticipatoryAmount = anticipatoryAmount;
	}

	public BudgetDetail getBudgetDetail() {
		return budgetDetail;
	}
	
	public BigDecimal getAdditionAmount() {
		return additionAmount;
	}

	public void setAdditionAmount(BigDecimal additionAmount) {
		this.additionAmount = additionAmount;
	}

	public BigDecimal getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(BigDecimal deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public void setBudgetDetail(BudgetDetail budgetDetail) {
		this.budgetDetail = budgetDetail;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getStateDetails() {
		return null;
	}

	public void setOriginalAdditionAmount(BigDecimal originalAdditionAmount) {
		this.originalAdditionAmount = originalAdditionAmount;
	}

	public BigDecimal getOriginalAdditionAmount() {
		return originalAdditionAmount;
	}

	public void setOriginalDeductionAmount(BigDecimal originalDeductionAmount) {
		this.originalDeductionAmount = originalDeductionAmount;
	}

	public BigDecimal getOriginalDeductionAmount() {
		return originalDeductionAmount;
	}

	public Date getAsOnDate() {
		return asOnDate;
	}

	public void setAsOnDate(Date asOnDate) {
		this.asOnDate = asOnDate;
	}

	
}
