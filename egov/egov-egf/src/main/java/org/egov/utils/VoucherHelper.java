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
package org.egov.utils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.commons.CFiscalPeriod;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.dao.FiscalPeriodHibernateDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.commons.service.CommonsServiceImpl;
import org.egov.eis.entity.EmployeeView;
import org.egov.eis.service.EisCommonService;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.persistence.utils.DBSequenceGenerator;
import org.egov.infra.persistence.utils.SequenceNumberGenerator;
import org.egov.infra.script.entity.Script;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.infstr.utils.seqgen.DatabaseSequence;
import org.egov.infstr.utils.seqgen.DatabaseSequenceFirstTimeException;
import org.egov.model.bills.EgBillregister;
import org.egov.model.voucher.VoucherDetails;
import org.egov.pims.model.PersonalInformation;
import org.egov.pims.service.EisUtilService;
import org.hibernate.Query;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;

import com.exilant.eGov.src.common.EGovernCommon;
import com.exilant.exility.common.TaskFailedException;
/**
 * @author msahoo
 *
 */
public class VoucherHelper {
	private static final Logger	LOGGER	= Logger.getLogger(VoucherHelper.class);
	@SuppressWarnings("unchecked")
	private PersistenceService persistenceService;
	private EisCommonService eisCommonService;
	private EisUtilService eisUtilService;
	@Autowired
    private FundHibernateDAO fundDAO;
	@Autowired
	private ScriptService scriptService;
	@Autowired
	private EGovernCommon eGovernCommon;
	@Autowired
	private FiscalPeriodHibernateDAO fiscalDAO;
	@Autowired
	private CommonsServiceImpl commonsService;

    @Autowired
    private DBSequenceGenerator dbSequenceGenerator;

    @Autowired
    private SequenceNumberGenerator sequenceNumberGenerator;
    
	@SuppressWarnings("unchecked")
	public PersistenceService getPersistenceService() {
		return persistenceService;
	}

	@SuppressWarnings("unchecked")
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}
	
	public static String getMisQuery(CVoucherHeader voucherHeader){
		StringBuffer misQuery = new StringBuffer();
		if(null != voucherHeader && null != voucherHeader.getVouchermis()){
			if(null != voucherHeader.getVouchermis().getDepartmentid() ){
				misQuery.append(" and mis.departmentid.id=");
				misQuery.append(voucherHeader.getVouchermis().getDepartmentid().getId());
			}
			if(null != voucherHeader.getVouchermis().getFunctionary()){
				misQuery.append(" and mis.functionary.id=");
				misQuery.append(voucherHeader.getVouchermis().getFunctionary().getId());
			}if(null != voucherHeader.getVouchermis().getSchemeid()){
				misQuery.append(" and mis.schemeid.id=");
				misQuery.append(voucherHeader.getVouchermis().getSchemeid().getId());
			}
			if (null != voucherHeader.getVouchermis().getSubschemeid()) {
				misQuery.append(" and mis.subschemeid.id=");
				misQuery.append(voucherHeader.getVouchermis().getSubschemeid().getId());
			}
			if (null != voucherHeader.getVouchermis().getFundsource()) {
				misQuery.append(" and mis.fundsource.id=");
				misQuery.append(voucherHeader.getVouchermis().getFundsource().getId());
			}
			if (null != voucherHeader.getVouchermis().getDivisionid()) {
				misQuery.append(" and mis.divisionid.id=");
				misQuery.append(voucherHeader.getVouchermis().getDivisionid().getId());
			}
		}
		return misQuery.toString();
	}

  public static String getVoucherNumDateQuery(String voucherNumFrom , String voucherNumTo , String voucherDateFrom,String voucherDateTo){
	  StringBuffer numDateQuery = new StringBuffer();
	  try{
		if (null != voucherNumFrom && StringUtils.isNotEmpty(voucherNumFrom)) {
			numDateQuery.append(" and vh.voucherNumber >'");
			numDateQuery.append(voucherNumFrom).append("'");
		}
		if (null != voucherNumTo && StringUtils.isNotEmpty(voucherNumTo)) {
			numDateQuery.append(" and vh.voucherNumber <'");
			numDateQuery.append(voucherNumTo).append("'");
		}
		
			if(null != voucherDateFrom && StringUtils.isNotEmpty(voucherDateFrom)){
				numDateQuery.append(" and vh.voucherDate>='").append(Constants.DDMMYYYYFORMAT1.format(Constants.DDMMYYYYFORMAT2.
						parse(voucherDateFrom))).append("'");
			}
			if(null != voucherDateTo && StringUtils.isNotEmpty(voucherDateTo)){
				numDateQuery.append(" and vh.voucherDate<='").append(Constants.DDMMYYYYFORMAT1.format(Constants.DDMMYYYYFORMAT2.
						parse(voucherDateTo))).append("'");
			
		}
		}catch (ParseException e) {
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Exception occured while parsing date" + e);
		}
		catch (Exception e) {
			LOGGER.error(e);
			throw new EGOVRuntimeException("Error occured while executing search instrument query");
		}
		return numDateQuery.toString();
  	}
  
  public static String getBillMisQuery(EgBillregister egBillRegister){
	  
	  StringBuffer misQuery = new StringBuffer(300);
	  
	  if(null != egBillRegister && null != egBillRegister.getEgBillregistermis()){
		  
		  	if(null != egBillRegister.getEgBillregistermis().getFund()){
			  misQuery.append(" and billmis.fund.id=").append(egBillRegister.getEgBillregistermis().getFund().getId());
		  	}
		    
			if(null !=egBillRegister.getEgBillregistermis().getEgDepartment() ){
				misQuery.append(" and billmis.egDepartment.id=");
				misQuery.append(egBillRegister.getEgBillregistermis().getEgDepartment().getId());
			}
			if(null != egBillRegister.getEgBillregistermis().getFunctionaryid()){
				misQuery.append(" and billmis.functionaryid.id=");
				misQuery.append(egBillRegister.getEgBillregistermis().getFunctionaryid().getId());
			}if(null != egBillRegister.getEgBillregistermis().getScheme()){
				misQuery.append(" and billmis.scheme.id=");
				misQuery.append(egBillRegister.getEgBillregistermis().getScheme().getId());
			}
			if (null != egBillRegister.getEgBillregistermis().getSubScheme()) {
				misQuery.append(" and billmis.subScheme.id=");
				misQuery.append(egBillRegister.getEgBillregistermis().getSubScheme().getId());
			}
			if (null != egBillRegister.getEgBillregistermis().getFundsource()) {
				misQuery.append(" and billmis.fundsource.id=");
				misQuery.append( egBillRegister.getEgBillregistermis().getFundsource().getId());
			}
			if (null != egBillRegister.getEgBillregistermis().getFieldid()) {
				misQuery.append(" and billmis.fieldid.id=");
				misQuery.append(egBillRegister.getEgBillregistermis().getFieldid().getId());
			}
			if (null != egBillRegister.getBillnumber() && !egBillRegister.getBillnumber().equalsIgnoreCase("")) {
				misQuery.append(" and br.billnumber=");
				misQuery.append("'"+egBillRegister.getBillnumber()+"'");
			}
		}
		return misQuery.toString();
	  
	  
  }

  /**
   * Constructs the corresponding sequence name for an EG_NUMBERS record.
   * E.g. if vouchertype = "1/CJV/CGVN" and fiscalperiod name = "FP7", then it returns
   * "SQ_1_CJV_CGVN_FP7".
   *
   * @param voucherType
   * @param fiscalPeriodName
   * @return
   */
  public static String sequenceNameFor(String voucherType, String fiscalPeriodName) {
      return new StringBuilder()
      .append(DatabaseSequence.SEQUENCE_NAME_PREFIX)
      .append(DatabaseSequence.replaceBadChars(voucherType))
      .append(DatabaseSequence.WORD_SEPARATOR_FOR_NAME)
      .append(fiscalPeriodName)
      .toString();
  }

  
  public static String getBillDateQuery(String billDateFrom,String billDateTo){
	  StringBuffer numDateQuery = new StringBuffer();
	  try{
		
		
			if(null != billDateFrom && StringUtils.isNotEmpty(billDateFrom)){
				numDateQuery.append(" and br.billdate>='").append(Constants.DDMMYYYYFORMAT1.format(Constants.DDMMYYYYFORMAT2.
						parse(billDateFrom))).append("'");
			}
			if(null != billDateTo && StringUtils.isNotEmpty(billDateTo)){
				numDateQuery.append(" and br.billdate<='").append(Constants.DDMMYYYYFORMAT1.format(Constants.DDMMYYYYFORMAT2.
						parse(billDateTo))).append("'");
			
		}
		}catch (ParseException e) {
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Exception occured while parsing date" + e);
		}
		catch (Exception e) {
			LOGGER.error(e);
			throw new EGOVRuntimeException("Error occured while executing search instrument query");
		}
		return numDateQuery.toString();
  	}
  public String getEg_Voucher(String vouType,String fiscalPeriodIdStr) throws TaskFailedException,Exception
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug(" In EGovernCommon :getEg_Voucher method ");
		Query query = HibernateUtil.getCurrentSession().createSQLQuery("select name from fiscalperiod where id="+Integer.parseInt(fiscalPeriodIdStr)+"");
		List<String> fc  = query.list();
		//Sequence name will be SQ_U_DBP_CGVN_FP7 for vouType U/DBP/CGVN and fiscalPeriodIdStr 7
		 Serializable sequenceNumber;
		 String sequenceName ;
		 sequenceName = sequenceNameFor(vouType, fc.get(0).toString());
		try {
            sequenceNumber = sequenceNumberGenerator.getNextSequence(sequenceName);
        } catch (final SQLGrammarException e) {
            sequenceNumber = dbSequenceGenerator.createAndGetNextSequence(sequenceName);
        }
		return sequenceNumber.toString();
		
}
	public  String getGeneratedVoucherNumber(Integer fundId, String voucherType, Date voucherDate, String vNumGenMode, String voucherNumber)  throws Exception {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("fundId | in getGeneratedVoucherNumber      :"+fundId);
		if(LOGGER.isDebugEnabled())     LOGGER.debug("voucherType | in getGeneratedVoucherNumber :"+voucherType);
		if(LOGGER.isDebugEnabled())     LOGGER.debug("voucherDate | in getGeneratedVoucherNumber :"+voucherDate);
		String vDate = Constants.DDMMYYYYFORMAT2.format(voucherDate);
		String vDateTemp = Constants.DDMMYYYYFORMAT1.format(voucherDate);
		CFiscalPeriod	fiscalPeriod=fiscalDAO.getFiscalPeriodByDate(voucherDate);
		Fund vFund=fundDAO.fundById(fundId);
		String fundIdentifier =vFund.getIdentifier().toString();
		String sequenceName= sequenceNameFor(fundIdentifier+"/"+voucherType,fiscalPeriod.getName());
		String transNumber = "";
		if(vNumGenMode == null) vNumGenMode = "Auto";
		if(voucherNumber == null) voucherNumber = "";
		if(voucherType == null) voucherType = "";
		String fVoucherNumber=null;
		
		if( vNumGenMode.equalsIgnoreCase("Auto"))
		{
		if(LOGGER.isInfoEnabled())     LOGGER.info(" before transNumber................"+transNumber);
		/*if(vNumGenMode.equalsIgnoreCase("Auto"))
			transNumber = cmImpl.getTransRunningNumber(fundId.toString(),voucherType,vDate,conn);
		if(LOGGER.isInfoEnabled())     LOGGER.info("after transNumber..........................."+transNumber);
		*/String monthArr[] = vDate.split("/");
		String month = (String) monthArr[1];
		
		String scriptName = "voucherheader.vouchernumber";
		Script voucherNumberScript=scriptService.getByName( scriptName);
		ScriptContext scriptContext = ScriptService.createContext("fundIdentity", fundIdentifier, "voucherType", voucherType, "transNumber", 
				transNumber, "vNumGenMode", vNumGenMode,  "date", voucherDate, "month", month,"commonsService",commonsService, "dbSequenceGenerator",dbSequenceGenerator,"sequenceNumberGenerator",sequenceNumberGenerator,"voucherNumber", voucherNumber,"sequenceName",sequenceName );
		fVoucherNumber = (String)scriptService.executeScript(scriptName, scriptContext);
	
		}
		
		else
		{
				//	this is not client specific  needs to be from script  only
			fVoucherNumber=fundIdentifier+"/"+voucherType+"/"+voucherNumber;
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("fVoucherNumber | fVoucherNumber in getGeneratedVoucherNumber :"+fVoucherNumber);
		
		// unique checking...
		if(LOGGER.isDebugEnabled())     LOGGER.debug("unique checking for voucher number :- "+fVoucherNumber);
		
		
		
		if(!eGovernCommon.isUniqueVN(fVoucherNumber,vDateTemp, null))
		{
			throw new EGOVRuntimeException("Trying to create Duplicate Voucher Number");
		}
		return fVoucherNumber;
	}
	
	/**
	 * return the glcodes that are repeated. e.g [1,2,2,2,3,3] returns [2,3]
	 * @param billDetailslist
	 * @return
	 */
	public static List<String> getRepeatedGlcodes(List<VoucherDetails> billDetailslist){
		
		List<String> list = new ArrayList<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		for (VoucherDetails voucherDetail : billDetailslist) {
			String glCodeIdDetail = voucherDetail.getGlcodeIdDetail().toString();
			if(map.containsKey(glCodeIdDetail)){
				list.add(glCodeIdDetail);
			}else{
				map.put(glCodeIdDetail,glCodeIdDetail);
			}	
		}
		return list;
	}

	public  List<Department> getAllAssgnDeptforUser(){
		// load the  primary and secondary assignment departments of the logged in user 
		PersonalInformation employee = eisCommonService.getEmployeeByUserId(EgovThreadLocals.getUserId());
		HashMap<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("code", employee.getCode());
		List<EmployeeView> listEmployeeView =null;//Phoenix eisUtilService.getEmployeeInfoList(paramMap);
		List<Department> departmentList = new ArrayList<Department> ();
		for (EmployeeView employeeView : listEmployeeView) {
			employeeView.getDepartment().getName();
			if(employeeView.getPrimary())
				departmentList.add(0, employeeView.getDepartment());
			else
				departmentList.add(employeeView.getDepartment());
		}
		return departmentList;
	}

	public static final List<String> VOUCHER_TYPES = new ArrayList<String>() {
		{
			add(FinancialConstants.STANDARD_VOUCHER_TYPE_CONTRA);
			add(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT);
			add(FinancialConstants.STANDARD_VOUCHER_TYPE_RECEIPT);
			add(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
		}
	};
	public static final List<String> CONTRAVOUCHER_NAMES = new ArrayList<String>() {
		{
			add(FinancialConstants.CONTRAVOUCHER_NAME_BTOB);
			add(FinancialConstants.CONTRAVOUCHER_NAME_PAYIN);
			add(FinancialConstants.CONTRAVOUCHER_NAME_INTERFUND);
		}
		
	};
	public static final List<String> PAYMENTVOUCHER_NAMES = new ArrayList<String>() {
		{
		
			add(FinancialConstants.PAYMENTVOUCHER_NAME_BILL);
			add(FinancialConstants.PAYMENTVOUCHER_NAME_DIRECTBANK);
			add(FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE);
			add(FinancialConstants.PAYMENTVOUCHER_NAME_SALARY);
			add(FinancialConstants.PAYMENTVOUCHER_NAME_PENSION);
			add(FinancialConstants.PAYMENTVOUCHER_NAME_ADVANCE);
		}
		
	};
	public static final List<String> JOURNALVOUCHER_NAMES = new ArrayList<String>() {
		{
			
			add(FinancialConstants.JOURNALVOUCHER_NAME_GENERAL);
			add(FinancialConstants.JOURNALVOUCHER_NAME_SUPPLIERJOURNAL);
			add(FinancialConstants.JOURNALVOUCHER_NAME_CONTRACTORJOURNAL);
			add(FinancialConstants.JOURNALVOUCHER_NAME_SALARYJOURNAL);
			add(FinancialConstants.JOURNALVOUCHER_NAME_EXPENSEJOURNAL);
			add(FinancialConstants.JOURNALVOUCHER_NAME_PENSIONJOURNAL);
			add(FinancialConstants.JOURNALVOUCHER_NAME_ISSUE);
			add(FinancialConstants.JOURNALVOUCHER_NAME_SUPPLIERRECEIPT);
			add(FinancialConstants.JOURNALVOUCHER_NAME_LE_DEMAND);
			add(FinancialConstants.JOURNALVOUCHER_NAME_RECEIPT_REVERSAL);
		}
		
	};
	public static final List<String> RECEIPT_NAMES = new ArrayList<String>() {
		{
			add(FinancialConstants.RECEIPT_NAME_DIRECT);
			add(FinancialConstants.RECEIPT_NAME_PAYMENT_REVERSAL);
			add(FinancialConstants.RECEIPT_NAME_OTHER_RECEIPTS);
		}
		
	};
	public static final Map<String, List<String>> VOUCHER_TYPE_NAMES = new HashMap<String, List<String>>(){
		{
			put(VOUCHER_TYPES.get(0),CONTRAVOUCHER_NAMES);
			put(VOUCHER_TYPES.get(1),PAYMENTVOUCHER_NAMES);
			put(VOUCHER_TYPES.get(2),RECEIPT_NAMES);
			put(VOUCHER_TYPES.get(3),JOURNALVOUCHER_NAMES);
		}
	};
	public static final List<String> TNEB_REGIONS = new ArrayList<String>() {
		{
			add(FinancialConstants.REGION_SE_CEDC_NORTH);
			add(FinancialConstants.REGION_SE_CEDC_SOUTH);
			add(FinancialConstants.REGION_SE_CEDC_CENTRAL);
			add(FinancialConstants.REGION_SE_CEDC_EAST);
			add(FinancialConstants.REGION_SE_CEDC_WEST);
		}
		
	};
	public EisCommonService getEisCommonService() {
		return eisCommonService;
	}

	public void setEisCommonService(EisCommonService eisCommonService) {
		this.eisCommonService = eisCommonService;
	}

	public void setEisUtilService(EisUtilService eisUtilService) {
		this.eisUtilService = eisUtilService;
	}

	public EGovernCommon geteGovernCommon() {
		return eGovernCommon;
	}

	public void seteGovernCommon(EGovernCommon eGovernCommon) {
		this.eGovernCommon = eGovernCommon;
	}
	
}
