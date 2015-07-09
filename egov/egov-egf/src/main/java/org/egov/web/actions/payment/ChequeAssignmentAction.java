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

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.commons.Bankaccount;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.EgwStatus;
import org.egov.commons.Fund;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.commons.utils.EntityType;
import org.egov.egf.commons.EgovCommon;
import org.egov.eis.entity.DrawingOfficer;
import org.egov.exceptions.EGOVException;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.script.entity.Script;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.config.dao.AppConfigValuesDAO;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.infstr.utils.SequenceGenerator;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.model.instrument.InstrumentVoucher;
import org.egov.model.payment.ChequeAssignment;
import org.egov.model.recoveries.Recovery;
import org.egov.payment.client.BankAdviceForm;
import org.egov.services.cheque.ChequeService;
import org.egov.services.contra.ContraService;
import org.egov.services.instrument.InstrumentService;
import org.egov.services.payment.PaymentService;
import org.egov.services.recoveries.RecoveryService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.utils.ReportHelper;
import org.egov.utils.VoucherHelper;
import org.egov.web.actions.voucher.BaseVoucherAction;
import org.egov.web.actions.voucher.CommonAction;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.validator.annotations.Validation;
@Results(value={
		@Result(name="bankAdvice-PDF",type="stream",location=Constants.INPUT_STREAM, params={Constants.INPUT_NAME,Constants.INPUT_STREAM,Constants.CONTENT_TYPE,"application/pdf",Constants.CONTENT_DISPOSITION,"no-cache;filename=BandAdvice.pdf"}),
		@Result(name="bankAdvice-XLS",type="stream",location=Constants.INPUT_STREAM, params={Constants.INPUT_NAME,Constants.INPUT_STREAM,Constants.CONTENT_TYPE,"application/xls",Constants.CONTENT_DISPOSITION,"no-cache;filename=BandAdvice.xls"}),
		@Result(name="bankAdvice-HTML",type="stream",location=Constants.INPUT_STREAM, params={Constants.INPUT_NAME,Constants.INPUT_STREAM,Constants.CONTENT_TYPE,"text/html"})
	})

@ParentPackage("egov")
@Validation
@Transactional(readOnly=true)
public class ChequeAssignmentAction extends BaseVoucherAction 
{
	private static final String	SURRENDERSEARCH	= "surrendersearch";
	private static final String	SURRENDERRTGSSEARCH	= "surrenderRTGSsearch";
	private String paymentMode,inFavourOf;
	private PaymentService paymentService;
	private Integer bankaccount,selectedRows=0,bankbranch; 
	private String bank_branch;
	private String bank_account;	
	private Integer department;
	public Map<String, String>						modeOfPaymentMap;
	private Date chequeDt;
	private boolean chequeNoGenerationAuto;
	private boolean rtgsNoGenerationAuto;
	private SequenceGenerator sequenceGenerator;
	private InstrumentService instrumentService;
	private List<ChequeAssignment> chequeAssignmentList;
	private List<InstrumentHeader> instHeaderList=null;
	List<InstrumentVoucher> instVoucherList;
	List<InstrumentHeader> instVoucherDisplayList;
	private static final Logger LOGGER = Logger.getLogger(ChequeAssignmentAction.class);
	@Autowired
        private AppConfigValuesDAO appConfigValuesDAO;  
	private static final String JASPER_PATH="/org/egov/payment/client/bankAdviceReport.jasper";
	InputStream inputStream;
	ReportHelper reportHelper;
	Map<String,Object> paramMap = new HashMap<String,Object>();
	List<Object> adviceList = new ArrayList<Object>();
	String fromDate;
	String toDate;
	private String rtgsDate;
	private String rtgsRefNo;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",Constants.LOCALE);
	private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Constants.LOCALE);
	
	private List<InstrumentVoucher>	instrumentVoucherList;
	private List <InstrumentHeader>  instrumentHeaderList;
	private CommonAction common;
	String[] surrender;
	String instrumentNumber;
	private ContraService	contraService;
	Long recoveryId;
	private String button;
	String[] newInstrumentNumber;
	String[] newSerialNo;
	String[] newInstrumentDate;
	String[] surrendarReasons;
	private EgovCommon egovCommon;
	private ChequeService chequeService;
	String bank_account_dept;
	private String typeOfAccount;
	private Date currentDate;
	Map<String, String> bankBranchMap=Collections.EMPTY_MAP;
	Map<String, String> billTypeMap=Collections.EMPTY_MAP;
	String billType;
	Map<String, String> bankAccountMap=Collections.EMPTY_MAP;
	private RecoveryService recoveryService;
	Map<String, String> surrendarReasonMap=Collections.EMPTY_MAP;
	private boolean reassignSurrenderChq=false;
	//to overriding department Mandatory Condition only for remittance cheque assignment search
	private Boolean 	deptNonMandatory=false;  
	private Boolean 	rtgsContractorAssignment=false;  
	private String assignmentType="BillPayment";//SalaryPayment or RemittancePayment 
	private List<String> chequeSlNoList=new ArrayList<String>();
	private Map chequeSlNoMap;
	Map<Bankaccount,List<ChequeAssignment>> accountNoAndRtgsEntryMap = new HashMap<Bankaccount,List<ChequeAssignment>>();
	Map<BankAccountRemittanceCOA,List<ChequeAssignment>> accountNoAndRemittanceRtgsEntryMap = new HashMap<BankAccountRemittanceCOA,List<ChequeAssignment>>();
	Map<String,String> rtgsdateMap = new HashMap<String,String>();
	Map<String,String> rtgsRefNoMap = new HashMap<String,String>();
	Map<String,Boolean> rtgsSeceltedAccMap = new HashMap<String,Boolean>();       
	List<ChequeAssignment> rtgsList=new LinkedList<ChequeAssignment>();
	List<ChequeAssignment> viewReceiptDetailsList = new ArrayList<ChequeAssignment>();
	public static final String RTGSAUTOGENERATIONNUMBERFORMAT = "000000";
	private NumberFormat ackNumberFormat = new DecimalFormat(RTGSAUTOGENERATIONNUMBERFORMAT);
	private Integer drawingOfficerId;
	private String drawingOfficerCode;
	private String billSubType;
	private String region;
	private String recoveryCode;
	private String paymentId;
	private BigDecimal totalDeductedAmount;
	private Boolean nonSubledger=false;
	private FinancialYearDAO financialYearDAO;
	
	public List<String> getChequeSlNoList() {
		return chequeSlNoList;       
	}

	public void setChequeSlNoList(List<String> chequeSlNoList) {
		this.chequeSlNoList = chequeSlNoList;
	}

	public String getRtgsRefNo() {
		return rtgsRefNo;
	}

	public void setRtgsRefNo(String rtgsRefNo) {
		this.rtgsRefNo = rtgsRefNo;
	}
	
	public String getRtgsDate() {
		return rtgsDate;
	}

	public void setRtgsDate(String rtgsDate) {
		this.rtgsDate = rtgsDate;
	}
	public String getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}

	public boolean getReassignSurrenderChq() {
		return reassignSurrenderChq;
	}

	public void setReassignSurrenderChq(boolean reassignSurrenderChq) {
		this.reassignSurrenderChq = reassignSurrenderChq;
	}

	public Map<String, String> getSurrendarReasonMap() {
		return surrendarReasonMap;
	}

	public void setSurrendarReasonMap(Map<String, String> surrendarReasonMap) {
		this.surrendarReasonMap = surrendarReasonMap;
	}

	public void prepare()
	{
		super.prepare();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepare...");
		addDropdownData("bankaccountList", Collections.EMPTY_LIST);
		addDropdownData("regionsList", VoucherHelper.TNEB_REGIONS);
		chequeNoGenerationAuto = paymentService.isChequeNoGenerationAuto();
		rtgsNoGenerationAuto = paymentService.isRtgsNoGenerationAuto();     
		typeOfAccount = FinancialConstants.TYPEOFACCOUNT_PAYMENTS+","+FinancialConstants.TYPEOFACCOUNT_RECEIPTS_PAYMENTS;
		currentDate=new Date();
		//overriding department Mandatory Condition only for remittance cheque assignment search
		if(deptNonMandatory==true)
		{
			mandatoryFields.remove("department");
		}
			
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed prepare.");
	}
	
	@SkipValidation
	@Action(value="/payment/chequeAssignment-beforeSearch")
	public String beforeSearch()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting beforeSearch...");
		paymentMode=FinancialConstants.MODEOFPAYMENT_CHEQUE;
		loadBillTypeMap();
		//typeOfAccount = FinancialConstants.TYPEOFACCOUNT_PAYMENTS+","+FinancialConstants.TYPEOFACCOUNT_RECEIPTS_PAYMENTS;
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed beforeSearch.");
		return "search";
	}
	@SkipValidation
	@Action(value="/payment/chequeAssignment-beforeRtgsSearch")
	public String beforeRtgsSearch()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting beforeRtgsSearch...");     
		paymentMode=FinancialConstants.MODEOFPAYMENT_RTGS; 
		rtgsContractorAssignment=true;
		//loadBillTypeMap();
		//typeOfAccount = FinancialConstants.TYPEOFACCOUNT_PAYMENTS+","+FinancialConstants.TYPEOFACCOUNT_RECEIPTS_PAYMENTS;
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed beforeRtgsSearch.");
		return "rtgsSearch";
	}  
	@SkipValidation
	@Action(value="/payment/chequeAssignment-beforeTNEBRtgsSearch")
	public String beforeTNEBRtgsSearch()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting beforeRtgsSearch...");     
		paymentMode=FinancialConstants.MODEOFPAYMENT_RTGS; 
		rtgsContractorAssignment=true;
		setTNEBMandatoryFields();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed beforeRtgsSearch.");
		return "tnebRtgsSearch";
	}  
private void setTNEBMandatoryFields(){
		billType = "Expense";
		List<String> propartyAppConfigKeysList = new ArrayList<String>();
		Map<String, String> propartyAppConfigResultList = new LinkedHashMap<String, String> ();
		propartyAppConfigKeysList.add(FinancialConstants.EB_VOUCHER_PROPERTY_FUND);
		propartyAppConfigKeysList.add(FinancialConstants.EB_VOUCHER_PROPERTY_FUNCTION);
		propartyAppConfigKeysList.add(FinancialConstants.EB_VOUCHER_PROPERTY_DEPARTMENT);
		propartyAppConfigKeysList.add(FinancialConstants.EB_VOUCHER_PROPERTY_BANKBRANCH);
		propartyAppConfigKeysList.add(FinancialConstants.EB_VOUCHER_PROPERTY_BANKACCOUNT);
		
		//Get App config value
		for(String key:propartyAppConfigKeysList){
			String value = null;
		try{
			List<AppConfigValues> configValues =appConfigValuesDAO.
					getConfigValuesByModuleAndKey(FinancialConstants.MODULE_NAME_APPCONFIG,key); 
			
			for (AppConfigValues appConfigVal : configValues) {
				value = appConfigVal.getValue();
				propartyAppConfigResultList.put(key, value);
					 }
			} catch (Exception e) {
				 throw new EGOVRuntimeException("Appconfig value for EB Voucher propartys is not defined in the system");
			}
		}
		for(String key:propartyAppConfigResultList.keySet()){
			
			if(key.equals("EB Voucher Property-Fund")){
				voucherHeader.setFundId((Fund) persistenceService.find("from Fund where code = ?",propartyAppConfigResultList.get(key)));
			}
			if(key.equals("EB Voucher Property-Function")){
				voucherHeader.getVouchermis().setFunction((CFunction) persistenceService.find("from CFunction where code = ?",propartyAppConfigResultList.get(key)));
			}
			if(key.equals("EB Voucher Property-Department")){
				voucherHeader.getVouchermis().setDepartmentid((Department) persistenceService.find("from Department where deptCode = ?",propartyAppConfigResultList.get(key)));
			}
			if(key.equals("EB Voucher Property-BankBranch")){
				bank_branch = propartyAppConfigResultList.get(key);
			}
			if(key.equals("EB Voucher Property-BankAccount")){
				bank_account = propartyAppConfigResultList.get(key);
				Bankaccount ba = (Bankaccount) persistenceService.find(" from Bankaccount where accountnumber=?",bank_account);
				if(ba.getId()!=null){
				bankaccount = ba.getId();
				}
			}
		}
		
	}
	
	public void prepareBeforeRemittanceRtgsSearch()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepareBeforeRemittanceRtgsSearch...");
		addDropdownData("drawingofficerList",getPersistenceService().findAllBy("from DrawingOfficer where id in" +
				" (select drawingOfficer.id from DepartmentDOMapping) order by code"));
		final List<Recovery> listRecovery = recoveryService.getAllActiveAutoRemitTds();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("RemitRecoveryAction | Tds list size : "+ listRecovery.size());
		addDropdownData("recoveryList" , listRecovery);
		paymentMode=FinancialConstants.MODEOFPAYMENT_RTGS;
		rtgsContractorAssignment=true;
		if(deptNonMandatory==true)
		{
			mandatoryFields.remove("department");
		}    
		if(mandatoryFields.contains("fund")){
			mandatoryFields.remove("fund");    
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed prepareBeforeRemittanceRtgsSearch.");
	}  
	
	@SkipValidation
	@Action(value="/payment/chequeAssignment-beforeRemittanceRtgsSearch")
	public String beforeRemittanceRtgsSearch()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting beforeRemittanceRtgsSearch...");     
		paymentMode=FinancialConstants.MODEOFPAYMENT_RTGS; 
		rtgsContractorAssignment=true;
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed beforeRemittanceRtgsSearch.");
		return "remittanceRtgsSearch";
	}  
	
	@ValidationErrorPage(value="remittanceRtgsSearch")
	@SkipValidation
	public String searchRemittanceRTGS() throws EGOVException,ParseException            
	{    
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting searchRemittanceRTGS...");
		List<ChequeAssignment> rtgsChequeAssignmentList = null;
		List<ChequeAssignment> rtgsEntry =new ArrayList<ChequeAssignment>(); 
		rtgsContractorAssignment=true;            
		boolean addList=false;
		//TODO Initialize to null not new ()
		Bankaccount bnkAcc= new Bankaccount();
				//TODO Initialize to null not new ());
		BankAccountRemittanceCOA selBnkAccCOA= new BankAccountRemittanceCOA();
		//TODO Initialize to null not new ()
		CChartOfAccounts coa = new CChartOfAccounts();
		
			rtgsChequeAssignmentList = paymentService.getPaymentVoucherForRemittanceRTGSInstrument(parameters,voucherHeader);
			
		for(ChequeAssignment chqAssgn:rtgsChequeAssignmentList){
			BankAccountRemittanceCOA bnkAccCOA = new BankAccountRemittanceCOA();
				if(accountNoAndRemittanceRtgsEntryMap.isEmpty()){
							rtgsEntry=new ArrayList<ChequeAssignment>();
							bnkAcc = (Bankaccount) persistenceService.find("from Bankaccount where id=?", Integer.parseInt(chqAssgn.getBankAccountId().toString()));
							coa = (CChartOfAccounts) persistenceService.find("from CChartOfAccounts where id =?",  new Long(chqAssgn.getGlcodeId().toString()));
							bnkAccCOA.setBankAccount(bnkAcc);
							bnkAccCOA.setRemittanceCOA(coa);
							selBnkAccCOA=bnkAccCOA;              
							rtgsEntry.add(chqAssgn); 
							accountNoAndRemittanceRtgsEntryMap.put(bnkAccCOA,rtgsEntry);   
							rtgsdateMap.put(bnkAcc.getId().toString(),formatter.format(currentDate));
				}else{
							Set<BankAccountRemittanceCOA> bankAccntKeySet=accountNoAndRemittanceRtgsEntryMap.keySet();  
							java.util.Iterator keySetitr =bankAccntKeySet.iterator();
							  while (keySetitr.hasNext()) {   
								  BankAccountRemittanceCOA bkcoa = (BankAccountRemittanceCOA) keySetitr.next();
								 addList=false;
								 if(bkcoa.getBankAccount().getId().equals(Integer.parseInt(chqAssgn.getBankAccountId().toString())) &&
										 bkcoa.getRemittanceCOA().getId().toString().equals(chqAssgn.getGlcodeId().toString())){
									 bnkAccCOA=bkcoa;addList=false;
										break;
							     }else{   
							    	 	addList=true;
							     }
							 }              
							if(!addList){          
								accountNoAndRemittanceRtgsEntryMap.get(bnkAccCOA).add(chqAssgn);
									selBnkAccCOA=null;
								}           
							else{    
									rtgsEntry=new ArrayList<ChequeAssignment>();
									bnkAcc = (Bankaccount) persistenceService.find("from Bankaccount where id=?", Integer.parseInt(chqAssgn.getBankAccountId().toString()));
									coa = (CChartOfAccounts) persistenceService.find("from CChartOfAccounts where id =?", new Long(chqAssgn.getGlcodeId().toString()));
									bnkAccCOA.setBankAccount(bnkAcc); 
									bnkAccCOA.setRemittanceCOA(coa);
									selBnkAccCOA=bnkAccCOA;                                                  
									rtgsEntry.add(chqAssgn);
									accountNoAndRemittanceRtgsEntryMap.put(bnkAccCOA,rtgsEntry);
									rtgsdateMap.put(bnkAcc.getId().toString(),formatter.format(currentDate));
							}   
				}                                     
		}          
	getSession().put("accountNoAndRtgsEntryMapSession", accountNoAndRemittanceRtgsEntryMap);
		if( 0 != drawingOfficerId)
		{
			DrawingOfficer drawingOfficer = (DrawingOfficer) persistenceService.find("from DrawingOfficer where id =?", drawingOfficerId  );
			drawingOfficerCode = drawingOfficer.getCode();
		}     
		assignmentType="BillPayment"; 
		if(!"".equals(parameters.get("recoveryId")[0]))
		{    
			Recovery recovery=(Recovery)persistenceService.find("from Recovery where id=?",new Long(parameters.get("recoveryId")[0]));
			 if(recovery.getChartofaccounts().getChartOfAccountDetails().isEmpty())
			 {
				 nonSubledger = true;
			 }
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed searchRemittanceRTGS.");         
		return "searchRemittanceRtgsResult";      
	}
	@SkipValidation
	@Action(value="/payment/chequeAssignment-getReceiptDetails")
	public String getReceiptDetails(){
		Query query = null;
		query = HibernateUtil.getCurrentSession().createSQLQuery("select  vh.id as voucherid ,vh.voucherNumber as voucherNumber ," +
						" redtl.remittedamt as receiptAmount,redtl.remittedamt as deductedAmount" +
						" FROM voucherheader vh,eg_remittance re,eg_remittance_detail redtl,generalledger gl" +
						" WHERE re.paymentvhid = "+paymentId+" AND re.id = redtl.remittanceid AND redtl.generalledgerid = gl.id AND gl.voucherheaderid =  " +
						"  vh.id group by vh.id,vh.voucherNumber,redtl.remittedamt order by vh.voucherNumber")
						.addScalar("voucherid").addScalar("voucherNumber")
						.addScalar("receiptAmount").addScalar("deductedAmount")
						.setResultTransformer(Transformers.aliasToBean(ChequeAssignment.class));
		viewReceiptDetailsList = query.list();  
		totalDeductedAmount = BigDecimal.ZERO;
		for(ChequeAssignment ch:viewReceiptDetailsList){
			totalDeductedAmount = totalDeductedAmount.add(ch.getDeductedAmount());
		}
		return "viewReceiptDetailsResult"; 
		
	}
	
	public void prepareBeforeSearchForRemittance()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepareBeforeSearchForRemittance...");
		paymentMode=FinancialConstants.MODEOFPAYMENT_CASH;
		
		if(getSession().get("recoveryList")==null)
		{
			List<Recovery> listRecovery = recoveryService.getAllActiveTds();
		getSession().put("RecoveryList", listRecovery);
		}
		addDropdownData("recoveryList" ,(List)getSession().get("recoveryList"));
		//overriding department Mandatory Condition only for remittance cheque assignment search
		deptNonMandatory=true;
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed prepareBeforeSearchForRemittance.");
	 
	}
	
	
	public void prepareBeforeRtgsSearch()
	{
if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepareBeforeRemittanceRtgsSearch...");
		
		addDropdownData("drawingofficerList",getPersistenceService().findAllBy("from DrawingOfficer order by code"));
		
		final List<Recovery> listRecovery = recoveryService.getAllActiveAutoRemitTds();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepareBeforeRtgsSearch...");
		paymentMode=FinancialConstants.MODEOFPAYMENT_RTGS;
		rtgsContractorAssignment=true;
		if(deptNonMandatory==true)
		{
			mandatoryFields.remove("department");
		}    
		if(mandatoryFields.contains("fund")){
			mandatoryFields.remove("fund");    
		}
	
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed prepareBeforeRtgsSearch.");
	 
	}           

	@SkipValidation
	@Action(value="/payment/chequeAssignment-beforeSearchForRemittance")
	public String beforeSearchForRemittance()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting beforeSearchForRemittance...");
		paymentMode=FinancialConstants.MODEOFPAYMENT_CASH;
		modeOfPaymentMap = new LinkedHashMap<String, String>();
		modeOfPaymentMap.put(FinancialConstants.MODEOFPAYMENT_CASH, getText("cash.consolidated.cheque"));                       
		List<Recovery> listRecovery = recoveryService.getAllActiveTds();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("RemitRecoveryAction | Tds list size : "+ listRecovery.size());
		addDropdownData("recoveryList" , listRecovery);
		//typeOfAccount = FinancialConstants.TYPEOFACCOUNT_PAYMENTS+","+FinancialConstants.TYPEOFACCOUNT_RECEIPTS_PAYMENTS;
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed beforeSearchForRemittance.");
		return "before_remittance_search";                                        
	}
	@ValidationErrorPage(value="rtgsSearch")
	@SkipValidation
	public String searchRTGS() throws EGOVException,ParseException            
	{    
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting searchRTGS...");
		List<ChequeAssignment> rtgsChequeAssignmentList = null;
		List<ChequeAssignment> dbpRtgsAssignmentList = null;
		List<ChequeAssignment> rtgsEntry =new ArrayList<ChequeAssignment>(); 
		Map<Bankaccount,List<ChequeAssignment>> tempMap;
		rtgsContractorAssignment=true;            
		String bankBranchName="";  
		boolean addList=false;
		Bankaccount bnkAcc= new Bankaccount();
		Bankaccount selBnkAcc= new Bankaccount();
		Set<Bankaccount> bankAccSet=new HashSet<Bankaccount>();
		ArrayList<Bankaccount> bankAccntList=new ArrayList<Bankaccount>();
		ChequeAssignment obj=new ChequeAssignment();
		
		rtgsChequeAssignmentList = paymentService.getPaymentVoucherForRTGSInstrument(parameters,voucherHeader);
		dbpRtgsAssignmentList = paymentService.getDirectBankPaymentVoucherForRTGSInstrument(parameters,voucherHeader);
	
		rtgsChequeAssignmentList.addAll(dbpRtgsAssignmentList);
		if(!paymentMode.equals(FinancialConstants.MODEOFPAYMENT_CHEQUE))
		{
			List<AppConfigValues> appList = appConfigValuesDAO.getConfigValuesByModuleAndKey(Constants.EGF,"cheque.assignment.infavourof");
			inFavourOf = appList.get(0).getValue();
		}
		chequeDt = new Date();
		for(ChequeAssignment chqAssgn:rtgsChequeAssignmentList){
			//to set date 
			if(accountNoAndRtgsEntryMap.isEmpty()){
				rtgsEntry=new ArrayList<ChequeAssignment>();
				bnkAcc = (Bankaccount) persistenceService.find("from Bankaccount where id=?", Integer.parseInt(chqAssgn.getBankAccountId().toString()));
				selBnkAcc=bnkAcc;              
				rtgsEntry.add(chqAssgn); 
				accountNoAndRtgsEntryMap.put(bnkAcc,rtgsEntry);   
				rtgsdateMap.put(bnkAcc.getId().toString(),formatter.format(currentDate));
			}else{
				Set<Bankaccount> bankAccntSet=accountNoAndRtgsEntryMap.keySet();  
				java.util.Iterator ir =bankAccntSet.iterator();
				  while (ir.hasNext()) {
					 Bankaccount bk = (Bankaccount) ir.next();
					 addList=false;
				     if(bk.getId().equals(Integer.parseInt(chqAssgn.getBankAccountId().toString()))){
							selBnkAcc=bk;addList=false;
							break;
				     }else{
				    	 	addList=true;
				     }
				 }              
				if(!addList){          
						accountNoAndRtgsEntryMap.get(selBnkAcc).add(chqAssgn);
						selBnkAcc=null;
					}           
				else{    
						rtgsEntry=new ArrayList<ChequeAssignment>();
						bnkAcc = (Bankaccount) persistenceService.find("from Bankaccount where id=?", Integer.parseInt(chqAssgn.getBankAccountId().toString()));
						selBnkAcc=bnkAcc;                                                     
						rtgsEntry.add(chqAssgn);
						accountNoAndRtgsEntryMap.put(selBnkAcc,rtgsEntry);
						rtgsdateMap.put(bnkAcc.getId().toString(),formatter.format(currentDate));
						selBnkAcc=null;          
				}
				
			}                                   
		}          
	getSession().put("accountNoAndRtgsEntryMapSession", accountNoAndRtgsEntryMap);
		//getSession().put("voucherIdOwnerNameMap", voucherIDOwnerNameMap);
		//chequeAssignmentList=rtgsChequeAssignmentList;
		//if(LOGGER.isInfoEnabled())     LOGGER.info("rtgsChequeAssignmentList>>>>>"+rtgsdateMap);         
		
		assignmentType="BillPayment";           
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed searchRTGS.");         
		return "searchRtgsResult";      
	}
	@SkipValidation
	public String searchTNEBRTGS() throws EGOVException,ParseException            
	{    
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting searchTNEBRTGS...");
		List<ChequeAssignment> rtgsChequeAssignmentList = null;
		List<ChequeAssignment> rtgsEntry =new ArrayList<ChequeAssignment>(); 
		Map<Bankaccount,List<ChequeAssignment>> tempMap;
		rtgsContractorAssignment=true;            
		String bankBranchName="";  
		boolean addList=false;
		Bankaccount bnkAcc= new Bankaccount();
		Bankaccount selBnkAcc= new Bankaccount();
		Set<Bankaccount> bankAccSet=new HashSet<Bankaccount>();
		ArrayList<Bankaccount> bankAccntList=new ArrayList<Bankaccount>();
		ChequeAssignment obj=new ChequeAssignment();
		
		rtgsChequeAssignmentList = paymentService.getPaymentVoucherForTNEBRTGSInstrument(parameters,voucherHeader);
	
		chequeDt = new Date();
		for(ChequeAssignment chqAssgn:rtgsChequeAssignmentList){
			//to set date 
			if(accountNoAndRtgsEntryMap.isEmpty()){
				rtgsEntry=new ArrayList<ChequeAssignment>();
				bnkAcc = (Bankaccount) persistenceService.find("from Bankaccount where id=?", Integer.parseInt(chqAssgn.getBankAccountId().toString()));
				selBnkAcc=bnkAcc;              
				rtgsEntry.add(chqAssgn); 
				accountNoAndRtgsEntryMap.put(bnkAcc,rtgsEntry);   
				rtgsdateMap.put(bnkAcc.getId().toString(),formatter.format(currentDate));
			}else{
				Set<Bankaccount> bankAccntSet=accountNoAndRtgsEntryMap.keySet();  
				java.util.Iterator ir =bankAccntSet.iterator();
				  while (ir.hasNext()) {
					 Bankaccount bk = (Bankaccount) ir.next();
					 addList=false;
				     if(bk.getId().equals(Integer.parseInt(chqAssgn.getBankAccountId().toString()))){
							selBnkAcc=bk;addList=false;
							break;
				     }else{
				    	 	addList=true;
				     }
				 }              
				if(!addList){          
						accountNoAndRtgsEntryMap.get(selBnkAcc).add(chqAssgn);
						selBnkAcc=null;
					}           
				else{    
						rtgsEntry=new ArrayList<ChequeAssignment>();
						bnkAcc = (Bankaccount) persistenceService.find("from Bankaccount where id=?", Integer.parseInt(chqAssgn.getBankAccountId().toString()));
						selBnkAcc=bnkAcc;                                                     
						rtgsEntry.add(chqAssgn);
						accountNoAndRtgsEntryMap.put(selBnkAcc,rtgsEntry);
						rtgsdateMap.put(bnkAcc.getId().toString(),formatter.format(currentDate));
						selBnkAcc=null;          
				}
				
			}                                   
		}          
	getSession().put("accountNoAndRtgsEntryMapSession", accountNoAndRtgsEntryMap);
		
		assignmentType="BillPayment";           
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed searchRTGS.");         
		return "searchRtgsResult";      
	}
	@ValidationErrorPage(value="search")         
	public String search() throws EGOVException,ParseException
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting search...");
		chequeSlNoMap=	loadChequeSerialNo(bankaccount);
		chequeAssignmentList = paymentService.getPaymentVoucherNotInInstrument(parameters,voucherHeader);
		if(!paymentMode.equals(FinancialConstants.MODEOFPAYMENT_CHEQUE))
		{
			chequeDt = new Date();
			List<AppConfigValues> appList = appConfigValuesDAO.getConfigValuesByModuleAndKey(Constants.EGF,"cheque.assignment.infavourof");
			inFavourOf = appList.get(0).getValue();
		}
		loadBankAndAccount();
		if(LOGGER.isInfoEnabled())     LOGGER.info(""+reassignSurrenderChq);
		assignmentType="BillPayment";
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed search.");
		return "searchpayment";      
	}
	@SuppressWarnings("unchecked")
	private Map<String,String> loadChequeSerialNo(Integer acc) {
		
		 chequeSlNoMap=new  LinkedHashMap<String, String>();
         List<String> cheueSlList = HibernateUtil.getCurrentSession().createSQLQuery("select distinct(serialNo) from  egf_account_cheques where bankAccountId="+acc+" order by serialNo desc ").list();
         if(cheueSlList!=null)
           for(String s:cheueSlList)
           {
        	   chequeSlNoMap.put(s,s);
           }
          return   chequeSlNoMap;      
	}  

	public Map getChequeSlNoMap() {
		return chequeSlNoMap;
	}

	public void setChequeSlNoMap(Map chequeSlNoMap) {
		this.chequeSlNoMap = chequeSlNoMap;
	}

	public void prepareSearchChequesOfRemittance()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepareSearchChequesOfRemittance...");
		beforeSearchForRemittance();
		assignmentType="RemittancePayment";
		mandatoryFields.add(Constants.RECOVERY);
	//overriding department Mandatory Condition only for remittance cheque assignment search		
		deptNonMandatory=true;
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed prepareSearchChequesOfRemittance.");
	}
	
	@ValidationErrorPage(value="before_salary_search")
	public String searchForSalaryPayments() throws EGOVException,ParseException
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting searchForSalaryPayments...");
		voucherHeader.setName(FinancialConstants.PAYMENTVOUCHER_NAME_SALARY);
		loadChequeSerialNo(bankaccount);
		chequeAssignmentList = paymentService.getPaymentVoucherNotInInstrument(parameters,voucherHeader); 
		if(LOGGER.isInfoEnabled())     LOGGER.info(""+reassignSurrenderChq);
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed searchForSalaryPayments.");
		return "searchsalpayment";      
	}
	
	@ValidationErrorPage(value="before_pension_search")
	public String searchForPensionPayments() throws EGOVException,ParseException
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting searchForPensionPayments...");
		voucherHeader.setName(FinancialConstants.PAYMENTVOUCHER_NAME_PENSION);
		loadChequeSerialNo(bankaccount);
		chequeAssignmentList = paymentService.getPaymentVoucherNotInInstrument(parameters,voucherHeader); 
		if(LOGGER.isInfoEnabled())     LOGGER.info(""+reassignSurrenderChq);
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed searchForSalaryPayments.");
		return "searchpensionpayment";      
	}


	@SkipValidation
	@Action(value="/payment/chequeAssignment-beforeSalarySearch")
	public String beforeSalarySearch()  
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting beforeSalarySearch...");
		modeOfPaymentMap = new LinkedHashMap<String, String>();
		modeOfPaymentMap.put(FinancialConstants.MODEOFPAYMENT_CASH, getText("cash.consolidated.cheque"));  
		modeOfPaymentMap.put(FinancialConstants.MODEOFPAYMENT_RTGS, getText("rtgs"));
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed beforeSalarySearch.");
		return "before_salary_search";                                        
	}
	
	@SkipValidation
	@Action(value="/payment/chequeAssignment-beforePensionSearch")
	public String beforePensionSearch()  
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting beforePensionSearch...");
		modeOfPaymentMap = new LinkedHashMap<String, String>();
		modeOfPaymentMap.put(FinancialConstants.MODEOFPAYMENT_CASH, getText("cash.consolidated.cheque"));  
		modeOfPaymentMap.put(FinancialConstants.MODEOFPAYMENT_RTGS, getText("rtgs"));
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed beforePensionSearch.");
		return "before_pension_search";                                        
	}
	public void prepareBeforeSalarySearch()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepareBeforeSalarySearch...");
		paymentMode=FinancialConstants.MODEOFPAYMENT_CASH;
		deptNonMandatory=true;
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed prepareBeforeSalarySearch.");
	}
	public void prepareBeforePensionSearch()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepareBeforePensionSearch...");
		paymentMode=FinancialConstants.MODEOFPAYMENT_CASH;
		deptNonMandatory=true;
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed prepareBeforePensionSearch.");
	}

	public void prepareSearchForSalaryPayments()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepareSearchForSalaryPayments...");
		beforeSalarySearch();
		deptNonMandatory=true;
		assignmentType="SalaryPayment";
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed prepareSearchForSalaryPayments.");
	}
	
	public void prepareSearchForPensionPayments()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting prepareSearchForPensionPayments...");
		beforePensionSearch();
		deptNonMandatory=true;
		assignmentType="PensionPayment";
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed prepareSearchForPensionPayments.");
	}
	
	@ValidationErrorPage(value="before_remittance_search")
	public String searchChequesOfRemittance() throws EGOVException,ParseException
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting searchChequesOfRemittance...");
		Recovery recovery=(Recovery)persistenceService.find("from Recovery where id=?",recoveryId);
		voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT);
		voucherHeader.setName(FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE);
		loadChequeSerialNo(bankaccount);
		chequeAssignmentList = paymentService.getPaymentVoucherNotInInstrument(parameters,voucherHeader); 
		inFavourOf=recovery.getRemitted();
		loadBankAndAccount();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed searchChequesOfRemittance.");
		return "searchremittance";      
	}
	private void loadBankAndAccount() {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting loadBankAndAccount..."); 
		if(voucherHeader.getFundId()!=null)
		{
			common.setTypeOfAccount(typeOfAccount);
			common.setFundId(voucherHeader.getFundId().getId());
			common.setAsOnDate(currentDate);
			common.ajaxLoadBanksWithApprovedPayments();
			addDropdownData("bankbranchList", common.getBankBranchList());
			bankBranchMap=new LinkedHashMap<String, String>();
			for(Map mp:common.getBankBranchList())
			{
				bankBranchMap.put((String)mp.get("bankBranchId"),(String)mp.get("bankBranchName"));	
			}
	
		}
		if(getBankbranch()!=null) 
		{
			common.setTypeOfAccount(typeOfAccount);
			common.setFundId(voucherHeader.getFundId().getId());
			//common.ajaxLoadBankAccounts();
			common.setBranchId(getBankbranch());			
			common.setAsOnDate(currentDate);
			common.ajaxLoadBankAccountsWithApprovedPayments();
			addDropdownData("bankaccountList",common.getAccNumList());
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed loadBankAndAccount.");
	}
	@ValidationErrorPage(value="searchRtgsResult")
	@SkipValidation
	public String update()throws  EGOVException,ParseException
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Start createInstrumentForRTGS");   
		Map<String,List<ChequeAssignment>> resultMap = new HashMap<String,List<ChequeAssignment>>();
		instHeaderList=new ArrayList<InstrumentHeader>();
		try{
			//validateData(); 
			resultMap=prepareMapForRTGS(); 
			if(!getFieldErrors().isEmpty()){
				//searchRTGS();
				accountNoAndRtgsEntryMap=	(Map<Bankaccount, List<ChequeAssignment>>)getSession().get("accountNoAndRtgsEntryMapSession");
				return "searchRtgsResult";             
			}                        
			createRtgsAssignment(resultMap);
		}catch(ValidationException e) 
		{  
			searchRTGS();
			LOGGER.error(e.getMessage());
			List<ValidationError> errors=new ArrayList<ValidationError>();
			errors.add(new ValidationError("exp",e.getMessage()));
			throw new ValidationException(e.getErrors());
		}
		catch(Exception e)
		{
			//searchRTGS();
			LOGGER.error(e.getMessage());
			List<ValidationError> errors=new ArrayList<ValidationError>();
			errors.add(new ValidationError("exp",e.getMessage()));
			throw new ValidationException(errors);
		}
		addActionMessage(getMessage("rtgs.transaction.success"));
		
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed createInstrument.");
		
		return "viewRtgs";
	}
	@ValidationErrorPage(value="searchpayment")
	@SkipValidation
	public String create()throws EGOVException
	
	{
		            
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting createInstrument...");
		loadChequeSerialNo(bankaccount);
		try               
		{                
			validateData();               
			if(reassignSurrenderChq ||!isChequeNoGenerationAuto())
				validateDataForManual();
			
			          
			if(getFieldErrors().isEmpty())  
			{
				if(reassignSurrenderChq && !paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS))
				{
				instHeaderList = paymentService.reassignInstrument(chequeAssignmentList, paymentMode,bankaccount, parameters,voucherHeader.getVouchermis().getDepartmentid());
				}
				
				else                 
				{
					instHeaderList = paymentService.createInstrument(chequeAssignmentList, paymentMode,bankaccount, parameters,voucherHeader.getVouchermis().getDepartmentid());	
				}
				selectedRows = paymentService.selectedRows;
				if(paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS)){
					addActionMessage(getMessage("rtgs.transaction.success"));
				}
				else{	
					addActionMessage(getMessage("chq.assignment.transaction.success"));
				}
				
					instVoucherList = paymentService.getInstVoucherList();
			}
			else   
			{
				loadChequeSerialNo(bankaccount);
					return "searchpayment";
			}
		}catch(ValidationException e) 
		{  
			throw new ValidationException(e.getErrors());
		}
		catch(Exception e)
		{
			LOGGER.error(e.getMessage());
			List<ValidationError> errors=new ArrayList<ValidationError>();
			errors.add(new ValidationError("exp",e.getMessage()));
			throw new ValidationException(errors);
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed createInstrument.");
		
		return "view";
	}

	private void createRtgsAssignment(	Map<String, List<ChequeAssignment>> resultMap) throws Exception {
		instVoucherList=new ArrayList<InstrumentVoucher>();      
		// InstrumentHeader instHeaderObj=new InstrumentHeader();
		 //InstrumentVoucher instVoucherObj=new InstrumentVoucher();
		 instVoucherDisplayList=new ArrayList<InstrumentHeader>();
		  for (Entry<String,List<ChequeAssignment>> row : resultMap.entrySet()) {
			  if(row.getKey()!=null){
				  if(getRtgsSeceltedAccMap().get(row.getKey())!=null && getRtgsSeceltedAccMap().get(row.getKey())){  
					  if(isRtgsNoGenerationAuto()){
						  String [] dateArray=new String[] {getRtgsdateMap().get(row.getKey())};
						 // LOGGER.debug(dateArray);
						  Date rtgsdate=null;
						  Date autoNoCutOffDate=FinancialConstants.RTGS_FINYEAR_WISE_ROLLING_SEQ_CUTOFF_DATE;
						  String rtgsNo="";
						  System.out.println(autoNoCutOffDate);
						  if(dateArray[0]!=null)
						  {
							  String date = dateArray[0];
							//  LOGGER.debug(date);
							  rtgsdate=formatter.parse(date);
							//  LOGGER.debug(rtgsdate);
						  }
						  if(null==autoNoCutOffDate || rtgsdate.after(autoNoCutOffDate))
						  {
							  String finYearRange = financialYearDAO.getFinancialYearByDate(rtgsdate).getFinYearRange();
							  //LOGGER.debug(finYearRange);
							  rtgsNo= getRtgsSequenceNumber("RTGS_RefNumber/"+finYearRange);
							  rtgsNo=rtgsNo+"/"+finYearRange;
						  }else
						  {
							  rtgsNo= getRtgsSequenceNumber("RTGS_RefNumber");		  
						  }
				  
						  
						//  LOGGER.debug(rtgsNo);
						  String[] refNoArray = new String[] {rtgsNo};
						  parameters.put("rtgsRefNo",refNoArray);                   
					  }else{
						  String[] refNoArray = new String[] {getRtgsRefNoMap().get(row.getKey())};
						  parameters.put("rtgsRefNo",refNoArray);
					  }
					  String [] dateArray=new String[] {getRtgsdateMap().get(row.getKey())};
					  parameters.put("rtgsDate",dateArray);    
					  chequeAssignmentList=resultMap.get(row.getKey());    
					  bankaccount=Integer.parseInt(row.getKey());
					  
					paymentService.createInstrument(chequeAssignmentList, paymentMode,bankaccount, parameters,voucherHeader.getVouchermis().getDepartmentid());
					instVoucherList.addAll( paymentService.getInstVoucherList()); 
					//  if(LOGGER.isInfoEnabled())     LOGGER.info("addded instrunment"+instList);          
				  }                                     
			  } /*if(!instList.isEmpty()&&instList.size()>0)   
				  instVoucherDisplayList.addAll( instList);   
		  }   if(LOGGER.isInfoEnabled())     LOGGER.info("addded instrunment"+instVoucherDisplayList);*/       
		  }
	}
	private Map<String, List<ChequeAssignment>> prepareMapForRTGS(	) throws ParseException{
		ArrayList<ChequeAssignment> rtgsEntry;
		List<ChequeAssignment> ContractorbillList=new ArrayList<ChequeAssignment>();  
		List<ChequeAssignment> SupplierbillList=new ArrayList<ChequeAssignment>();  
		List<ChequeAssignment> ContingentbillList=new ArrayList<ChequeAssignment>();
		Date rtgsdate=null;     
		 
		Map<String, List<ChequeAssignment>> resultMap=new HashMap<String, List<ChequeAssignment>>(); 
		List<InstrumentHeader> instList=new ArrayList<InstrumentHeader>();
	// 	if(expType==null || expType.equals("-1") || expType.equals("Purchase")) Works FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT
		for(ChequeAssignment chqAssgn:rtgsList){     
			if(resultMap.isEmpty()){
				rtgsEntry=new ArrayList<ChequeAssignment>();
				rtgsEntry.add(chqAssgn);             
				if(chqAssgn.getBankAccountId()!=null && !"".equals(chqAssgn.getBankAccountId()))        
					resultMap.put(chqAssgn.getBankAccountId().toString(),rtgsEntry); 
				if(chqAssgn.getIsSelected()){
					rtgsSeceltedAccMap.put(chqAssgn.getBankAccountId().toString(),true);
					if(chqAssgn.getExpenditureType()!=null && !"".equals(chqAssgn.getExpenditureType())) {
							if(chqAssgn.getExpenditureType().equalsIgnoreCase("Works") ){
								ContractorbillList.add(chqAssgn);
							}/*else if (chqAssgn.getExpenditureType().equalsIgnoreCase("Purchase") ){
								SupplierbillList.add(chqAssgn);
							}else if (chqAssgn.getExpenditureType().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT)) {
								ContingentbillList.add(chqAssgn);
							}*/
					}  
					rtgsdate=Constants.DDMMYYYYFORMAT2.parse(getRtgsdateMap().get(chqAssgn.getBankAccountId().toString())); 
					if(chqAssgn.getVoucherDate().compareTo(rtgsdate)>0){
						addFieldError("rtgs.date.less.than.payment.date","Payment date is less than RTGS Date"+chqAssgn.getVoucherNumber());
						                      
					}
				}
			}else{
				if(resultMap.containsKey(chqAssgn.getBankAccountId().toString())){
					resultMap.get(chqAssgn.getBankAccountId().toString()).add(chqAssgn); 
					if(chqAssgn.getIsSelected()){
						rtgsSeceltedAccMap.put(chqAssgn.getBankAccountId().toString(),true);
						if(chqAssgn.getExpenditureType()!=null && !"".equals(chqAssgn.getExpenditureType())) {
							if(chqAssgn.getExpenditureType().equalsIgnoreCase("Works") ){
								ContractorbillList.add(chqAssgn);
							}/*else if (chqAssgn.getExpenditureType().equalsIgnoreCase("Purchase") ){
								SupplierbillList.add(chqAssgn);
							}else if (chqAssgn.getExpenditureType().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT)) {
								ContingentbillList.add(chqAssgn);
							}*/
					 } rtgsdate=Constants.DDMMYYYYFORMAT2.parse(getRtgsdateMap().get(chqAssgn.getBankAccountId().toString())); 
						if(chqAssgn.getVoucherDate().compareTo(rtgsdate)>0){
							addFieldError("rtgs.date.less.than.payment.date","Payment date is less than RTGS Date"+chqAssgn.getVoucherNumber());       
						}
				  }
			    } else{      
					rtgsEntry=new ArrayList<ChequeAssignment>();
					rtgsEntry.add(chqAssgn);
					resultMap.put(chqAssgn.getBankAccountId().toString(),rtgsEntry);
					if(chqAssgn.getIsSelected()){
						rtgsSeceltedAccMap.put(chqAssgn.getBankAccountId().toString(),true);
						if(chqAssgn.getExpenditureType()!=null && !"".equals(chqAssgn.getExpenditureType())) {
							if(chqAssgn.getExpenditureType().equalsIgnoreCase("Works") ){
								ContractorbillList.add(chqAssgn);
							}/*else if (chqAssgn.getExpenditureType().equalsIgnoreCase("Purchase") ){
								SupplierbillList.add(chqAssgn);
							}else if (chqAssgn.getExpenditureType().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT)) {
								ContingentbillList.add(chqAssgn);
							}   */         
						}rtgsdate=Constants.DDMMYYYYFORMAT2.parse(getRtgsdateMap().get(chqAssgn.getBankAccountId().toString())); 
						if(chqAssgn.getVoucherDate().compareTo(rtgsdate)>0){
							addFieldError("rtgs.date.less.than.payment.date","Payment date is less than RTGS Date"+chqAssgn.getVoucherNumber());       
						} 
					}
				}
			} 
		} // validate RTGS entries 
		try {
			paymentService.validatePaymentForRTGSAssignment(ContractorbillList,"Contractor") ;
			//paymentService.validatePaymentForRTGSAssignment(SupplierbillList,"Supplier") ;
		//	paymentService.validatePaymentForRTGSAssignment(ContingentbillList,FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT) ;
		} catch(ValidationException e){  
			accountNoAndRtgsEntryMap=(Map<Bankaccount,List<ChequeAssignment>>)getSession().get("accountNoAndRtgsEntryMap");
			addFieldError("rtgs.payment.mandatory.details.missing",e.getErrors().get(0).getMessage());    
					//getMessage("rtgs.payment.mandatory.details.missing"));
		} catch (EGOVException e) {
			accountNoAndRtgsEntryMap=(Map<Bankaccount,List<ChequeAssignment>>)getSession().get("accountNoAndRtgsEntryMap");
			addFieldError("rtgs.payment.mandatory.details.missing",e.getMessage()); 
		}
		return resultMap;         
	}
	

	@ValidationErrorPage(value="searchsalpayment")
	@SkipValidation
	public String createInstrumentForSalaryPayment()throws EGOVException,ParseException
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting createInstrumentForSalaryPayment...");
	//	loadBankAndAccount();
		loadChequeSerialNo(bankaccount);
		
		try
		{
			validateData();   
			if(reassignSurrenderChq ||!isChequeNoGenerationAuto())
				validateDataForManual();
			if(getFieldErrors().isEmpty())
			{
				if(reassignSurrenderChq && !paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS))
				{
				instHeaderList = paymentService.reassignInstrument(chequeAssignmentList, paymentMode,bankaccount, parameters,voucherHeader.getVouchermis().getDepartmentid());
				}
				else
				{
					instHeaderList = paymentService.createInstrument(chequeAssignmentList, paymentMode,bankaccount, parameters,voucherHeader.getVouchermis().getDepartmentid());	
				}
				selectedRows = paymentService.selectedRows;
				if(paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS))
					addActionMessage(getMessage("rtgs.transaction.success"));
				else	
					addActionMessage(getMessage("chq.assignment.transaction.success"));
				
				
				instVoucherList = paymentService.getInstVoucherList();
				//if(paymentMode.equals(FinancialConstants.MODEOFPAYMENT_RTGS))
				//	return "bankadvice";
			}
			else
			{
				loadChequeSerialNo(bankaccount);
					return "searchsalpayment";
			}
		}catch(ValidationException e) 
		{  
			throw new ValidationException(e.getErrors());
		}
		catch(Exception e)
		{
			LOGGER.error(e.getMessage());
			List<ValidationError> errors=new ArrayList<ValidationError>();
			errors.add(new ValidationError("exp",e.getMessage()));
			throw new ValidationException(errors);
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed createInstrumentForSalaryPayment.");
		if(paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS))
		{	
			prepareBeforeSalarySearch();
			beforeSalarySearch();
			mandatoryFields.remove("department");
			return "before_salary_search";
		}
		else
			return "view";
	}
	
	protected String getRtgsSequenceNumber(String SEQ_RTGS_ReferenceNumber){
		String seqNo = sequenceGenerator.getNextNumber(SEQ_RTGS_ReferenceNumber,1L).getFormattedNumber() ; 
		seqNo=ackNumberFormat.format((Integer.parseInt(seqNo)));
		return seqNo;           
	}           
	   
	@ValidationErrorPage(value="searchpensionpayment")
	@SkipValidation
	public String createInstrumentForPensionPayment()throws EGOVException,ParseException
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting createInstrumentForPensionPayment...");
	//	loadBankAndAccount();
		loadChequeSerialNo(bankaccount);
		
		try
		{
			validateData();   
			if(reassignSurrenderChq ||!isChequeNoGenerationAuto())
				validateDataForManual();
			if(getFieldErrors().isEmpty())
			{
				if(reassignSurrenderChq && !paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS))
				{
					instHeaderList = paymentService.reassignInstrument(chequeAssignmentList, paymentMode,bankaccount, parameters,voucherHeader.getVouchermis().getDepartmentid());
				}
				else
				{
					instHeaderList = paymentService.createInstrument(chequeAssignmentList, paymentMode,bankaccount, parameters,voucherHeader.getVouchermis().getDepartmentid());	
				}
				selectedRows = paymentService.selectedRows;
				if(paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS))
					addActionMessage(getMessage("rtgs.transaction.success"));
				else	
					addActionMessage(getMessage("chq.assignment.transaction.success"));
				instVoucherList = paymentService.getInstVoucherList();
				//if(paymentMode.equals(FinancialConstants.MODEOFPAYMENT_RTGS))
				//	return "bankadvice";
			}
			else
			{
				loadChequeSerialNo(bankaccount);
					return "searchpensionpayment";
			}
		}catch(ValidationException e) 
		{  
			throw new ValidationException(e.getErrors());
		}
		catch(Exception e)
		{
			LOGGER.error(e.getMessage());
			List<ValidationError> errors=new ArrayList<ValidationError>();
			errors.add(new ValidationError("exp",e.getMessage()));
			throw new ValidationException(errors);
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed createInstrumentForPensionPayment.");
		if(paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS))
		{	
			prepareBeforePensionSearch();
			beforePensionSearch();
			mandatoryFields.remove("department");
			return "before_pension_search";
		}
		else
			return "view";
	}
	
	public void generateAdvice()throws Exception
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting generateAdvice...");
		BankAdviceForm baf;
		EntityType entity=null;
		String voucherno="";
		InstrumentHeader instrumentHeader = (InstrumentHeader) persistenceService.find("from InstrumentHeader where id=?", Long.valueOf(parameters.get("instHeaderId")[0]));
		for (InstrumentVoucher instrumentVoucher : instrumentHeader.getInstrumentVouchers()) 
		{
			Object[] obj = (Object[])persistenceService.find(" select gld.detailTypeId,gld.detailKeyId,gld.amount from CGeneralLedgerDetail gld,CGeneralLedger gl where gl.id=gld.generalLedgerId and gl.voucherHeaderId=?",instrumentVoucher.getVoucherHeaderId());
			if(obj!=null)
			{
				entity =  paymentService.getEntity((Integer)obj[0], (Serializable)obj[1]);
				baf = new BankAdviceForm();
				baf.setContractorCode(entity.getCode());
				baf.setContractorName(entity.getName());
				baf.setBankName(entity.getBankname());
				baf.setBankAccountNo(entity.getBankaccount());
				baf.setIfscCode(entity.getIfsccode());
				baf.setNetAmount((BigDecimal)obj[2]);
				voucherno = voucherno+instrumentVoucher.getVoucherHeaderId().getVoucherNumber()+",";
				adviceList.add(baf);
			}
		}
		
		if(adviceList.size()!=0) 
		{
			voucherno = voucherno.substring(0,voucherno.length()-1);
			paramMap.put("chequeNo", instrumentHeader.getInstrumentNumber());
			paramMap.put("chequeDate", paymentService.formatter.format(instrumentHeader.getInstrumentDate()));
			paramMap.put("pymtVhNo", voucherno);
			paramMap.put("inFavourOf", instrumentHeader.getPayTo());
			paramMap.put("pymtBank", instrumentHeader.getBankAccountId().getBankbranch().getBank().getName()+"-"+instrumentHeader.getBankAccountId().getBankbranch().getBranchname());
			paramMap.put("pymtAcNo", instrumentHeader.getBankAccountId().getAccountnumber());
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed generateAdvice.");
	}
	public void validateData()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting validateData...");
		// rtgsContractorAssignment is true only for rtgs assignment for asstbudget user 
		if(paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS)  )
		{        
			if(!rtgsContractorAssignment){
				if(rtgsRefNo==null || rtgsRefNo.equalsIgnoreCase("")  )
					addFieldError("rtgsrefno", getMessage("rtgs.refno.empty"));			
				if(rtgsDate==null || rtgsDate.equalsIgnoreCase(""))
					addFieldError("rtgsdate", getMessage("rtgs.date.empty"));
			}
			return;          
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed validateData.");
		checkMandatory("vouchermis.departmentid",Constants.DEPARTMENT,voucherHeader.getVouchermis().getDepartmentid(),"voucher.department.mandatory");
	}                                        
	@SkipValidation
	@Action(value="/payment/chequeAssignment-beforeSearchForSurrender")
	public String beforeSearchForSurrender()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting beforeSearchForSurrender...");
		addDropdownData("bankaccountList", Collections.EMPTY_LIST);
		loadBankAndAccounForSurender();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed beforeSearchForSurrender.");
		return SURRENDERSEARCH;
	}
	
	@SkipValidation
	@Action(value="/payment/chequeAssignment-beforeSearchForRTGSSurrender")
	public String beforeSearchForRTGSSurrender()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting beforeSearchForSurrender...");
		addDropdownData("bankaccountList", Collections.EMPTY_LIST);
		loadBankAndAccounForRTGSSurender();		
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed beforeSearchForSurrender.");
		return SURRENDERRTGSSEARCH;
	}
	
	@SkipValidation
	@ValidationErrorPage(value=SURRENDERSEARCH)
	public String searchChequesForSurrender()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting searchChequesForSurrender...");
		
		validateForSuurenderSearch();
		if(getFieldErrors().size()>0)
		{
			if(bank_branch!=null && !bank_branch.equals("-1"))
				addDropdownData("bankaccountList", persistenceService.findAllBy(" from Bankaccount where bankbranch.id=? and isactive=1 ",Integer.valueOf(bank_branch.split("-")[1])));
			loadReasonsForSurrendaring();	
			return beforeSearchForSurrender();	
		}
		
		StringBuffer sql =new StringBuffer();
		try {
			if(!"".equals(fromDate))
				sql.append(" and iv.voucherHeaderId.voucherDate>='"+sdf.format(formatter.parse(fromDate))+"' ");
			if(!"".equals(toDate))
				sql.append(" and iv.voucherHeaderId.voucherDate<='"+sdf.format(formatter.parse(toDate))+"'");
			if(bankaccount!=null && bankaccount!=-1)
				sql.append(" and  iv.instrumentHeaderId.bankAccountId.id="+bankaccount);
			if(instrumentNumber!=null && !instrumentNumber.isEmpty())
				sql.append(" and  iv.instrumentHeaderId.instrumentNumber='"+instrumentNumber+"'");
			if(department!=null && department!=-1 )
				{
				sql.append(" and  iv.voucherHeaderId.vouchermis.departmentid.id="+department);
				}
			if(voucherHeader.getVoucherNumber()!=null && !voucherHeader.getVoucherNumber().isEmpty() )
			{
			sql.append(" and  iv.voucherHeaderId.voucherNumber='"+voucherHeader.getVoucherNumber()+"'");
			}
			String mainquery = "select iv.instrumentHeaderId from  InstrumentVoucher iv  where   iv.voucherHeaderId.status=0  and iv.voucherHeaderId.type='"+FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT+"'  "+sql+" "
				
			+" and iv.instrumentHeaderId.statusId.id in (?)  order by iv.voucherHeaderId.voucherDate";
			EgwStatus created = instrumentService.getStatusId(FinancialConstants.INSTRUMENT_CREATED_STATUS);
			instrumentHeaderList = persistenceService.findAllBy(mainquery,created.getId());
			LinkedHashSet  lhs=new LinkedHashSet();
			lhs.addAll(instrumentHeaderList);
			instrumentHeaderList.clear();
			instrumentHeaderList.addAll(lhs);
			instrumentVoucherList=new ArrayList<InstrumentVoucher>();
			for(InstrumentHeader ih:instrumentHeaderList)
			  {
				  instrumentVoucherList.addAll(ih.getInstrumentVouchers());
			  }  
			 getSession().put("instrumentVoucherList",instrumentVoucherList);
			 getSession().put("instrumentHeaderList", instrumentHeaderList);
			
			if(instrumentVoucherList.size()>0)
			{
				loadReasonsForSurrendaring();
				loadChequeSerialNo(bankaccount);
			}
			
			  
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(Arrays.asList(new ValidationError("Unparsable Date", "Unparsable Date")));
		}
		getheader();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed searchChequesForSurrender.");
		return "surrendercheques";
	}
	
	
	@SkipValidation
	@ValidationErrorPage(value=SURRENDERRTGSSEARCH)
	@Action(value="/payment/chequeAssignment-searchForRTGSSurrender")
	public String searchForRTGSSurrender()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting searchRTGSForSurrender...");
		
		validateForSuurenderSearch();
		if(getFieldErrors().size()>0)
		{
			if(bank_branch!=null && !bank_branch.equals("-1"))
				addDropdownData("bankaccountList", persistenceService.findAllBy(" from Bankaccount where bankbranch.id=? and isactive=1 ",Integer.valueOf(bank_branch.split("-")[1])));
			loadReasonsForSurrendaring();	
			return beforeSearchForRTGSSurrender();	
		}
		
		StringBuffer sql =new StringBuffer();
		try {
			if(!"".equals(fromDate))
				sql.append(" and iv.voucherHeaderId.voucherDate>='"+sdf.format(formatter.parse(fromDate))+"' ");
			if(!"".equals(toDate))
				sql.append(" and iv.voucherHeaderId.voucherDate<='"+sdf.format(formatter.parse(toDate))+"'");
			if(bankaccount!=null && bankaccount!=-1)
				sql.append(" and  iv.instrumentHeaderId.bankAccountId.id="+bankaccount);
			if(instrumentNumber!=null && !instrumentNumber.isEmpty())
				sql.append(" and  iv.instrumentHeaderId.transactionNumber='"+instrumentNumber+"'");
			if(department!=null && department!=-1 )
				{
				sql.append(" and  iv.voucherHeaderId.vouchermis.departmentid.id="+department);
				}
			if(voucherHeader.getVoucherNumber()!=null && !voucherHeader.getVoucherNumber().isEmpty() )
			{
			sql.append(" and  iv.voucherHeaderId.voucherNumber='"+voucherHeader.getVoucherNumber()+"'");
			}
			String mainquery = "select iv.instrumentHeaderId from  InstrumentVoucher iv,InstrumentHeader ih where iv.instrumentHeaderId =ih.id and ih.transactionNumber is not null and ih.instrumentType=6 and   iv.voucherHeaderId.status=0  and iv.voucherHeaderId.type='"+FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT+"'  "+sql+" "
				
			+" and iv.instrumentHeaderId.statusId.id in (?)  order by iv.voucherHeaderId.voucherDate";
			EgwStatus created = instrumentService.getStatusId(FinancialConstants.INSTRUMENT_CREATED_STATUS);
			instrumentHeaderList = persistenceService.findAllBy(mainquery,created.getId());
			LinkedHashSet  lhs=new LinkedHashSet();
			lhs.addAll(instrumentHeaderList);
			instrumentHeaderList.clear();
			instrumentHeaderList.addAll(lhs);
			instrumentVoucherList=new ArrayList<InstrumentVoucher>();
			for(InstrumentHeader ih:instrumentHeaderList)
			  {
				  instrumentVoucherList.addAll(ih.getInstrumentVouchers());
			  }  
			 getSession().put("instrumentVoucherList",instrumentVoucherList);
			 getSession().put("instrumentHeaderList", instrumentHeaderList);
			
			if(instrumentVoucherList.size()>0)
			{
				loadReasonsForSurrendaring();
				//loadChequeSerialNo(bankaccount);
			}
			
			  
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(Arrays.asList(new ValidationError("Unparsable Date", "Unparsable Date")));
		}
		getheader();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed searchRTGSForSurrender.");
		return "surrenderRTGS";
	}
	/**
	 * 
	 */
	private void loadReasonsForSurrendaring() {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting loadReasonsForSurrendaring...");

		List<AppConfigValues> appConfigValuesList;
		surrendarReasonMap=new LinkedHashMap<String, String>();
		appConfigValuesList = appConfigValuesDAO.getConfigValuesByModuleAndKey("EGF", "Reason For Cheque Surrendaring");
		for(AppConfigValues app:appConfigValuesList)
		{
			String value = app.getValue();
			if(app.getValue().indexOf('|')!=-1)
			{
				surrendarReasonMap.put(app.getValue(),value.substring(0, app.getValue().indexOf('|')));
			}
			else
			{
			surrendarReasonMap.put(app.getValue(),app.getValue());
			}
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed loadReasonsForSurrendaring.");
		//return surrendarReasonMap;
	}

	/**
	 *    
	 */
	private void loadBankAndAccounForSurender() {
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting loadBankAndAccounForSurender...");
			common.setTypeOfAccount(typeOfAccount);
		//	common.ajaxLoadBankAccounts();
			common.setAsOnDate(currentDate);
			common.ajaxLoadBanksWithAssignedCheques();
			addDropdownData("bankbranchList",common.getBankBranchList());
			if(getBankbranch()!=null)
			{
			common.setBranchId(getBankbranch());
			//common.ajaxLoadBankAccounts();  
			common.setAsOnDate(currentDate);
			common.ajaxLoadBanksAccountsWithAssignedCheques();
			addDropdownData("bankaccountList",common.getAccNumList());
			}
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed loadBankAndAccounForSurender.");
	}
	
	
	private void loadBankAndAccounForRTGSSurender() {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting loadBankAndAccounForSurender...");
		common.setTypeOfAccount(typeOfAccount);
	//	common.ajaxLoadBankAccounts();
		common.setAsOnDate(currentDate);
		common.ajaxLoadBanksWithAssignedRTGS();
		addDropdownData("bankbranchList",common.getBankBranchList());
		if(getBankbranch()!=null)
		{
		common.setBranchId(getBankbranch());
		//common.ajaxLoadBankAccounts();  
		common.setAsOnDate(currentDate);
		common.ajaxLoadBanksAccountsWithAssignedRTGS();
		addDropdownData("bankaccountList",common.getAccNumList());
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed loadBankAndAccounForSurender.");
}
  
	/**
	 * 
	 */
	private void getheader() {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting getheader...");
		Bankaccount	account=(Bankaccount)persistenceService.find("from Bankaccount where id=?",bankaccount);
		bank_account_dept=account.getBankbranch().getBank().getName()+"-"+account.getBankbranch().getBranchname()
		+"-"+account.getAccountnumber();
		if(department!=null && department!=-1 )
		{
			Department dept=(Department)persistenceService.find("from Department where id=?",department);
			bank_account_dept=bank_account_dept+"-"+dept.getName();
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed getheader.");
	}
	@SkipValidation
	private void validateForSuurenderSearch() {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting validateForSuurenderSearch...");
	
		if(bankaccount==null || bankaccount==-1)
		{
			addFieldError("bankaccount", getMessage("bankaccount.empty"));
			
		}
		if(bank_branch==null || bank_branch.equals("-1"))
		{
			addFieldError("bankbranch", getMessage("bankbranch.empty"));	
			
		}
		if(isFieldMandatory("department") && (null == department || department == -1)){
			addFieldError("department", getMessage("validate.department.null"));	
		}
		
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed validateForSuurenderSearch.");
	}
@ValidationErrorPage(value="surrendercheques")
	@SkipValidation
	public String save()
	{
	if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting surrenderCheques...");
	StringBuffer reasonMissingRows=new StringBuffer(50);  
	loadBankAndAccount();	
	loadReasonsForSurrendaring();
	loadChequeSerialNo(bankaccount);
	
	try {
		boolean containsRTGS=false;		
		if(surrender==null)
		{
			throw new ValidationException(Arrays.asList(new ValidationError("Exception while surrender Cheque ",
			"Please select the atleast one Cheque for Surrendering ")));	
		}
			List<InstrumentHeader> suurenderChequelist=new ArrayList<InstrumentHeader>();
			List<String> chequeNoList=new ArrayList<String>();
			List<String> serialNoList=new ArrayList<String>();
			List<Date> chequeDateList=new ArrayList<Date>();
			InstrumentHeader instrumentHeader=null;
			if(null == department || -1 == department){
				throw new ValidationException(Arrays.asList(new ValidationError("Exception while surrender Cheque ",
				"please select department")));
			}
			int j=0;
			instrumentHeaderList=(List<InstrumentHeader>)getSession().get("instrumentHeaderList");
			String[] newSurrender=new String[instrumentHeaderList.size()];
			for (InstrumentHeader iheader:instrumentHeaderList)
			{
					newSurrender[j]=null;
						
			 for(String ih:surrender)
				{
				if(ih.equalsIgnoreCase(iheader.getId().toString()))
				{
					newSurrender[j]=ih;
				}
				}
			 j++;
			}
			surrender=newSurrender;
			if(surrender!=null && surrender.length>0)
			{
			for( int i=0;i<surrender.length;i++)
			{   
				if(surrender[i]==null)
					instrumentHeader=null;
				else
					instrumentHeader=(InstrumentHeader) persistenceService.find("from InstrumentHeader where id=?",Long.valueOf(surrender[i]));
				if(instrumentHeader!=null && (surrendarReasons[i]==null || surrendarReasons[i].equalsIgnoreCase("-1")))
				{
					reasonMissingRows.append(i+1);
					reasonMissingRows.append(",");
				}
				if(instrumentHeader!=null)  
				{
				instrumentHeader.setSurrendarReason(surrendarReasons[i]);
				suurenderChequelist.add(instrumentHeader);
				if(instrumentHeader.getTransactionNumber()!=null)
				{
					 containsRTGS = true;
				}
				}
				
			}
			if(!reasonMissingRows.toString().isEmpty())
			{
				throw new ValidationException(Arrays.asList(new ValidationError("Exception while surrender Cheque ",
						"please select the Reason for Surrendering the cheque for selected  rows")));	
			}
			instrumentService.surrenderCheques(suurenderChequelist);
			if(button.equalsIgnoreCase("surrenderAndReassign") && containsRTGS==true  )
			{
				throw new ValidationException(Arrays.asList(new ValidationError("Cannot reassign RTGS Numbers. Use RTGS Screen ",
						"Cannot reassign RTGS Numbers. Use RTGS Screen")));
			}
			else if(button.equalsIgnoreCase("surrenderAndReassign") && containsRTGS==false)
			{
			
			for( int i=0;i<surrender.length;i++)
			{
				if(surrender[i]!=null)
				{
					if(!isChequeNoGenerationAuto())
					{
						chequeNoList.add(newInstrumentNumber[i]);
						serialNoList.add(newSerialNo[i]);
						try {
							chequeDateList.add(formatter.parse(newInstrumentDate[i]));  
							
						} catch (ParseException e) {
							throw new ValidationException(Arrays.asList(new ValidationError("Exception while formatting ChequeDate ", "TRANSACTION_FAILED")));
						}
					}
					else
					{
						chequeNoList.add(getNewChequenumbers(instrumentHeader,department))	;
						chequeDateList.add(new Date());
						
					}
				}
				
			}
			
			
			 if(!isChequeNoGenerationAuto())
			 {
				 validateNewChequeNumbers(suurenderChequelist,chequeNoList,department,serialNoList);
				 if(getFieldErrors().size()>0)
				 {
					 throw new ValidationException(Arrays.asList(new ValidationError("TRANSACTION FAILED", "TRANSACTION FAILED")));
				 }
			 }
			//instrumentService.surrenderCheques(suurenderChequelist);
			 paymentMode="cheque";
			instHeaderList=addNewInstruments(suurenderChequelist,chequeNoList,chequeDateList,serialNoList);
			addActionMessage(getMessage("surrender.reassign.succesful"));
			}else
			{
				instHeaderList=suurenderChequelist;
				paymentMode="cheque";
				addActionMessage(getMessage("surrender.succesful"));	
			}
			
			}else {
				throw new ValidationException(Arrays.asList(new ValidationError("Exception while surrender ChequeDate ",
						"please select at least one cheque")));
			}
		} catch (ValidationException e) {
			instrumentVoucherList=(List<InstrumentVoucher>)getSession().get("instrumentVoucherList");
			instrumentHeaderList=(List<InstrumentHeader>)getSession().get("instrumentHeaderList");
			getheader();
			
			throw e;
		}
	if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed surrenderCheques.");
	return "viewsurrender";
	}
	
	/**
	 * @param suurenderChequelist
	 * @param chequeNoList
	 */
	private String getNewChequenumbers(InstrumentHeader instrumentHeader,Integer department ) {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting getNewChequenumbers...");
		/*int i=0;
		Integer dept=null;
		Set<InstrumentVoucher> instrumentVouchers = instrumentHeader.getInstrumentVouchers();
		for(InstrumentVoucher iv:instrumentVouchers)
			{
				dept=iv.getVoucherHeaderId().getVouchermis().getDepartmentid().getId();
				break;
			}*/
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed getNewChequenumbers.");
		return	chequeService.nextChequeNumber(instrumentHeader.getBankAccountId().getId().toString(), 1, department);
			
		
	}

	/**
	 * @param suurenderChequelist
	 */
	private List<InstrumentHeader> addNewInstruments(List<InstrumentHeader> suurenderChequelist, List<String> chequeNoList,List<Date> chequeDatelist, List<String> serialNoList)
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting addNewInstruments...");
		Map<String,Object> instrumentVoucherMap=null;
		List <Map<String,Object>> instrumentVoucherList=new ArrayList<Map<String,Object>>();
		instHeaderList=new ArrayList<InstrumentHeader>();
		
			int i=0;
			for(InstrumentHeader instrumentHeader:suurenderChequelist)
			{
			InstrumentHeader newInstrumentHeader = instrumentHeader.clone();
			newInstrumentHeader.setInstrumentNumber(chequeNoList.get(i).toString());
			newInstrumentHeader.setSerialNo(serialNoList.get(i).toString());  
			newInstrumentHeader.setStatusId(instrumentService.getStatusId(FinancialConstants.INSTRUMENT_CREATED_STATUS));
			newInstrumentHeader.setInstrumentDate((Date)chequeDatelist.get(i));
			i++;
			instHeaderList.add(instrumentService.instrumentHeaderService.persist(newInstrumentHeader));
			Set<InstrumentVoucher> instrumentVouchers = instrumentHeader.getInstrumentVouchers();
			
			for(InstrumentVoucher iv:instrumentVouchers)
			{
				instrumentVoucherMap=new HashMap<String,Object>();
				instrumentVoucherMap.put(VoucherConstant.VOUCHER_HEADER, iv.getVoucherHeaderId());
				instrumentVoucherMap.put(VoucherConstant.INSTRUMENT_HEADER, newInstrumentHeader);
				instrumentVoucherList.add(instrumentVoucherMap);
								
			}
			
			}
			instVoucherList = instrumentService.updateInstrumentVoucherReference(instrumentVoucherList);
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed addNewInstruments.");	
		return instHeaderList;
	}

	

	private void validateNewChequeNumbers(List<InstrumentHeader> suurenderChequelist, List<String> chequeNoList,Integer department,List<String> serialNoList) {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting validateNewChequeNumbers...");
	if(newInstrumentNumber!=null && newInstrumentNumber.length>0)
	{
		int i=0;
		Integer dept=null;
		InstrumentHeader instrumentHeader=null;
		for(i=0;i<suurenderChequelist.size();i++)
		{
			instrumentHeader= suurenderChequelist.get(i);
			/*Set<InstrumentVoucher> instrumentVouchers = instrumentHeader.getInstrumentVouchers();
			
			for(InstrumentVoucher iv:instrumentVouchers)
			{
				dept=iv.getVoucherHeaderId().getVouchermis().getDepartmentid().getId();
				if(dept!=null)
				{break;
				}
			}*/
			if(!instrumentService.isChequeNumberValid(chequeNoList.get(i),instrumentHeader.getBankAccountId().getId(),department, serialNoList.get(i)))
					addFieldError("newInstrumentNumber["+i+"]",getMessage("payment.chequenumber.invalid")+" "+chequeNoList.get(i).toString());
			}
	}
	if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed validateNewChequeNumbers.");
	}

	public void validateDataForManual() throws ParseException
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting validateDataForManual...");
		int i=0;
		Map<String,String> chqNoMap = new HashMap<String,String>();
		if(paymentMode.equals(FinancialConstants.MODEOFPAYMENT_CHEQUE))
		{
			for(ChequeAssignment assignment : chequeAssignmentList)
			{
				if(assignment.getIsSelected())
				{
					if(null ==assignment.getChequeNumber() || "".equals(assignment.getChequeNumber()))
					{
						addFieldError("chequeAssignmentList["+i+"].chequeNumber",getMessage("payment.chequeno.empty"));
						break;
					}
					else
					{
					if(reassignSurrenderChq)
					{
						if(!instrumentService.isReassigningChequeNumberValid(assignment.getChequeNumber(), bankaccount,voucherHeader.getVouchermis().getDepartmentid().getId().intValue(),assignment.getSerialNo()))
							addFieldError("chequeAssignmentList["+i+"].chequeNumber",getMessage("payment.chequenumber.invalid"));
					}
					else{
						if(!instrumentService.isChequeNumberValid(assignment.getChequeNumber(), bankaccount,voucherHeader.getVouchermis().getDepartmentid().getId().intValue(), assignment.getSerialNo()))
							addFieldError("chequeAssignmentList["+i+"].chequeNumber",getMessage("payment.chequenumber.invalid"));
					}
					}
					if(null ==assignment.getChequeDate())
					{
						addFieldError("chequeAssignmentList["+i+"].chequeNumber",getMessage("payment.chequeno.empty"));
						break;
					}
					if(assignment.getChequeDate().compareTo(assignment.getVoucherDate())<0){
						
						addFieldError("Cheque date cannot be less than paymnet date",getMessage("payment.chequedate.invalid"));
						break;
					}
					if(chqNoMap.containsKey(assignment.getChequeNumber()))
					{
						if(!chqNoMap.get(assignment.getChequeNumber()).equals(assignment.getPaidTo())) // can't give the same cheque to different parties.
							addFieldError("chequeAssignmentList["+i+"].chequeNumber",getMessage("payment.duplicate.chequeno",new String[]{assignment.getChequeNumber(),chqNoMap.get(assignment.getChequeNumber()),assignment.getPaidTo()}));
					}
					else
						chqNoMap.put(assignment.getChequeNumber(),assignment.getPaidTo());
					i++;
				}
			}
		}
		else  // cash or RTGS
		{
			/// Validations are done for RTGS payment mode
			String  chequedt=null;
			if(paymentMode.equalsIgnoreCase(FinancialConstants.MODEOFPAYMENT_RTGS))
				return;
			chequedt=parameters.get("chequeDt")[0];
			if(StringUtils.isEmpty(parameters.get("inFavourOf")[0] ))
				addFieldError("inFavourOf", getMessage("inFavourOf.is.empty"));
			if(StringUtils.isEmpty(parameters.get("chequeNo")[0] ))
				addFieldError("chequeNo", getMessage("payment.chequeno.empty"));
			else{
				for(int j=0;j<chequeAssignmentList.size();j++){     
					if(parameters.get("chequeAssignmentList["+j+"].isSelected")!=null && parameters.get("chequeAssignmentList["+j+"].isSelected")[0].equals("true")){                
					String paymentdt=	parameters.get("chequeAssignmentList["+j+"].tempPaymentDate")[0];   
					     
					if(formatter.parse(chequedt).compareTo(formatter.parse( paymentdt))<0){
						addFieldError("Cheque date cannot be less than paymnet date",getMessage("payment.chequedate.invalid"));     
					}
					}           
				}
				if(reassignSurrenderChq)   
				{                              
					if(!instrumentService.isReassigningChequeNumberValid(parameters.get("chequeNo")[0], bankaccount,voucherHeader.getVouchermis().getDepartmentid().getId().intValue(),parameters.get("serialNo")[0]))
						addFieldError("chequeAssignmentList["+i+"].chequeNumber",getMessage("payment.chequenumber.invalid"));
				}else{
				if(!instrumentService.isChequeNumberValid(parameters.get("chequeNo")[0], bankaccount,voucherHeader.getVouchermis().getDepartmentid().getId().intValue(), parameters.get("serialNo")[0]))
					addFieldError("chequeN0",getMessage("payment.chequenumber.invalid"));
				} 
			}
			if(null==getChequeDt())
				addFieldError("chequeDt", getMessage("payment.chequedate.empty"));
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed validateDataForManual.");
	}
	@SkipValidation
	public boolean validateUser(String purpose)throws ParseException
	{                
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting validateUser...");
		Script validScript = (Script) getPersistenceService().findAllByNamedQuery(Script.BY_NAME,"Paymentheader.show.bankbalance").get(0);
		List<String> list = null;//(List<String>) validScript.eval(Script.createContext("persistenceService",paymentService,"purpose",purpose));
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed validateUser.");
		if(list.get(0).equals("true"))
			return true;
		else 
			return false;
	}
	public void validate()
	{
		             
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting validate...");
		if(!rtgsContractorAssignment){
		checkMandatory("fundId",Constants.FUND,voucherHeader.getFundId(),"voucher.fund.mandatory");
		checkMandatory("vouchermis.departmentid",Constants.DEPARTMENT,voucherHeader.getVouchermis().getDepartmentid(),"voucher.department.mandatory");
		checkMandatory("vouchermis.schemeid",Constants.SCHEME,voucherHeader.getVouchermis().getSchemeid(),"voucher.scheme.mandatory");
		checkMandatory("vouchermis.subschemeid",Constants.SUBSCHEME,voucherHeader.getVouchermis().getSubschemeid(),"voucher.subscheme.mandatory");
		checkMandatory("vouchermis.functionary",Constants.FUNCTIONARY,voucherHeader.getVouchermis().getFunctionary(),"voucher.functionary.mandatory");
		checkMandatory("fundsourceId",Constants.FUNDSOURCE,voucherHeader.getVouchermis().getFundsource(),"voucher.fundsource.mandatory");
		checkMandatory("vouchermis.divisionId",Constants.FIELD,voucherHeader.getVouchermis().getDivisionid(),"voucher.field.mandatory");
		checkMandatory("Recovery Code",Constants.RECOVERY,recoveryId,"recovery.mandatory");	
		loadBankAndAccount();
		loadBillTypeMap();
		
		if(getBankbranch()==null || getBankbranch()==-1)
			addFieldError("bankbranch", getMessage("bankbranch.empty"));
		if(getBankaccount()==null || getBankaccount()==-1)
			addFieldError("bankaccount", getMessage("bankaccount.empty"));
		}
	
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed validate.");
	}

	private void loadBillTypeMap() {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting loadBillTypeMap...");
		billTypeMap=new HashMap<String, String>();
		billTypeMap.put(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT, FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT);
		billTypeMap.put(FinancialConstants.STANDARD_EXPENDITURETYPE_WORKS+"-"+FinancialConstants.STANDARD_EXPENDITURETYPE_PURCHASE, "Contractor/Supplier");
		billTypeMap.put(FinancialConstants.PAYMENTVOUCHER_NAME_DIRECTBANK, "No associated bills");
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed loadBillTypeMap.");
	}

	private void checkMandatory(String objectName,String fieldName,Object value,String errorKey)
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting checkMandatory...");
		if(mandatoryFields.contains(fieldName) && value == null)
			addFieldError(objectName, getMessage(errorKey));
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed checkMandatory.");
	}
	@SkipValidation
	public String ajaxGenerateAdviceHtml() throws Exception{ 
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting ajaxGenerateAdviceHtml...");
		generateAdvice();
		inputStream = reportHelper.exportHtml(inputStream, JASPER_PATH, getParamMap(),getAdviceList(),"pt");
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed ajaxGenerateAdviceHtml.");
		return "bankAdvice-HTML";
	}
	@SkipValidation
	public String generateAdvicePdf() throws Exception{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting generateAdvicePdf...");
		generateAdvice();
		inputStream = reportHelper.exportPdf(inputStream, JASPER_PATH, getParamMap(), getAdviceList());
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed generateAdvicePdf.");
		return "bankAdvice-PDF";
	}
	public Map<String, String> getBankBranchMap() {
		return bankBranchMap;
	}

	public void setBillTypeMap(Map<String, String> billTypeMap) {
		this.billTypeMap = billTypeMap;
	}
	public Map<String, String> getBillTypeMap() {
		return billTypeMap;
	}

	public void setBankBranchMap(Map<String, String> bankBranchMap) {
		this.bankBranchMap = bankBranchMap;
	}
	public Map<String, String> getBankAccountMap() {
		return bankAccountMap;
	}

	public void setBankAccountMap(Map<String, String> bankAccountMap) {
		this.bankAccountMap = bankAccountMap;
	}

	@SkipValidation
	public String generateAdviceXls() throws Exception{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Starting generateAdviceXls...");
		generateAdvice();
		inputStream = reportHelper.exportXls(inputStream, JASPER_PATH, getParamMap(), getAdviceList());
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Completed generateAdviceXls.");
		return "bankAdvice-XLS";
	}
	protected String getMessage(String key) {
		return getText(key);
	}
	protected String getMessage(String key,String[] value) {
		return getText(key,value);
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}
	public Integer getBankaccount() {
		return bankaccount;
	}
	public void setBankaccount(Integer bankaccount) {
		this.bankaccount = bankaccount;
	}
	public List<InstrumentHeader> getInstHeaderList() {
		return instHeaderList;
	}
	public void setInstHeaderList(List<InstrumentHeader> instHeaderList) {
		this.instHeaderList = instHeaderList;
	}
	public Integer getSelectedRows() {
		return selectedRows;
	}
	public void setSelectedRows(Integer selectedRows) {
		this.selectedRows = selectedRows;
	}
	public List<ChequeAssignment> getChequeAssignmentList() {
		return chequeAssignmentList;
	}
	public void setChequeAssignmentList(List<ChequeAssignment> chequeAssignmentList) {
		this.chequeAssignmentList = chequeAssignmentList;
	}
	public boolean isChequeNoGenerationAuto() {
		if(LOGGER.isInfoEnabled())     LOGGER.info(chequeNoGenerationAuto);
		return chequeNoGenerationAuto;
	}

	public void setChequeNoGenerationAuto(boolean chequeNoGenerationAuto) {
		this.chequeNoGenerationAuto = chequeNoGenerationAuto;
	}
	public void setInstrumentService(InstrumentService instrumentService) {
		this.instrumentService = instrumentService;
	}

	public Integer getBankbranch() {
		return bankbranch;
	}

	public void setBankbranch(Integer bankbranch) {
		this.bankbranch = bankbranch;
	}

	public Date getChequeDt() {
		return chequeDt;
	}

	public void setChequeDt(Date chequeDt) {
		this.chequeDt = chequeDt;
	}

	public String getInFavourOf() {
		return inFavourOf;
	}

	public void setInFavourOf(String inFavourOf) {
		this.inFavourOf = inFavourOf;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setReportHelper(final ReportHelper reportHelper) {
		this.reportHelper = reportHelper;
	}

	public Map<String,Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String,Object> paramMap) {
		this.paramMap = paramMap;
	}

	public List<Object> getAdviceList() {
		return adviceList;
	}

	public List<InstrumentVoucher> getInstVoucherList() {
		return instVoucherList;
	}

	public void setInstVoucherList(List<InstrumentVoucher> instVoucherList) {
		this.instVoucherList = instVoucherList;
	}
	
	
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public List<InstrumentVoucher> getInstrumentVoucherList() {
		return instrumentVoucherList;
	}
	public void setInstrumentVoucherList(List<InstrumentVoucher> instrumentVoucherList) {
		this.instrumentVoucherList = instrumentVoucherList;
	}
	public String getInstrumentNumber() {
		return instrumentNumber;
	}
	public void setInstrumentNumber(String instrumentNumber) {
		this.instrumentNumber = instrumentNumber;
	}
	public String[] getSurrender() {
		return surrender;
	}
	public void setSurrender(String[] surrender) {
		this.surrender = surrender;
	}
	public void setContraService(ContraService contraService) {
		this.contraService = contraService;
	}
	public String getBank_branch() {
		return bank_branch;
	}
	public void setBank_branch(String bank_branch) {
		this.bank_branch = bank_branch;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getButton() {
		return button;
	}
	public void setButton(String button) {
		this.button = button;
	}
	public void setChequeService(ChequeService chequeService) {
		this.chequeService = chequeService;
	}
	public String[] getNewInstrumentNumber() {
		return newInstrumentNumber;
	}
		public void setNewInstrumentNumber(String[] newInstrumentNumber) {
		this.newInstrumentNumber = newInstrumentNumber;
	}
	public Integer getDepartment() {
			return department;
		}
	public void setDepartment(Integer department) {
			this.department = department;
		}
	public String getBank_account_dept() {
		return bank_account_dept;
	}
	public void setBank_account_dept(String bank_account_dept) {
		this.bank_account_dept = bank_account_dept;
	}
	public String[] getNewInstrumentDate() {
		return newInstrumentDate;
	}
	public void setNewInstrumentDate(String[] newInstrumentDate) {
		this.newInstrumentDate = newInstrumentDate;
	}

	public List<InstrumentHeader> getInstrumentHeaderList() {
		return instrumentHeaderList;
	}

	public void setInstrumentHeaderList(List<InstrumentHeader> instrumentHeaderList) {
		this.instrumentHeaderList = instrumentHeaderList;
	}

	public void setEgovCommon(EgovCommon egovCommon) {
		this.egovCommon = egovCommon;
	}

	public String getTypeOfAccount() {
		return typeOfAccount;
	}

	public void setTypeOfAccount(String typeOfAccount) {
		this.typeOfAccount = typeOfAccount;
	}
	public CommonAction getCommon() {
		return common;
	}

	public void setCommon(CommonAction commonAction) {
		this.common = commonAction;
	}
	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	public RecoveryService getRecoveryService() {
		return recoveryService;              
	}
	public void setRecoveryService(RecoveryService recoveryService) {
		this.recoveryService = recoveryService;
	}
	public Long getRecoveryId() {
		return recoveryId;
	}
	public void setRecoveryId(Long recoveryId) {
		this.recoveryId = recoveryId;
	}
	public Map<String, String> getModeOfPaymentMap() {
		return modeOfPaymentMap;
	}
	public void setModeOfPaymentMap(Map<String, String> modeOfPaymentMap) {
		this.modeOfPaymentMap = modeOfPaymentMap;
	}
	public String[] getSurrendarReasons() {
		return surrendarReasons;
	}

	public void setSurrendarReasons(String[] surrendarReasons) {
		this.surrendarReasons = surrendarReasons;
	}

	public String[] getNewSerialNo() {
		return newSerialNo;
	}

	public void setNewSerialNo(String[] newSerialNo) {
		this.newSerialNo = newSerialNo;
	}

	public Boolean getRtgsContractorAssignment() {
		return rtgsContractorAssignment;
	}

	public void setRtgsContractorAssignment(Boolean rtgsContractorAssignment) {
		this.rtgsContractorAssignment = rtgsContractorAssignment;
	}

	public Map<Bankaccount, List<ChequeAssignment>> getAccountNoAndRtgsEntryMap() {
		return accountNoAndRtgsEntryMap;
	}

	public void setAccountNoAndRtgsEntryMap(
			Map<Bankaccount, List<ChequeAssignment>> accountNoAndRtgsEntryMap) {
		this.accountNoAndRtgsEntryMap = accountNoAndRtgsEntryMap;
	}
               
	public boolean isRtgsNoGenerationAuto() {
		return rtgsNoGenerationAuto;
	}

	public void setRtgsNoGenerationAuto(boolean rtgsNoGenerationAuto) {
		this.rtgsNoGenerationAuto = rtgsNoGenerationAuto;
	}

	
	public Map<String, String> getRtgsdateMap() {
		return rtgsdateMap;
	}

	public void setRtgsdateMap(Map<String, String> rtgsdateMap) {
		this.rtgsdateMap = rtgsdateMap;
	}

	public Map<String, String> getRtgsRefNoMap() {
		return rtgsRefNoMap;
	}

	public List<ChequeAssignment> getRtgsList() {
		return rtgsList;
	}

	public void setRtgsRefNoMap(Map<String, String> rtgsRefNoMap) {
		this.rtgsRefNoMap = rtgsRefNoMap;
	}

	public void setRtgsList(List<ChequeAssignment> rtgsList) {
		this.rtgsList = rtgsList;
	}

	public Map<String, Boolean> getRtgsSeceltedAccMap() {
		return rtgsSeceltedAccMap;
	}

	public void setRtgsSeceltedAccMap(Map<String, Boolean> rtgsSeceltedAccMap) {
		this.rtgsSeceltedAccMap = rtgsSeceltedAccMap;
	}

	public SequenceGenerator getSequenceGenerator() {
		return sequenceGenerator;
	}

	public void setSequenceGenerator(SequenceGenerator sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	public List<InstrumentHeader> getInstVoucherDisplayList() {
		return instVoucherDisplayList;
	}

	public void setInstVoucherDisplayList(
			List<InstrumentHeader> instVoucherDisplayList) {
		this.instVoucherDisplayList = instVoucherDisplayList;
	}

	public Integer getDrawingOfficerId() {
		return drawingOfficerId;
	}

	public void setDrawingOfficerId(Integer drawingOfficerId) {
		this.drawingOfficerId = drawingOfficerId;
	}

	public String getDrawingOfficerCode() {
		return drawingOfficerCode;
	}

	public void setDrawingOfficerCode(String drawingOfficerCode) {
		this.drawingOfficerCode = drawingOfficerCode;
	}

	public Map<BankAccountRemittanceCOA, List<ChequeAssignment>> getAccountNoAndRemittanceRtgsEntryMap() {
		return accountNoAndRemittanceRtgsEntryMap;
	}

	public void setAccountNoAndRemittanceRtgsEntryMap(
			Map<BankAccountRemittanceCOA, List<ChequeAssignment>> accountNoAndRemittanceRtgsEntryMap) {
		this.accountNoAndRemittanceRtgsEntryMap = accountNoAndRemittanceRtgsEntryMap;
	}

	public String getBillSubType() {
		return billSubType;
	}

	public void setBillSubType(String billSubType) {
		this.billSubType = billSubType;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getBank_account() {
		return bank_account;
	}

	public void setBank_account(String bank_account) {
		this.bank_account = bank_account;
	}
	public String getRecoveryCode() {
		return recoveryCode;
	}

	public void setRecoveryCode(String recoveryCode) {
		this.recoveryCode = recoveryCode;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public List<ChequeAssignment> getViewReceiptDetailsList() {
		return viewReceiptDetailsList;
	}

	public void setViewReceiptDetailsList(
			List<ChequeAssignment> viewReceiptDetailsList) {
		this.viewReceiptDetailsList = viewReceiptDetailsList;
	}

	public BigDecimal getTotalDeductedAmount() {
		return totalDeductedAmount;
	}

	public void setTotalDeductedAmount(BigDecimal totalDeductedAmount) {
		this.totalDeductedAmount = totalDeductedAmount;
	}

	public Boolean getNonSubledger() {
		return nonSubledger;
	}

	public void setNonSubledger(Boolean nonSubledger) {
		this.nonSubledger = nonSubledger;
	}

	public void setFinancialYearDAO(FinancialYearDAO financialYearDAO) {
		this.financialYearDAO = financialYearDAO;
	}
	
}
