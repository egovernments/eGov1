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
/**
 * 
 */
package org.egov.web.actions.bill;

import java.util.Map;

import org.egov.infstr.services.PersistenceService;
import org.egov.infra.admin.master.entity.Department;
import org.egov.model.bills.EgBillregister;
import org.egov.model.voucher.VoucherDetails;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.eGov.src.domain.EGBillPayeeDetails;

/**
 * @author mani
 *
 */
@Transactional(readOnly=true)
public class BillReport {
	
	private PersistenceService persistenceService;
	private Department department;
	public PersistenceService getPersistenceService() {
		return persistenceService;
	}
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public VoucherDetails getVoucherDetails() {
		return voucherDetails;
	}
	public void setVoucherDetails(VoucherDetails voucherDetails) {
		this.voucherDetails = voucherDetails;
	}
	public EgBillregister getBill() {
		return bill;
	}
	public void setBill(EgBillregister bill) {
		this.bill = bill;
	}
	public EGBillPayeeDetails getBillPayeeDetails() {
		return billPayeeDetails;
	}
	public void setBillPayeeDetails(EGBillPayeeDetails billPayeeDetails) {
		this.billPayeeDetails = billPayeeDetails;
	}
	VoucherDetails voucherDetails;
	EgBillregister bill;
	EGBillPayeeDetails billPayeeDetails;
	Map<String,Object> budgetApprDetails;
	public Map<String,Object>  getBudgetApprDetails() {
		return budgetApprDetails;
	}
	public void setBudgetAppropriationdetails(Map<String,Object>  budgetAppropriationdetails) {
		this.budgetApprDetails = budgetAppropriationdetails;
	}
	/**
	 * @param persistenceService
	 * @param billDetails
	 * @param bill
	 */
	public BillReport(PersistenceService persistenceService, VoucherDetails voucherDetails, EgBillregister bill,Map<String,Object> budgetAppropriationdetails) {
		super();
		this.persistenceService = persistenceService;
		this.voucherDetails = voucherDetails;
		this.bill = bill;
		this.budgetApprDetails=budgetAppropriationdetails;
	}
	

}

 
