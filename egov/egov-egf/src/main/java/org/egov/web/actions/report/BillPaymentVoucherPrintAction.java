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


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.Bankaccount;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.VoucherDetail;
import org.egov.commons.utils.EntityType;
import org.egov.egf.commons.EgovCommon;
import org.egov.exceptions.EGOVException;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infstr.utils.DateUtils;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.infstr.utils.NumberToWord;
import org.egov.model.bills.Miscbilldetail;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.model.instrument.InstrumentVoucher;
import org.egov.model.payment.Paymentheader;
import org.egov.pims.commons.Position;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.utils.ReportHelper;
import org.egov.web.actions.voucher.VoucherReport;
import org.hibernate.FlushMode;
import org.hibernate.SQLQuery;
import org.springframework.transaction.annotation.Transactional;

@Results(value={ 
	@Result(name="PDF",type="stream",location="inputStream", params={"inputName","inputStream","contentType","application/pdf","contentDisposition","no-cache;filename=BankPaymentVoucherReport.pdf"}),
	@Result(name="XLS",type="stream",location="inputStream", params={"inputName","inputStream","contentType","application/xls","contentDisposition","no-cache;filename=BankPaymentVoucherReport.xls"}),
	@Result(name="HTML",type="stream",location="inputStream", params={"inputName","inputStream","contentType","text/html"})
})
@Transactional(readOnly=true)
@ParentPackage("egov")
public class BillPaymentVoucherPrintAction extends BaseFormAction{
	Long chequeNumberPass;
	String chequeNumber="";
	InstrumentHeader instrumentHeader = null;
	String cashModePartyName = "";				//Also used as a flag to check if the mode of payment is Cash
	String chequeDate = "";
	String rtgsRefNo="";
	String rtgsDate = "";
	String paymentMode = "";
	String chequeNoComp="";
	Long chequeNoCompL;
	String jasperpath = "/org/egov/web/actions/report/billPaymentVoucherReport.jasper";
	static final long serialVersionUID = 1L;
	static final String PRINT = "print";
	CVoucherHeader voucher = new CVoucherHeader();
	Paymentheader paymentHeader = new Paymentheader();
	List<Object> voucherReportList = new ArrayList<Object>();
	InputStream inputStream;
	ReportHelper reportHelper;
	Long id;
	List<Miscbilldetail> miscBillDetailList;
	Miscbilldetail ob=new Miscbilldetail();
	static final String ACCDETAILTYPEQUERY=" from Accountdetailtype where id=?";
	String bankName="";
	String bankAccountNumber="";
	ArrayList<Long> chequeNoList=new ArrayList<Long>();
	ArrayList<String> chequeNosList=new ArrayList<String>();

	
	public Map<String, Object> getParamMap() {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("bpvNumber", getVoucherNumber());
		paramMap.put("voucherDate", getVoucherDate());
		paramMap.put("bankName", bankName);
		paramMap.put("bankAccountNumber", bankAccountNumber);
		
		if(paymentHeader!=null && paymentHeader.getState()!=null){
			loadInboxHistoryData(paymentHeader.getState(),paramMap);
		}
		
		if(miscBillDetailList != null){
			paramMap.put("partyName", getPartyName());
			paramMap.put("billNumber", getBillNumber());
			paramMap.put("linkNo", getLinkNo());
		}
		
	    paramMap.put("amountInWords", getAmountInWords());
		paramMap.put("chequeNumber", chequeNumber);
		paramMap.put("chequeDate", chequeDate);
		paramMap.put("rtgsRefNo", rtgsRefNo);
		paramMap.put("paymentMode", paymentMode);
		paramMap.put("rtgsDate", rtgsDate);
		paramMap.put("ulbName", getUlbName());
		paramMap.put("narration", getPaymentNarration());
		
		return paramMap;
	}

	private String getLinkNo() {
		if(miscBillDetailList!=null && miscBillDetailList.size()>1)
			return "MULTIPLE";
		else if(miscBillDetailList!=null && miscBillDetailList.size()==1 && miscBillDetailList.get(0).getBillVoucherHeader()!=null)
			return miscBillDetailList.get(0).getBillVoucherHeader().getVoucherNumber();
		else
			return "";
	}
	
	private String getAmountInWords() {
			if(miscBillDetailList!=null && miscBillDetailList.size()>1)
			{
				Float totalAmt=0f;
				for(Miscbilldetail misBillDet:miscBillDetailList)
				{
					totalAmt+=misBillDet.getPaidamount().floatValue();
				}
				return NumberToWord.convertToWord(new BigDecimal(totalAmt).toString());
			}
			else if(miscBillDetailList!=null && miscBillDetailList.size()==1)
				return miscBillDetailList.get(0).getAmtInWords();
			else
				return "";
	}


	private String getBillNumber() {
		if(miscBillDetailList!=null && miscBillDetailList.size()>1)
			return "MULTIPLE";
		else if(miscBillDetailList!=null && miscBillDetailList.size()==1)
			return miscBillDetailList.get(0).getBillnumber();
		else
			return "";
	}
	
	private String getPartyName() {
		
		if(cashModePartyName != null && !cashModePartyName.equalsIgnoreCase(""))
			return cashModePartyName;
		if(miscBillDetailList!=null && miscBillDetailList.size()>1 && hasSamePartyName(miscBillDetailList))
			return miscBillDetailList.get(0).getPaidto();
		else if(miscBillDetailList!=null && miscBillDetailList.size()>1)
			return "MULTIPLE";
		else if(miscBillDetailList!=null && miscBillDetailList.size()==1)
			return miscBillDetailList.get(0).getPaidto();
		else
			return "";
	}
	
	boolean hasSamePartyName(List<Miscbilldetail> billList) {
		String name = "initial";
		for (Miscbilldetail miscbilldetail : billList) {
			if("initial".equalsIgnoreCase(name)){
				name = miscbilldetail.getPaidto();
			}else if(!name.equalsIgnoreCase(miscbilldetail.getPaidto()))
				return false;
		}
		return true;
	}

	public Long getId() {
		return id;
	}
	
	public void setReportHelper(ReportHelper helper) {
		this.reportHelper = helper;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public List<Object> getVoucherReportList() {
		return voucherReportList;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
	
	public String execute(){
		return print();
	}
	
@Action(value="/report/billPaymentVoucherPrint-ajaxPrint")
	public String ajaxPrint(){
		return exportHtml();
	}
	
	@Override
	public Object getModel() {
		return voucher;
	}
	
@Action(value="/report/billPaymentVoucherPrint-print")
	public String print() {
		return PRINT;
	}

	void populateVoucher() {
	HibernateUtil.getCurrentSession().setDefaultReadOnly(true);
	HibernateUtil.getCurrentSession().setFlushMode(FlushMode.MANUAL);
		
		if(!StringUtils.isBlank(parameters.get("id")[0])){
			chequeNosList=new ArrayList<String>();
			Long id = Long.valueOf(parameters.get("id")[0]);
			paymentHeader = (Paymentheader) HibernateUtil.getCurrentSession().get(Paymentheader.class,id);
			if(paymentHeader != null && paymentHeader.getType().equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS))
			{
				paymentMode="rtgs";
				voucher=paymentHeader.getVoucherheader();
				if(voucher!=null)
				{
					List<InstrumentVoucher> instrumentVoucherList = persistenceService.findAllBy("from InstrumentVoucher where voucherHeaderId.id=?", voucher.getId());
					if(instrumentVoucherList!=null && instrumentVoucherList.size()!=0)
					{
						InstrumentHeader instrumentHeader= instrumentVoucherList.get(0).getInstrumentHeaderId();
						rtgsRefNo=instrumentHeader.getTransactionNumber();
						rtgsDate=Constants.DDMMYYYYFORMAT2.format(instrumentHeader.getTransactionDate());
					}
					generateVoucherReportList();
					Bankaccount bankAccount = paymentHeader.getBankaccount();
					if(bankAccount!=null)
					{
						bankName = bankAccount.getBankbranch().getBank().getName().concat("-").concat(bankAccount.getBankbranch().getBranchname());
						bankAccountNumber = bankAccount.getAccountnumber();
					}
					miscBillDetailList = persistenceService.findAllBy("from Miscbilldetail where payVoucherHeader.id=?", voucher.getId());
				}
				return;
			}
			if(paymentHeader != null){
				voucher = paymentHeader.getVoucherheader();
				List<String> excludeChequeStatusses=new ArrayList<String>();
				excludeChequeStatusses.add(FinancialConstants.INSTRUMENT_CANCELLED_STATUS);
				excludeChequeStatusses.add(FinancialConstants.INSTRUMENT_SURRENDERED_FOR_REASSIGN_STATUS);
				excludeChequeStatusses.add(FinancialConstants.INSTRUMENT_SURRENDERED_STATUS);
				List<InstrumentVoucher> instrumentVoucherList = persistenceService.findAllBy("from InstrumentVoucher where voucherHeaderId.id=?", voucher.getId());
				if(instrumentVoucherList!=null && !instrumentVoucherList.isEmpty()){
						for (InstrumentVoucher instrumentVoucher : instrumentVoucherList) {
							
							try{
								if(excludeChequeStatusses.contains(instrumentVoucher.getInstrumentHeaderId().getStatusId().getDescription()))
										continue;
								 instrumentHeader = instrumentVoucher.getInstrumentHeaderId();	
								 chequeNumber= instrumentVoucher.getInstrumentHeaderId().getInstrumentNumber();
								 chequeDate=Constants.DDMMYYYYFORMAT2.format(instrumentVoucher.getInstrumentHeaderId().getInstrumentDate());
								 if(isInstrumentMultiVoucherMapped(instrumentVoucher.getInstrumentHeaderId().getId()))
									 chequeNosList.add(chequeNumber+"-MULTIPLE");
								 else
									 chequeNosList.add(chequeNumber);
								 chequeNumberPass=Long.parseLong(chequeNumber);
								 chequeNoList.add(chequeNumberPass);
							 }
							 catch(NumberFormatException ex){System.out.println("Exception"+ex);}
					    }
						//if(!excludeChequeStatusses.contains(instrumentVoucher.getInstrumentHeaderId().getStatusId().getDescription()))
							//chequeDate = Constants.DDMMYYYYFORMAT2.format(instrumentVoucherList.get(0).getInstrumentHeaderId().getInstrumentDate());
				}
				generateVoucherReportList();
				Bankaccount bankAccount = paymentHeader.getBankaccount();
				if(bankAccount!=null){
					bankName = bankAccount.getBankbranch().getBank().getName().concat("-").concat(bankAccount.getBankbranch().getBranchname());
					bankAccountNumber = bankAccount.getAccountnumber();
				}
				//For Cash mode of payment, we need to take the payto of the associated cheque.
				if(paymentHeader.getType().equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_CASH))
				{
					if(instrumentHeader != null && instrumentHeader.getPayTo() != null )
					{
						cashModePartyName = instrumentHeader.getPayTo();
					}
				}
			}
			miscBillDetailList = persistenceService.findAllBy("from Miscbilldetail where payVoucherHeader.id=?", voucher.getId());
		}
		Collections.sort(chequeNoList);
		chequeNumber="";
		for(Long lval:chequeNoList) {
			 for(String sval:chequeNosList){
				 if(sval.contains("MULTIPLE"))
					 chequeNoCompL=Long.parseLong(sval.substring(0, sval.lastIndexOf("-")));
				 else
					 chequeNoCompL=Long.parseLong(sval);
				 if(lval.equals(chequeNoCompL)){
					 chequeNumber = chequeNumber+ sval + "/";
					 break;
				 }
				
			 }
		}
		if(chequeNumber.length()>1){
			chequeNumber=chequeNumber.substring(0, chequeNumber.length()-1);
		}
    }	
	private boolean isInstrumentMultiVoucherMapped(Long instrumentHeaderId) {
		List<InstrumentVoucher> instrumentVoucherList = persistenceService.findAllBy(
						"from InstrumentVoucher where instrumentHeaderId.id=?",instrumentHeaderId);
		boolean rep = false;
		if (instrumentVoucherList != null && instrumentVoucherList.size() != 0) {
			Long voucherId = instrumentVoucherList.get(0).getVoucherHeaderId().getId();
			for (InstrumentVoucher instrumentVoucher : instrumentVoucherList) {
				if (voucherId != instrumentVoucher.getVoucherHeaderId().getId()) 
				{
					rep = true;
					break;
				}
			}
		}
		return rep;
	}

	private void generateVoucherReportList() {
		if(voucher != null){
			for (VoucherDetail vd : voucher.getVoucherDetail()) {
				if(BigDecimal.ZERO.equals(vd.getCreditAmount())){
					VoucherReport voucherReport = new VoucherReport(persistenceService,Integer.valueOf(voucher.getId().toString()),vd);
					voucherReport.setDepartment(voucher.getVouchermis().getDepartmentid());
					voucherReportList.add(voucherReport);
				}
			}
			for (VoucherDetail vd : voucher.getVoucherDetail()) {
				if(BigDecimal.ZERO.equals(vd.getDebitAmount())){
					VoucherReport voucherReport = new VoucherReport(persistenceService,Integer.valueOf(voucher.getId().toString()),vd);
					voucherReport.setDepartment(voucher.getVouchermis().getDepartmentid());
					voucherReportList.add(voucherReport);
				}
			}
		}
	}
	
	String getUlbName(){
		SQLQuery query = HibernateUtil.getCurrentSession().createSQLQuery("select name from companydetail");
		List<String> result = query.list();
		if(result!=null)
			return result.get(0);
		return "";
	}

	private String getPaymentNarration(){
		return voucher == null || voucher.getDescription() == null ?"" : voucher.getDescription();
	}
	
	public String exportPdf() throws JRException, IOException{
		populateVoucher();
		inputStream = reportHelper.exportPdf(inputStream, jasperpath, getParamMap(), voucherReportList);
	    return "PDF";
	}
	
	public String exportHtml() {
		populateVoucher();
	   inputStream = reportHelper.exportHtml(inputStream, jasperpath, getParamMap(), voucherReportList,"px");
	   return "HTML";
	}
	
	public String exportXls() throws JRException, IOException{
		populateVoucher();
		inputStream = reportHelper.exportXls(inputStream, jasperpath, getParamMap(), voucherReportList);
	    return "XLS";
	}


	public Map<String,Object> getAccountDetails(final Integer detailtypeid,final Integer detailkeyid,Map<String,Object> tempMap) throws EGOVException{
		Accountdetailtype detailtype = (Accountdetailtype) getPersistenceService().find(ACCDETAILTYPEQUERY,detailtypeid);
		tempMap.put("detailtype", detailtype.getName());
		tempMap.put("detailtypeid", detailtype.getId());
		tempMap.put("detailkeyid", detailkeyid);
		
		EgovCommon common = new EgovCommon();
		common.setPersistenceService(persistenceService);
		EntityType entityType = common.getEntityType(detailtype,detailkeyid);
		tempMap.put(Constants.DETAILKEY,entityType.getName());
		tempMap.put(Constants.DETAILCODE,entityType.getCode());		
		return tempMap;
	}

	String getVoucherNumber() {
		return voucher == null || voucher.getVoucherNumber() == null?"" : voucher.getVoucherNumber();
	}
	
	String getVoucherDate() {
		return voucher == null || voucher.getVoucherDate() == null ?"" : DateUtils.getDefaultFormattedDate(voucher.getVoucherDate());
	}
	
	void loadInboxHistoryData(State states, Map<String, Object> paramMap) throws EGOVRuntimeException {
		List<String> history = new ArrayList<String>();
		List<String> workFlowDate = new ArrayList<String>();
    	if (states != null) {
    	    List<StateHistory> stateHistory = states.getHistory();
    	  
    	    for (StateHistory state : stateHistory) {
	    		
	    		if(!"NEW".equalsIgnoreCase(state.getValue())){
	    			history.add(state.getSenderName());
	    			workFlowDate.add(Constants.DDMMYYYYFORMAT2.format(state.getLastModifiedDate()));
	    		}
    	    }
        }
    	for (int i = 0; i<history.size();i++) {
    		paramMap.put("workFlow_"+i, history.get(i));
    		paramMap.put("workFlowDate_"+i, workFlowDate.get(i));
		}
    }
	
	

	String getVoucherDescription() {
		return voucher == null || voucher.getDescription() == null?"" : voucher.getDescription();
	}

}

