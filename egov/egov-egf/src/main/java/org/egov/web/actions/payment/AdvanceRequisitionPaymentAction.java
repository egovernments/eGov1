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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.commons.Bankaccount;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.Fundsource;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.egf.commons.EgovCommon;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.infstr.workflow.Action;
import org.egov.model.advance.EgAdvanceReqPayeeDetails;
import org.egov.model.advance.EgAdvanceRequisition;
import org.egov.model.advance.EgAdvanceRequisitionDetails;
import org.egov.model.advance.EgAdvanceRequisitionMis;
import org.egov.model.bills.Miscbilldetail;
import org.egov.model.payment.Paymentheader;
import org.egov.services.payment.PaymentService;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.Constants;
import org.egov.web.actions.voucher.BaseVoucherAction;
import org.springframework.transaction.annotation.Transactional;
@Transactional(readOnly=true)
public class AdvanceRequisitionPaymentAction extends BaseVoucherAction{
	private EgAdvanceRequisition advanceRequisition = new EgAdvanceRequisition();
	private static final Logger	LOGGER	= Logger.getLogger(AdvanceRequisitionPaymentAction.class);
	private Bankaccount bankaccount = new Bankaccount();
	private PaymentService paymentService;
	private SimpleWorkflowService<Paymentheader> paymentWorkflowService;
	private Paymentheader paymentheader;
	private Date voucherDate = new Date();
	private Map<String,String> modeOfCollectionMap = new HashMap<String, String>(); 
	private String paymentMode = "cheque";
	private String narration;
	private VoucherService voucherService;
	private EgovCommon egovCommon;
	private Fund fund;
	
	@Override
	public void prepare() {
		super.prepare();
		modeOfCollectionMap.put("others", "Others");
		modeOfCollectionMap.put("cash", "Cash");
		modeOfCollectionMap.put("cheque", "Cheque");
		EgovMasterDataCaching masterCache = EgovMasterDataCaching.getInstance();
		addDropdownData("designationList", Collections.EMPTY_LIST);
		addDropdownData("userList", Collections.EMPTY_LIST);
		addDropdownData("bankList", egovCommon.getActiveBankBranchForActiveBanks());
		addDropdownData("accNumList", Collections.EMPTY_LIST);
		addDropdownData("fundList",  masterCache.get("egi-fund"));
		loadApproverUser();
	}

	public String execute(){
		if(advanceRequisition !=null && advanceRequisition.getId()!=null){
			advanceRequisition = (EgAdvanceRequisition) persistenceService.find("from EgAdvanceRequisition where id=?",advanceRequisition.getId());
			populateFund();
		}
		return "form";
	}

	private void populateFund() {
		if(advanceRequisition !=null && advanceRequisition.getEgAdvanceReqMises()!=null)
			fund = advanceRequisition.getEgAdvanceReqMises().getFund();
	}

	@ValidationErrorPage(value="form")
	public String create(){
		try{
			if(bankaccount.getId()==null || bankaccount.getId()==-1){
				throw new ValidationException(Arrays.asList(new ValidationError("invalid.bank","invalid.bank")));
			}
			if(advanceRequisition !=null && advanceRequisition.getId()!=null){
				advanceRequisition = (EgAdvanceRequisition) persistenceService.find("from EgAdvanceRequisition where id=?",advanceRequisition.getId());
			}
			bankaccount = (Bankaccount) persistenceService.find("from Bankaccount where id=?",bankaccount.getId());
			List<HashMap<String, Object>> accountcodedetails = new ArrayList<HashMap<String, Object>>();
			List<HashMap<String, Object>> subledgerdetails = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object>  headerdetails = new HashMap<String, Object>();
			populateHeaderDetails(headerdetails);
			populateAccountCodeDetails(accountcodedetails);
			populateSubledgerDetails(subledgerdetails);
			parameters.put("paymentMode", new String[]{paymentMode});
			parameters.put("grandTotal", new String[]{advanceRequisition.getAdvanceRequisitionAmount().toPlainString()});
			paymentheader = paymentService.createPayment(parameters,headerdetails, accountcodedetails, subledgerdetails,bankaccount);
			paymentheader.start().withOwner(paymentService.getPosition()).withComments(narration);
			Integer userId = null;
			if (null != parameters.get("approverUserId") &&  Integer.valueOf(parameters.get("approverUserId")[0])!=-1 ) {
				userId = Integer.valueOf(parameters.get("approverUserId")[0]);
			}else {
				userId = EgovThreadLocals.getUserId().intValue();
			}
			if(narration!=null){
				paymentWorkflowService.transition(getValidActions().get(0).getName()+"|"+userId, paymentheader, narration);
			}else{
				paymentWorkflowService.transition(getValidActions().get(0).getName()+"|"+userId, paymentheader, paymentheader.getVoucherheader().getDescription());	
			}
			paymentService.persist(paymentheader);
			createMiscBill(paymentheader,advanceRequisition);
			advanceRequisition.getEgAdvanceReqMises().setVoucherheader(paymentheader.getVoucherheader());
			advanceRequisition.withComments(narration).end();
			addActionMessage(getText("payment.transaction.success",new String[]{paymentheader.getVoucherheader().getVoucherNumber()}));
		}catch(ValidationException e){
			LOGGER.error("ERROR"+e.getMessage(),e);
			populateData();
			throw new ValidationException(e.getErrors());
		}catch(EGOVRuntimeException e){
			LOGGER.error("ERROR"+e.getMessage(),e);
			populateData();
			List<ValidationError> errors=new ArrayList<ValidationError>();
			errors.add(new ValidationError("exception",e.getMessage()));
			throw new ValidationException(errors);
		}catch (ParseException e) {
			LOGGER.error("ERROR"+e.getMessage(),e);
			populateData();
			
		}
		return "result";
	}

	private void createMiscBill(Paymentheader paymentheader, EgAdvanceRequisition advanceRequisition) {
		Miscbilldetail miscbilldetail= new Miscbilldetail();
		miscbilldetail.setBillnumber(advanceRequisition.getAdvanceRequisitionNumber());
		miscbilldetail.setBilldate(advanceRequisition.getAdvanceRequisitionDate());
		miscbilldetail.setBillamount(advanceRequisition.getAdvanceRequisitionAmount());
		miscbilldetail.setPassedamount(advanceRequisition.getAdvanceRequisitionAmount());
		miscbilldetail.setPaidamount(advanceRequisition.getAdvanceRequisitionAmount());
		miscbilldetail.setPaidto(advanceRequisition.getEgAdvanceReqMises().getPayto());
		miscbilldetail.setPayVoucherHeader(paymentheader.getVoucherheader());
		persistenceService.setType(Miscbilldetail.class);
		persistenceService.persist(miscbilldetail);
	}

	private void populateData() {
		populateBanks();
		populateBankAccounts();
		populateFund();
	}

	private void populateBanks() {
		if(bankaccount.getId()!=null){
			addDropdownData("bankList", persistenceService.findAllBy("from Bankbranch bb where " +
					"bb.isactive=1 and bb.bank.isactive=1 order by bb.bank.name"));
		}
	}

	private void populateBankAccounts() {
		if(bankaccount.getId()!=null){
			addDropdownData("accNumList", persistenceService.findAllBy("from Bankaccount ba where ba.bankbranch.id=? and ba.fund.id=? " +
					"and ba.isactive=1 order by ba.chartofaccounts.glcode", bankaccount.getBankbranch().getId(),bankaccount.getFund().getId()));
		}
	}

	void loadApproverUser(){
		String scriptName = "paymentHeader.nextDesg";
		String type = "Payment" + "|";  
		EgovMasterDataCaching masterCache = EgovMasterDataCaching.getInstance();
		Map<String, Object>  map = new HashMap<String, Object>(); 
		if(paymentheader!=null && paymentheader.getVoucherheader().getFiscalPeriodId()!=null){
			map = voucherService.getDesgByDeptAndTypeAndVoucherDate(type, scriptName, paymentheader.getVoucherheader().getVoucherDate(), paymentheader);
		}else{
			map = voucherService.getDesgByDeptAndTypeAndVoucherDate(type, scriptName, new Date(), paymentheader);
		}
		addDropdownData("departmentList", masterCache.get("egi-department"));
		List<Map<String,Object>> desgList  =  (List<Map<String,Object>>) map.get("designationList");
		String  strDesgId = "", dName = "";
		List< Map<String , Object>> designationList = new ArrayList<Map<String,Object>>();
		Map<String, Object> desgFuncryMap;
		for(Map<String,Object> desgIdAndName : desgList) {
			desgFuncryMap = new HashMap<String, Object>();
			if(desgIdAndName.get("designationName") != null ) {
				desgFuncryMap.put("designationName",(String) desgIdAndName.get("designationName"));
			}
			if(desgIdAndName.get("designationId") != null ) {
				strDesgId = (String) desgIdAndName.get("designationId");
				if(strDesgId.indexOf("~") != -1) {
					strDesgId = strDesgId.substring(0, strDesgId.indexOf('~'));
					dName = (String) desgIdAndName.get("designationId");
					dName = dName.substring(dName.indexOf('~')+1);
				}
				desgFuncryMap.put("designationId",strDesgId);
			}
			designationList.add(desgFuncryMap);
		}
		addDropdownData("designationList", designationList); 
	}

	private void populateSubledgerDetails(List<HashMap<String, Object>> subledgerdetails) {
		HashMap<String, Object> subledgertDetailMap = null;
		for (EgAdvanceRequisitionDetails voucherDetail : advanceRequisition.getEgAdvanceReqDetailses()) {
			for (EgAdvanceReqPayeeDetails payeeDetail : voucherDetail.getEgAdvanceReqpayeeDetailses()) {
				subledgertDetailMap = new HashMap<String, Object>();
				subledgertDetailMap.put(VoucherConstant.DEBITAMOUNT, payeeDetail.getDebitAmount());
				subledgertDetailMap.put(VoucherConstant.CREDITAMOUNT, payeeDetail.getCreditAmount());
				subledgertDetailMap.put(VoucherConstant.DETAILTYPEID, payeeDetail.getAccountDetailType().getId());
				subledgertDetailMap.put(VoucherConstant.DETAILKEYID, payeeDetail.getAccountdetailKeyId());
				subledgertDetailMap.put(VoucherConstant.GLCODE, voucherDetail.getChartofaccounts().getGlcode());
				subledgerdetails.add(subledgertDetailMap);
			}
		}
	}

	private void populateHeaderDetails(HashMap<String, Object> headerdetails)throws ParseException {
		headerdetails.put(VoucherConstant.VOUCHERNAME, "Advance Payment");
		headerdetails.put(VoucherConstant.VOUCHERTYPE, "Payment");
		if(parameters.get(VoucherConstant.DESCRIPTION)!=null)
			headerdetails.put(VoucherConstant.DESCRIPTION, parameters.get(VoucherConstant.DESCRIPTION)[0]);
		if(voucherDate!=null)
			headerdetails.put(VoucherConstant.VOUCHERDATE, voucherDate);
		else
			throw new ValidationException(Arrays.asList(new ValidationError("invalid.date","invalid.date")));
		if(shouldShowHeaderField(VoucherConstant.VOUCHERNUMBER) && (parameters.get(VoucherConstant.VOUCHERNUMBER)==null || "".equals(parameters.get(VoucherConstant.VOUCHERNUMBER)[0]))){
			throw new ValidationException(Arrays.asList(new ValidationError("invalid.voucher.number","invalid.voucher.number")));
		}else{
			headerdetails.put(VoucherConstant.VOUCHERNUMBER, parameters.get(VoucherConstant.VOUCHERNUMBER)[0]);
		}
		EgAdvanceRequisitionMis egAdvanceReqMises = advanceRequisition.getEgAdvanceReqMises();
		if(egAdvanceReqMises!=null){
			if(egAdvanceReqMises.getFund()!=null && egAdvanceReqMises.getFund().getId()!=null){
				voucherHeader.setFundId((Fund) persistenceService.find("from Fund where id=?",egAdvanceReqMises.getFund().getId()));
				headerdetails.put(VoucherConstant.FUNDCODE, egAdvanceReqMises.getFund().getCode());
			}
			if(egAdvanceReqMises.getEgDepartment()!=null && egAdvanceReqMises.getEgDepartment().getId()!=null){
				voucherHeader.getVouchermis().setDepartmentid((Department) persistenceService.find("from Department where id=?",egAdvanceReqMises.getEgDepartment().getId()));
				headerdetails.put(VoucherConstant.DEPARTMENTCODE, egAdvanceReqMises.getEgDepartment().getCode());
			}
			if(egAdvanceReqMises.getFundsource()!=null && egAdvanceReqMises.getFundsource().getId()!=null){
				voucherHeader.getVouchermis().setFundsource((Fundsource) persistenceService.find("from Fundsource where id=?",egAdvanceReqMises.getFundsource().getId()));
				headerdetails.put(VoucherConstant.FUNDSOURCECODE, egAdvanceReqMises.getFundsource().getCode());
			}
			if(egAdvanceReqMises.getScheme()!=null && egAdvanceReqMises.getScheme().getId()!=null){
				voucherHeader.getVouchermis().setSchemeid((Scheme) persistenceService.find("from Scheme where id=?",egAdvanceReqMises.getScheme().getId()));
				headerdetails.put(VoucherConstant.SCHEMECODE,egAdvanceReqMises.getScheme().getCode());
			}
			if(egAdvanceReqMises.getSubScheme()!=null && egAdvanceReqMises.getSubScheme().getId()!=null){
				voucherHeader.getVouchermis().setSubschemeid((SubScheme) persistenceService.find("from SubScheme where id=?",egAdvanceReqMises.getSubScheme().getId()));
				headerdetails.put(VoucherConstant.SUBSCHEMECODE, egAdvanceReqMises.getSubScheme().getCode());
			}
			if(egAdvanceReqMises.getFunctionaryId()!=null && egAdvanceReqMises.getFunctionaryId().getId()!=null){
				voucherHeader.getVouchermis().setFunctionary((Functionary) persistenceService.find("from Functionary where id=?",egAdvanceReqMises.getFunctionaryId().getId()));
				headerdetails.put(VoucherConstant.FUNCTIONARYCODE, egAdvanceReqMises.getFunctionaryId().getCode());
			}
			if(egAdvanceReqMises.getFunction()!=null && egAdvanceReqMises.getFunction().getId()!=null){
				voucherHeader.getVouchermis().setFunction((CFunction) persistenceService.find("from CFunction where id=?",egAdvanceReqMises.getFunction().getId()));
				headerdetails.put(VoucherConstant.FUNCTIONCODE, egAdvanceReqMises.getFunction().getCode());
			}
			if(egAdvanceReqMises.getSubFieldId()!=null && egAdvanceReqMises.getSubFieldId().getId()!=null){
				voucherHeader.getVouchermis().setDivisionid((Boundary) persistenceService.find("from Boundary where id=?",egAdvanceReqMises.getSubFieldId().getId()));
				headerdetails.put(VoucherConstant.DIVISIONID, egAdvanceReqMises.getSubFieldId().getId());
			}
		}

	}

	private void populateAccountCodeDetails(List<HashMap<String, Object>> accountcodedetails) {
		HashMap<String,Object> accdetailsMap = new HashMap<String, Object>();
		accdetailsMap.put(VoucherConstant.GLCODE,bankaccount.getChartofaccounts().getGlcode());
		accdetailsMap.put(VoucherConstant.NARRATION,bankaccount.getChartofaccounts().getName());
		accdetailsMap.put(VoucherConstant.DEBITAMOUNT,0);
		accdetailsMap.put(VoucherConstant.CREDITAMOUNT,advanceRequisition.getAdvanceRequisitionAmount());
		accountcodedetails.add(accdetailsMap);
		HashMap<String,Object> accdetailsMap1 = new HashMap<String, Object>();
		for (EgAdvanceRequisitionDetails row : advanceRequisition.getEgAdvanceReqDetailses()) {
			accdetailsMap1.put(VoucherConstant.GLCODE,row.getChartofaccounts().getGlcode());
			accdetailsMap1.put(VoucherConstant.NARRATION,row.getChartofaccounts().getName());
		}
		accdetailsMap1.put(VoucherConstant.DEBITAMOUNT,advanceRequisition.getAdvanceRequisitionAmount());
		accdetailsMap1.put(VoucherConstant.CREDITAMOUNT,0);
		accountcodedetails.add(accdetailsMap1);
	}
	
	@Override
	public Object getModel() {
		return voucherHeader;
	}

	public void setAdvanceRequisition(EgAdvanceRequisition advanceRequisition) {
		this.advanceRequisition = advanceRequisition;
	}

	public EgAdvanceRequisition getAdvanceRequisition() {
		return advanceRequisition;
	}
	
	@Override
	public boolean shouldShowHeaderField(String field) {
		return super.shouldShowHeaderField(field);
	}
	
	public void setBankaccount(Bankaccount bankaccount) {
		this.bankaccount = bankaccount;
	}
	public Bankaccount getBankaccount() {
		return bankaccount;
	}

	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void setPaymentWorkflowService(SimpleWorkflowService<Paymentheader> paymentWorkflowService) {
		this.paymentWorkflowService = paymentWorkflowService;
	}

	public void setVoucherDate(Date voucherdate) {
		this.voucherDate = voucherdate;
	}

	public Date getVoucherDate() {
		return voucherDate;
	}
	
	public void setModeOfCollectionMap(Map<String,String> modeOfCollectionMap) {
		this.modeOfCollectionMap = modeOfCollectionMap;
	}

	public Map<String,String> getModeOfCollectionMap() {
		return modeOfCollectionMap;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getNarration() {
		return narration;
	}

	public void setVoucherService(VoucherService voucherService) {
		this.voucherService = voucherService;
	}

	public VoucherService getVoucherService() {
		return voucherService;
	}

	public void setEgovCommon(EgovCommon egovCommon) {
		this.egovCommon = egovCommon;
	}

	public EgovCommon getEgovCommon() {
		return egovCommon;
	}
	
	public List<Action> getValidActions(){
		return paymentWorkflowService.getValidActions(paymentheader);
 	}
	
	public String formatDate(Date date){
		return Constants.DDMMYYYYFORMAT2.format(date);
	}

	public void setFund(Fund fund) {
		this.fund = fund;
	}

	public Fund getFund() {
		return fund;
	}


}
