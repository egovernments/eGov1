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
 * Action class to route to appropriate URL for drilldown from INBOX
 */
package org.egov.web.actions.payment;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.eis.service.EisCommonService;
import org.egov.infstr.config.dao.AppConfigValuesDAO;
import org.egov.model.advance.EgAdvanceRequisition;
import org.egov.model.payment.Paymentheader;
import org.egov.utils.FinancialConstants;
import org.egov.web.actions.voucher.BaseVoucherAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.eGov.src.transactions.VoucherTypeForULB;

/**    
 * @author mani
 */

@Results( {
		@Result(name = "billpayment", type = "redirectAction", location = "payment", params = { "namespace", "/payment", "method", "view" }),
		@Result(name = "advancepayment", type = "redirectAction", location = "payment", params = { "namespace", "/payment", "method", "advanceView" }),
		@Result(name = "directbankpayment", type = "redirectAction", location = "directBankPayment", params = { "namespace", "/payment","method", "viewInboxItem" }) ,
		@Result(name = "remitRecovery", type = "redirectAction", location = "remitRecovery", params = { "namespace", "/deduction","method", "viewInboxItem" }),
		@Result(name = "contractoradvancepayment", type = "redirectAction", location = "advancePayment", params = { "namespace", "/payment", "method", "viewInboxItem" })})
@Transactional(readOnly=true)	
public class BasePaymentAction extends BaseVoucherAction {    
	EisCommonService eisCommonService;
	private static Logger LOGGER=Logger.getLogger(BasePaymentAction.class);
	public void setEisCommonService(EisCommonService eisCommonService) {
		this.eisCommonService = eisCommonService;
	}  

	public BasePaymentAction()
	{
	super();
	}
	protected String				action				= "";
	protected String				paymentid			= "";
	private final String			BILLPAYMENT			= "billpayment";
	private final String			DIRECTBANKPAYMENT	= "directbankpayment";
	private final String            REMITTANCEPAYMENT   = "remitRecovery";
	public static final String ARF_TYPE="Contractor";
	
	protected static final String	ACTIONNAME			= "actionname";
	protected boolean canCheckBalance=false;
	
	public boolean isCanCheckBalance() {
		return canCheckBalance;
	}

	public void setCanCheckBalance(boolean canCheckBalance) {
		this.canCheckBalance = canCheckBalance;
	}
	@Autowired
	protected AppConfigValuesDAO appConfigValuesDAO;
	
	protected String showMode;
@Action(value="/payment/basePayment-viewInboxItems")
	public String viewInboxItems() {
		
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting viewInboxItems..... ");
		String result = null;
		Paymentheader paymentheader = (Paymentheader) persistenceService.find("from Paymentheader where id=?", Long.valueOf(paymentid));
		if(!validateOwner(paymentheader.getState())) 
		{
			return INVALIDPAGE;
		}
		getSession().put("paymentid", paymentid);
		if (paymentheader.getVoucherheader().getName().equalsIgnoreCase(FinancialConstants.PAYMENTVOUCHER_NAME_ADVANCE)) {
			EgAdvanceRequisition arf = (EgAdvanceRequisition)persistenceService.find("from EgAdvanceRequisition where arftype = ? and egAdvanceReqMises.voucherheader = ?",ARF_TYPE,paymentheader.getVoucherheader());
			if(arf != null)
				result = "contractoradvancepayment"; 
			else
				result = "advancepayment";
		}
		else if (paymentheader.getVoucherheader().getName().equalsIgnoreCase(FinancialConstants.PAYMENTVOUCHER_NAME_BILL) ||
				FinancialConstants.PAYMENTVOUCHER_NAME_SALARY.equalsIgnoreCase(paymentheader.getVoucherheader().getName()) ||
				FinancialConstants.PAYMENTVOUCHER_NAME_PENSION.equalsIgnoreCase(paymentheader.getVoucherheader().getName())) {
			result = BILLPAYMENT;
		}
		else if (paymentheader.getVoucherheader().getName().equalsIgnoreCase(FinancialConstants.PAYMENTVOUCHER_NAME_DIRECTBANK)) {
			result = DIRECTBANKPAYMENT;
		}
		else if (paymentheader.getVoucherheader().getName().equalsIgnoreCase(FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE)) {
			result = REMITTANCEPAYMENT;
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed viewInboxItems..... ");
		return result; 
	}
	
	//used only in create
	public boolean shouldshowVoucherNumber()
	{
		String  vNumGenMode="Manual";
		vNumGenMode= new VoucherTypeForULB().readVoucherTypes(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT);
			if(!"Auto".equalsIgnoreCase(vNumGenMode)){
				mandatoryFields.add("vouchernumber");
				return true	;
			}else
			{
			return false;
			}
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getPaymentid() {
		return paymentid;
	}
	
	public void setPaymentid(String paymentid) {
		this.paymentid = paymentid;
	}
	public String getShowMode() {
		return showMode;
	}
	public void setShowMode(String showMode) {
		this.showMode = showMode;
	}

	public String getFinConstExpendTypeContingency() {
		return FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT;
	}

	public String getFinConstExpendTypePension() {
		return FinancialConstants.STANDARD_EXPENDITURETYPE_PENSION;
	}

	
}
