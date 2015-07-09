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

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.EgModules;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.Fundsource;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.VoucherDetail;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.exceptions.EGOVException;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infra.web.utils.EgovPaginatedList;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.admin.master.entity.AppConfig;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infstr.services.Page;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.model.contra.ContraJournalVoucher;
import org.egov.model.payment.Paymentheader;
import org.egov.model.receipt.ReceiptVoucher;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.utils.ReportHelper;
import org.egov.utils.VoucherHelper;
import org.egov.web.actions.voucher.VoucherSearchAction;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.validator.annotations.Validation;
@Results(value={
		@Result(name="PDF",type="stream",location="inputStream", params={"inputName","inputStream","contentType","application/pdf","contentDisposition","no-cache;filename=VoucherStatusReport.pdf"}),
		@Result(name="XLS",type="stream",location="inputStream", params={"inputName","inputStream","contentType","application/xls","contentDisposition","no-cache;filename=VoucherStatusReport.xls"})
	})
@ParentPackage("egov")
@Validation
@Transactional(readOnly=true)
 public class VoucherStatusReportAction extends BaseFormAction
 {
	private static final Logger	LOGGER	= Logger.getLogger(VoucherSearchAction.class);
	public List<Map<String,Object>> voucherList;
	private static final long serialVersionUID = 1L;
	public CVoucherHeader voucherHeader = new CVoucherHeader();
	public final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Constants.LOCALE);
	public Date fromDate=new Date();
	public Date toDate=null;
	private final List<String> headerFields = new ArrayList<String>();
	private final List<String> mandatoryFields = new ArrayList<String>();
	InputStream inputStream;
	private static final String JASPERPATH = "/reports/templates/voucherStatusReport.jasper";
	ReportHelper reportHelper;
	List<Object> voucherReportList = new ArrayList<Object>(); 
	List<CVoucherHeader> voucherDisplayList = new ArrayList<CVoucherHeader>();
	private Map<Integer,String> statusMap;
	private Map<String,Object> paramMap=new HashMap<String,Object>();
	private Map<String, String> nameMap;
	private HashMap<Long, String> voucherIDOwnerNameMap;
	private Integer page=1;
	private Integer pageSize=30;
	private EgovPaginatedList pagedResults;
	private String countQry;
	private String modeOfPayment;
	@Autowired
	private FinancialYearDAO financialYearDAO;
	List<String> voucherTypes=VoucherHelper.VOUCHER_TYPES;
	Map<String,List<String>> voucherNames=VoucherHelper.VOUCHER_TYPE_NAMES;


	@Override
	public Object getModel() {
		return voucherHeader;
	}
	
	

	public VoucherStatusReportAction()
	{
		voucherHeader.setVouchermis(new Vouchermis());
		addRelatedEntity("vouchermis.departmentid", Department.class);
		addRelatedEntity("fundId", Fund.class);
		addRelatedEntity("vouchermis.schemeid", Scheme.class);
		addRelatedEntity("vouchermis.subschemeid", SubScheme.class);
		addRelatedEntity("vouchermis.functionary", Functionary.class);
		addRelatedEntity("vouchermis.divisionid", Boundary.class);  
		addRelatedEntity("fundsourceId", Fundsource.class);  
		
	}
	
	public void finYearDate(){
		
		String financialYearId= financialYearDAO.getCurrYearFiscalId();
		if(financialYearId==null || financialYearId.equals("")){
			fromDate=new Date();
		}
		else
			fromDate=(Date)persistenceService.find("select startingDate  from CFinancialYear where id=?",Long.parseLong(financialYearId));

	}
	
	public void prepare()
	{
	HibernateUtil.getCurrentSession().setDefaultReadOnly(true);
	HibernateUtil.getCurrentSession().setFlushMode(FlushMode.MANUAL);
		super.prepare();
		getHeaderFields();
		loadDropDowns();
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Number of  MIS attributes are :"+headerFields.size());
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Number of mandate MIS attributes are :"+mandatoryFields.size());
		statusMap = new HashMap<Integer,String>();
		statusMap.put(FinancialConstants.CREATEDVOUCHERSTATUS, "Approved");
		statusMap.put(FinancialConstants.REVERSEDVOUCHERSTATUS, "Reversed");
		statusMap.put(FinancialConstants.REVERSALVOUCHERSTATUS, "Reversal");
		statusMap.put(FinancialConstants.CANCELLEDVOUCHERSTATUS, "Cancelled");
		statusMap.put(FinancialConstants.PREAPPROVEDVOUCHERSTATUS, "Preapproved"); 
	}
	
	private void loadDropDowns() {

		if(headerFields.contains("department")){
			addDropdownData("departmentList", persistenceService.findAllBy("from Department order by deptName"));
		}
		if(headerFields.contains("functionary")){
			addDropdownData("functionaryList", persistenceService.findAllBy(" from Functionary where isactive=1 order by name"));
		}
		if(headerFields.contains("fund")){
			addDropdownData("fundList", persistenceService.findAllBy(" from Fund where isactive=1 and isnotleaf=0 order by name"));
		}
		if(headerFields.contains("fundsource")){
			addDropdownData("fundsourceList", persistenceService.findAllBy(" from Fundsource where isactive=1 and isnotleaf=0 order by name"));
		}
		if(headerFields.contains("field")){
			addDropdownData("fieldList", persistenceService.findAllBy(" from Boundary b where lower(b.boundaryType.name)='ward' "));
		}
		if(headerFields.contains("scheme")){
			addDropdownData("schemeList",  Collections.EMPTY_LIST );
		}
		if(headerFields.contains("subscheme")){
			addDropdownData("subschemeList", Collections.EMPTY_LIST);
		}
			//addDropdownData("typeList", persistenceService.findAllBy(" select distinct vh.type from CVoucherHeader vh  order by vh.type")); //where vh.status!=4
		addDropdownData("typeList",VoucherHelper.VOUCHER_TYPES);
		addDropdownData("modeOfPaymentList", persistenceService.findAllBy(" select DISTINCT upper(type) from Paymentheader "));
		nameMap=new LinkedHashMap<String, String> ();
	}
	protected void getHeaderFields() 
	{
		List<AppConfig> appConfigList = (List<AppConfig>) persistenceService.findAllBy("from AppConfig where key_name = 'DEFAULT_SEARCH_MISATTRRIBUTES'");
		for (AppConfig appConfig : appConfigList)
		{
			for (AppConfigValues appConfigVal : appConfig.getAppDataValues()) 
			{
				String value = appConfigVal.getValue();
				String header=value.substring(0, value.indexOf('|'));
				headerFields.add(header);
				String mandate = value.substring(value.indexOf('|')+1);
				if(mandate.equalsIgnoreCase("M")){
					mandatoryFields.add(header);
				}
			}
		}
		
	}
	public boolean shouldShowHeaderField(String field){
		return  headerFields.contains(field);
	}
	
	public Map<Integer, String> getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map<Integer, String> statusMap) {
		this.statusMap = statusMap;
	}

	
	@SkipValidation
@Action(value="/report/voucherStatusReport-beforeSearch")
	public String beforeSearch()
	{
		voucherHeader.reset();
		finYearDate();
		
		/*try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//
		//LOGGER.errorHibernateUtil.getCurrentSession().getFlushMode());
		return "search";
	}
	
	@ValidationErrorPage(value="search") 
	public String search() throws EGOVException,ParseException
	{
		voucherList = new ArrayList<Map<String,Object>>(); 
		Map<String,Object> voucherMap = null;
		voucherIDOwnerNameMap = new HashMap<Long,String>();
		Long voucherHeaderId;                                    
		String voucherOwner;
		Query qry=voucherSearchQuery();
		Long count = (Long)persistenceService.find(countQry);
		Page resPage=new Page(qry,page,pageSize);  
		pagedResults = new EgovPaginatedList(resPage, count.intValue());
		List <CVoucherHeader>list=	(pagedResults!=null?pagedResults.getList():null);
		for(CVoucherHeader voucherheader :list)
		{
			voucherMap = new HashMap<String,Object>(); 
			BigDecimal amt =BigDecimal.ZERO;
			voucherHeaderId = voucherheader.getId();
			voucherMap.put("id", voucherHeaderId);
			voucherMap.put("cgn", voucherheader.getCgn());
			voucherMap.put("vouchernumber", voucherheader.getVoucherNumber());
			voucherMap.put("type", voucherheader.getType());
			voucherMap.put("name", voucherheader.getName());		
			voucherMap.put("voucherdate", voucherheader.getVoucherDate());
			voucherMap.put("deptName", voucherheader.getVouchermis().getDepartmentid().getName());
			for(VoucherDetail detail:voucherheader.getVoucherDetail())
			{
				amt = amt.add(detail.getDebitAmount());
			}
			voucherMap.put("amount", amt);
			voucherMap.put("status",getVoucherStatus(voucherheader.getStatus()));
			voucherMap.put("source", getVoucherModule(voucherheader.getModuleId()));
			voucherOwner = getVoucherOwner(voucherheader);
			voucherMap.put("owner", voucherOwner);
			voucherIDOwnerNameMap.put(voucherHeaderId, voucherOwner);
			voucherList.add(voucherMap);
		}
		loadAjaxedData();
		pagedResults.setList(voucherList);
		return "search";
	}

	private void loadAjaxedData() {
		getVoucherNameMap(voucherHeader.getType());
		if (headerFields.contains("scheme")) {
			if (voucherHeader.getFundId() != null && voucherHeader.getFundId().getId() != -1) {
				StringBuffer st = new StringBuffer();
				st.append("from Scheme where isactive=1 and fund.id=");
				st.append(voucherHeader.getFundId().getId());
				dropdownData.put("schemeList", persistenceService.findAllBy(st.toString()));
				st.delete(0, st.length() - 1);

			} else
				dropdownData.put("schemeList", Collections.emptyList());
		}
		if (headerFields.contains("subscheme")) {
			if (voucherHeader.getVouchermis() != null
					&& voucherHeader.getVouchermis().getSchemeid() != null
					&& voucherHeader.getVouchermis().getSchemeid().getId() != -1) {

				dropdownData.put("subSchemeList", persistenceService.findAllBy(
						"from SubScheme where isactive=1 and scheme.id=?",
						voucherHeader.getVouchermis().getSchemeid().getId()));

			} else
				dropdownData.put("subSchemeList", Collections.emptyList());
		}
	}


public Map<String, String> getVoucherNameMap(String type) {
	List<Object> voucherNameList = getPersistenceService().findAllBy("select  distinct name from  CVoucherHeader where type=?",type);
	nameMap=new LinkedHashMap<String,String>();

	for(Object voucherName: voucherNameList )
	{
		nameMap.put((String)voucherName,(String)voucherName);
	}
	return nameMap;
}
	

	private Query voucherSearchQuery(){
		String sql="";
		
		if (!modeOfPayment.equals("-1")) {
			 sql = sql + " from CVoucherHeader vh,Paymentheader ph where vh.id = ph.voucherheader.id and";
		}
		else {
			sql =  sql +" from CVoucherHeader vh where ";
		}
		
		if (voucherHeader.getFundId() != null && voucherHeader.getFundId().getId() != -1) {
			sql = sql + "  vh.fundId=" + voucherHeader.getFundId().getId();
		}

		if (voucherHeader.getType() != null	&& !voucherHeader.getType().equals("-1")) {
			sql = sql + " and vh.type='" + voucherHeader.getType() + "'";
		}
		if (voucherHeader.getName() != null	&& !voucherHeader.getName().equalsIgnoreCase("-1")) {
			sql = sql + " and vh.name='" + voucherHeader.getName() + "'";
		}
		if (fromDate != null) {
			sql = sql + " and vh.voucherDate>='"+ Constants.DDMMYYYYFORMAT1.format(fromDate) + "'";
		}
		if (toDate != null) {
			sql = sql + " and vh.voucherDate<='"+ Constants.DDMMYYYYFORMAT1.format(toDate) + "'";
		}
		if (voucherHeader.getStatus() != -1) {
			sql = sql + " and vh.status=" + voucherHeader.getStatus();
		}

		if (voucherHeader.getVouchermis().getDepartmentid() != null	&& voucherHeader.getVouchermis().getDepartmentid().getId() != -1) {
			sql = sql + " and vh.vouchermis.departmentid="+ voucherHeader.getVouchermis().getDepartmentid().getId();
		}

		if (voucherHeader.getVouchermis().getSchemeid() != null) {
			sql = sql + " and vh.vouchermis.schemeid="+ voucherHeader.getVouchermis().getSchemeid().getId();
		}
		if (voucherHeader.getVouchermis().getSubschemeid() != null) {
			sql = sql + " and vh.vouchermis.subschemeid="+ voucherHeader.getVouchermis().getSubschemeid().getId();
		}
		if (voucherHeader.getVouchermis().getFunctionary() != null) {
			sql = sql + " and vh.vouchermis.functionary="+ voucherHeader.getVouchermis().getFunctionary().getId();
		}
		if (voucherHeader.getVouchermis().getDivisionid() != null) {
			sql = sql + " and vh.vouchermis.divisionid="+ voucherHeader.getVouchermis().getDivisionid().getId();
		}
		if (!modeOfPayment.equals("-1")) {
			sql = sql + " and upper(ph.type) ='"+getModeOfPayment()+"'";
		}
		countQry="select count(*) "+sql;
		sql= "select vh "+sql+" order by vh.vouchermis.departmentid.deptName , vh.voucherNumber";
		Query query = HibernateUtil.getCurrentSession().createQuery(sql);
		return query;
	}
	private String getVoucherModule(Integer vchrModuleId) throws EGOVException
	{
		if(vchrModuleId==null)
			return "Internal";
		else
		{  
			EgModules egModuleObj;
			egModuleObj=(EgModules)persistenceService.find("from EgModules m where m.id=?",vchrModuleId);
			if(egModuleObj==null)
				throw new EGOVException("INCORRECT MODULE ID");
			else
				return egModuleObj.getName();
		}
	}
	
	public void validate() {
		if(fromDate==null){
			addFieldError("From Date", getText("Please enter From Date"));
		}if(toDate==null){
			addFieldError("To Date", getText("Please enter To Date"));
		}
		checkMandatoryField("fundId","fund",voucherHeader.getFundId(),"voucher.fund.mandatory");
		checkMandatoryField("vouchermis.departmentid","department",voucherHeader.getVouchermis().getDepartmentid(),"voucher.department.mandatory");
		checkMandatoryField("vouchermis.schemeid","scheme",voucherHeader.getVouchermis().getSchemeid(),"voucher.scheme.mandatory");
		checkMandatoryField("vouchermis.subschemeid","subscheme",voucherHeader.getVouchermis().getSubschemeid(),"voucher.subscheme.mandatory");
		checkMandatoryField("vouchermis.functionary","functionary",voucherHeader.getVouchermis().getFunctionary(),"voucher.functionary.mandatory");
		checkMandatoryField("fundsourceId","fundsource",voucherHeader.getVouchermis().getFundsource(),"voucher.fundsource.mandatory");
		checkMandatoryField("vouchermis.divisionId","field",voucherHeader.getVouchermis().getDivisionid(),"voucher.field.mandatory");
	}

	

	@SuppressWarnings("unchecked")
	private void populateData() throws ParseException, EGOVException {
		List<CVoucherHeader> list=new ArrayList();
		list.addAll(voucherSearchQuery().list());
		BigDecimal amt =BigDecimal.ZERO;
		for(CVoucherHeader cVchrHdr:list)
		{
			VoucherReportView vhcrRptView = new VoucherReportView();
			vhcrRptView.setDeptName(cVchrHdr.getVouchermis().getDepartmentid().getName());
			vhcrRptView.setVoucherNumber(cVchrHdr.getVoucherNumber());
			vhcrRptView.setVoucherType(cVchrHdr.getType());
			vhcrRptView.setVoucherName(cVchrHdr.getName());
			vhcrRptView.setVoucherDate(cVchrHdr.getVoucherDate());
			vhcrRptView.setSource(getVoucherModule(cVchrHdr.getModuleId()));
			for(VoucherDetail detail:cVchrHdr.getVoucherDetail())
			{
				amt = amt.add(detail.getDebitAmount());
			}
			vhcrRptView.setAmount(amt);
			vhcrRptView.setOwner(getVoucherOwner(cVchrHdr));			
			vhcrRptView.setStatus(getVoucherStatus(cVchrHdr.getStatus()));
			voucherReportList.add(vhcrRptView);
			amt=BigDecimal.ZERO;            
		}
		
		setParamMap();
	}
	protected void checkMandatoryField(String objectName,String fieldName,Object value,String errorKey) 
	{
		if(mandatoryFields.contains(fieldName) && ( value == null || value.equals(-1) ))
		{
			addFieldError(objectName, getText(errorKey));
		}
	}
	public boolean isFieldMandatory(String field){
		return mandatoryFields.contains(field);
	}
	
	private String getVoucherStatus(int status)
	{
		if(FinancialConstants.CREATEDVOUCHERSTATUS.equals(status))
			return("Approved");
		if(FinancialConstants.REVERSEDVOUCHERSTATUS.equals(status))
			return("Reversed");
		if(FinancialConstants.REVERSALVOUCHERSTATUS.equals(status))
			return("Reversal");
		if(FinancialConstants.CANCELLEDVOUCHERSTATUS.equals(status))
			return("Cancelled");
		if(FinancialConstants.PREAPPROVEDVOUCHERSTATUS.equals(status))
			return("Preapproved");
		return "";
	}

	private String getVoucherOwner(CVoucherHeader voucherHeader)
	{
		String dash = "-";
		Integer voucherStatus = voucherHeader.getStatus();
		String voucherType = voucherHeader.getType();
		State voucherState =  null;
		if(voucherStatus.longValue() == FinancialConstants.CANCELLEDVOUCHERSTATUS.longValue()
				|| voucherStatus.longValue() == FinancialConstants.CREATEDVOUCHERSTATUS.longValue() )
		{
			return dash;
		}
		else if(voucherType.equalsIgnoreCase(FinancialConstants.STANDARD_VOUCHER_TYPE_CONTRA))
		{
			ContraJournalVoucher contraJV = (ContraJournalVoucher)persistenceService.find("from ContraJournalVoucher cj where cj.voucherHeaderId=?",voucherHeader);
			if(contraJV == null)
				return dash;
			else
				voucherState = contraJV.getState();
			if(voucherState == null)
			{
				return dash;
			}else if (voucherState.getValue().equals("END"))
			{
				return dash;
			}
			else
			{
				return getUserNameForPosition(voucherState.getOwnerPosition().getId().intValue());
			}
		}
		else if(voucherType.equalsIgnoreCase(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL))
		{
			voucherState = voucherHeader.getState();
			if(voucherState == null)
			{
				return dash;
			}
			else  if (voucherState.getValue().equals("END"))
			{
				return dash;
			}else
			{
				return getUserNameForPosition(voucherState.getOwnerPosition().getId().intValue());
			}
		}
		else if(voucherType.equalsIgnoreCase(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT))
		{
			Paymentheader paymentHeader = (Paymentheader)persistenceService.find("from Paymentheader ph where ph.voucherheader=?" , voucherHeader);
			if(paymentHeader == null)
				return dash;
			else
				voucherState = paymentHeader.getState();
			if(voucherState == null)
			{
				return dash;
			}else if (voucherState.getValue().equals("END"))
			{
				return dash;
			}
			else
			{
				return getUserNameForPosition(voucherState.getOwnerPosition().getId().intValue());
			}
		}
		else if(voucherType.equalsIgnoreCase(FinancialConstants.STANDARD_VOUCHER_TYPE_RECEIPT))
		{
			ReceiptVoucher receiptVoucher = (ReceiptVoucher)persistenceService.find("from ReceiptVoucher rv where rv.voucherHeader=?" , voucherHeader);
			if(receiptVoucher == null || receiptVoucher.getState() == null)
			{
				voucherState = voucherHeader.getState();
				if(voucherState == null)
					return dash;
				else  if (voucherState.getValue().equals("END"))
				{
					return dash;
				}else
					return getUserNameForPosition(voucherState.getOwnerPosition().getId().intValue());
			}
			else  if (voucherState.getValue().equals("END"))
			{
				return dash;
			}else
			{
				return getUserNameForPosition(receiptVoucher.getState().getOwnerPosition().getId().intValue());
			}
		}
		else
			return dash;
	}
	private String getUserNameForPosition (Integer posId) {
		String query = "select 	emp.userMaster  from org.egov.pims.model.EmployeeView emp where emp.position.id = ? ";
		User user = (User) persistenceService.find(query, posId);
		return user.getUsername();
	}
	public void setParamMap()
	{
			paramMap.put("fund", voucherHeader.getFundId().getName());
			if(voucherHeader.getVouchermis()!=null && voucherHeader.getVouchermis().getDepartmentid()!=null )
				paramMap.put("deptName", voucherHeader.getVouchermis().getDepartmentid().getName());
			paramMap.put("status", getVoucherStatus(voucherHeader.getStatus()));
			paramMap.put("toDate",toDate);
			paramMap.put("fromDate",fromDate);                     
			paramMap.put("voucherName",voucherHeader.getName());
			paramMap.put("voucherType",voucherHeader.getType());
	}
	public void setReportHelper(final ReportHelper reportHelper) {
		this.reportHelper = reportHelper;
	}
	public String generatePdf() throws Exception{
		populateData();
		inputStream = reportHelper.exportPdf(inputStream, JASPERPATH, getParamMap(),voucherReportList);
		return "PDF";
	}
	public String generateXls() throws Exception{
		populateData();
		inputStream = reportHelper.exportXls(inputStream, JASPERPATH, getParamMap(),voucherReportList);
		return "XLS";
	}
              
	protected Map<String, Object> getParamMap() {
		return paramMap;
	}
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setPagedResults(EgovPaginatedList pagedResults) {
		this.pagedResults = pagedResults;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public EgovPaginatedList getPagedResults() {
		return pagedResults;
	} 
	public Map<String, String> getNameMap() {
		return nameMap;
	}

	public void setNameMap(Map<String, String> nameMap) {
		this.nameMap = nameMap;
	}
	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
		
	

	public List<String> getVoucherTypes() {
		return voucherTypes;
	}



	public void setVoucherTypes(List<String> voucherTypes) {
		this.voucherTypes = voucherTypes;
	}



	public Map<String, List<String>> getVoucherNames() {
		return voucherNames;
	}



	public void setVoucherNames(Map<String, List<String>> voucherNames) {
		this.voucherNames = voucherNames;
	}
	public String getModeOfPayment() {
		return modeOfPayment;
	}



	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}


 }
