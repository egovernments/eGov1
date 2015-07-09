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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.commons.utils.EntityType;
import org.egov.deduction.model.EgRemittanceDetail;
import org.egov.egf.commons.EgovCommon;
import org.egov.exceptions.EGOVException;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.reporting.engine.ReportConstants.FileFormat;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.utils.DateUtils;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.model.deduction.RemittanceBean;
import org.egov.model.instrument.InstrumentVoucher;
import org.egov.model.recoveries.Recovery;
import org.egov.services.deduction.RemitRecoveryService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.FlushMode;
import org.springframework.transaction.annotation.Transactional;




@Results(value={
		@Result(name="PDF",type="stream",location="inputStream", params={"inputName","inputStream","contentType","application/pdf","contentDisposition","no-cache;filename=PendingTDSReport.pdf"}),
		@Result(name="XLS",type="stream",location="inputStream", params={"inputName","inputStream","contentType","application/xls","contentDisposition","no-cache;filename=PendingTDSReport.xls"}),
		@Result(name="summary-PDF",type="stream",location="inputStream", params={"inputName","inputStream","contentType","application/pdf","contentDisposition","no-cache;filename=TdsSummaryReport.pdf"}),
		@Result(name="summary-XLS",type="stream",location="inputStream", params={"inputName","inputStream","contentType","application/xls","contentDisposition","no-cache;filename=TdsSummaryReport.xls"})
	})
@Transactional(readOnly=true)
@ParentPackage("egov")
public class PendingTDSReportAction extends BaseFormAction{
	String jasperpath = "pendingTDSReport";
	String summaryJasperpath = "summaryTDSReport";
	private Date asOnDate = new Date();
	private Date fromDate;
	private InputStream inputStream;
	private ReportService reportService;
	private String partyName="";
	private Integer detailKey;
	private boolean showRemittedEntries = false;
	private List<RemittanceBean> pendingTDS = new ArrayList<RemittanceBean>();
	private List<TDSEntry> remittedTDS = new ArrayList<TDSEntry>();
	private Recovery recovery = new Recovery();
	private Fund fund = new Fund();
	private Department department = new Department();
	private EgovCommon egovCommon;
	private List<EntityType> entitiesList = new ArrayList<EntityType>();
	private RemitRecoveryService remitRecoveryService;
	private FinancialYearHibernateDAO financialYearDAO;	
	private String message = "";
	private static Logger LOGGER=Logger.getLogger(PendingTDSReportAction.class);
	
	public void setFinancialYearDAO(FinancialYearHibernateDAO financialYearDAO) {
		this.financialYearDAO = financialYearDAO;
	}

	public void setRemitRecoveryService(RemitRecoveryService remitRecoveryService) {
		this.remitRecoveryService = remitRecoveryService;
	}

	@Override
	public String execute() throws Exception {
		return "reportForm";
	}
	
@Action(value="/report/pendingTDSReport-summaryReport")
	public String summaryReport() throws Exception {
		return "summaryForm";
	}
	
	@Override
	public void prepare() {
	HibernateUtil.getCurrentSession().setDefaultReadOnly(true);
	HibernateUtil.getCurrentSession().setFlushMode(FlushMode.MANUAL);
		super.prepare();
		addDropdownData("departmentList", persistenceService.findAllBy("from Department order by deptName"));
		addDropdownData("fundList", persistenceService.findAllBy(" from Fund where isactive=1 and isnotleaf=0 order by name"));
		addDropdownData("recoveryList", persistenceService.findAllBy(" from Recovery where isactive=1 order by chartofaccounts.glcode"));
	}
	
@Action(value="/report/pendingTDSReport-ajaxLoadData")
	public String ajaxLoadData(){
		populateData();
		return "results";
	}
	
@Action(value="/report/pendingTDSReport-ajaxLoadSummaryData")
	public String ajaxLoadSummaryData(){
		populateSummaryData();
		return "summaryResults";
	}

	public void setAsOnDate(Date startDate) {
		this.asOnDate = startDate;
	}

	public Date getAsOnDate() {
		return asOnDate;
	}
	
	public String getFormattedDate(Date date){
		return Constants.DDMMYYYYFORMAT2.format(date);
	}

@Action(value="/report/pendingTDSReport-exportPdf")
	public String exportPdf() throws JRException, IOException{
		generateReport();
	    return "PDF";
	}
@Action(value="/report/pendingTDSReport-exportSummaryPdf")
	public String exportSummaryPdf() throws JRException, IOException{
		generateSummaryReport();
	    return "summary-PDF";
	}

	private void generateReport() {
		populateData();
		ReportRequest reportInput = new ReportRequest(jasperpath, pendingTDS, getParamMap());
		ReportOutput reportOutput = reportService.createReport(reportInput);
		inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
	}
	
	private void generateSummaryReport() {
		populateSummaryData();
		ReportRequest reportInput = new ReportRequest(summaryJasperpath, remittedTDS, getParamMap());
		ReportOutput reportOutput = reportService.createReport(reportInput);
		inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
	}
	
	Map<String, Object> getParamMap() {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("remittedTDSJasper", this.getClass().getResourceAsStream("/reports/templates/remittedTDSReport.jasper"));
		if(showRemittedEntries)
			paramMap.put("remittedTDS", remittedTDS);
		else 
			paramMap.put("remittedTDS", null);
		String formatedAsOndate = Constants.DDMMYYYYFORMAT2.format(asOnDate);
		paramMap.put("asOnDate", formatedAsOndate);
		if(fromDate!=null)
		{  
			String formatedFromDate = Constants.DDMMYYYYFORMAT2.format(fromDate);
		paramMap.put("fromDate", formatedFromDate);
		paramMap.put("heading", "Pending TDS report From "+formatedFromDate+ "  to "+formatedAsOndate);
		paramMap.put("fromDateText", "From Date :      "+formatedFromDate);
		}
		else
		{
			paramMap.put("heading", "Pending TDS report as on "+formatedAsOndate);
		}
		fund = (Fund) persistenceService.find("from Fund where id=?",fund.getId());
		paramMap.put("fundName", fund.getName());
		paramMap.put("partyName", partyName);
		if(department.getId()!=null && department.getId()!=-1){
			department = (Department) persistenceService.find("from Department where id=?",department.getId());
			paramMap.put("departmentName", department.getName());
		}
		paramMap.put("recoveryName", recovery.getRecoveryName());
		return paramMap;
	}


	private void populateData() {
		validateFinYear();
		if(getFieldErrors().size()>0)
			return;
		recovery = (Recovery) persistenceService.find("from Recovery where id=?",recovery.getId());
		String deptQuery="";
		String partyNameQuery="";
		RemittanceBean remittanceBean = new RemittanceBean();
		remittanceBean.setRecoveryId(recovery.getId());
		if(fromDate!=null)
		{
			remittanceBean.setFromDate(Constants.DDMMYYYYFORMAT1.format(fromDate));
		}
		pendingTDS = remitRecoveryService.getRecoveryDetailsForReport(remittanceBean, getVoucherHeader(),detailKey);
		if(showRemittedEntries){
			if(department.getId()!=null && department.getId()!=-1){
				deptQuery = " and egRemittanceGldtl.generalledgerdetail.generalledger.voucherHeaderId.vouchermis.departmentid.id="+department.getId();
			}
			if(detailKey!=null && detailKey!=-1){
				partyNameQuery = " and egRemittanceGldtl.generalledgerdetail.detailkeyid="+detailKey;
			}
			StringBuffer query=new StringBuffer(1000); 
			List<EgRemittanceDetail> result =new ArrayList<EgRemittanceDetail>(); 
					query.append("from EgRemittanceDetail where  egRemittanceGldtl.generalledgerdetail.generalledger.glcodeId.id=? "+ 
					"and egRemittance.fund.id=? and egRemittance.voucherheader.status=0 and egRemittanceGldtl.generalledgerdetail.generalledger.voucherHeaderId.status=0 and "+ 
					"egRemittanceGldtl.generalledgerdetail.generalledger.voucherHeaderId.voucherDate <= ? ");
					if(fromDate!=null )
					{
						query.append(" and egRemittanceGldtl.generalledgerdetail.generalledger.voucherHeaderId.voucherDate >= ?"); 
					}
					query.append(deptQuery).append(partyNameQuery);
					query.append(" order by egRemittanceGldtl.generalledgerdetail.generalledger.voucherHeaderId.voucherNumber ");
					if(fromDate!=null )
					{
						result=persistenceService.findAllBy(query.toString(),recovery.getChartofaccounts().getId(),fund.getId(),asOnDate,fromDate);
					}
					else
					{
						result=persistenceService.findAllBy(query.toString(),recovery.getChartofaccounts().getId(),fund.getId(),asOnDate);	
					}
			Boolean createPartialRow=false;
			for (EgRemittanceDetail entry : result) {
				createPartialRow=false;
				for(TDSEntry tdsExists:remittedTDS)
				{
					if(	tdsExists.getEgRemittanceGlDtlId().intValue()==entry.getEgRemittanceGldtl().getId().intValue())
					{
						createPartialRow=true;     
					}
				}
				TDSEntry tds=new TDSEntry();
				tds.setEgRemittanceGlDtlId(entry.getEgRemittanceGldtl().getId());
				if(!createPartialRow)
				{
					tds= createTds(entry);	
				}
				tds.setRemittedOn(Constants.DDMMYYYYFORMAT2.format(entry.getEgRemittance().getVoucherheader().getVoucherDate()));
				tds.setAmount(entry.getRemittedamt());
				if(entry.getEgRemittance().getVoucherheader()!=null){
					tds.setPaymentVoucherNumber(entry.getEgRemittance().getVoucherheader().getVoucherNumber());
				}
				List<InstrumentVoucher> ivList=persistenceService.findAllBy("from InstrumentVoucher where" +
						" instrumentHeaderId.statusId.description in(?,?,?) and voucherHeaderId=?"
						,FinancialConstants.INSTRUMENT_DEPOSITED_STATUS,FinancialConstants.INSTRUMENT_CREATED_STATUS,
						FinancialConstants.INSTRUMENT_RECONCILED_STATUS,entry.getEgRemittance().getVoucherheader());
				boolean isMultiple=false;
				for(InstrumentVoucher iv :ivList)
				{
					if(entry.getRemittedamt().compareTo(iv.getInstrumentHeaderId().getInstrumentAmount())!=0)   
					{
						isMultiple = true; 
					}
					
					tds.setChequeNumber(iv.getInstrumentHeaderId().getInstrumentNumber());
					if(isMultiple)
					{
						tds.setChequeNumber(tds.getChequeNumber()+"-MULTIPLE");
					}
					tds.setChequeAmount(iv.getInstrumentHeaderId().getInstrumentAmount());
					if(iv.getInstrumentHeaderId().getInstrumentDate()!=null)
						tds.setDrawnOn(Constants.DDMMYYYYFORMAT2.format(iv.getInstrumentHeaderId().getInstrumentDate()));
				}
				remittedTDS.add(tds);
			}
		}
	}
/**
 * show only pending TDSes
 */
	private void populateSummaryData() {
		recovery = (Recovery) persistenceService.find("from Recovery where id=?",recovery.getId());
		String deptQuery="";
		String partyNameQuery="";
		if(department.getId()!=null && department.getId()!=-1){
			deptQuery = " and mis.departmentid="+department.getId();
		}
		if(detailKey!=null && detailKey!=-1){
			partyNameQuery = " and gld.detailkeyid="+detailKey;
		}
		List<Object[]> result = new ArrayList<Object[]>();
		List<Object[]> resultTolDeduction=new ArrayList<Object[]>();
		try {
			String qry="select vh.name,sum(erd.remittedamt),er.month from eg_remittance_detail erd," +
					" voucherheader vh1 right outer join eg_remittance er on vh1.id=er.paymentvhid,voucherheader vh,vouchermis mis,generalledger gl,generalledgerdetail gld,fund f,eg_remittance_gldtl ergl where " +
					" erd.remittancegldtlid= ergl.id and erd.remittanceid=er.id and gl.glcodeid="+recovery.getChartofaccounts().getId()+" and vh.id=mis.voucherheaderid and " +
					"  vh1.status=0 and ergl.gldtlid=gld.id and gl.id=gld.generalledgerid and gl.voucherheaderid=vh.id and er.fundid=f.id and f.id="+fund.getId()+
					" and vh.status=0 and vh.voucherDate <= to_date('"+ Constants.DDMMYYYYFORMAT2.format(asOnDate)+"','dd/MM/yyyy') and "+"vh.voucherDate >= to_date('"+Constants.DDMMYYYYFORMAT2.format(financialYearDAO.getFinancialYearByDate(asOnDate).getStartingDate())+"','dd/MM/yyyy') "+deptQuery+partyNameQuery+" group by er.month,vh.name order by er.month,vh.name";
			if(LOGGER.isDebugEnabled())     LOGGER.debug(qry);
			result = HibernateUtil.getCurrentSession().createSQLQuery(qry).list();
			// Query to get total deduction
			String qryTolDeduction="SELECT type,MONTH,SUM(gldtamt) FROM (SELECT DISTINCT er.month AS MONTH,ergl.gldtlamt           AS gldtamt,"+
						"ergl.gldtlid,vh.name AS type FROM eg_remittance_detail erd,voucherheader vh1 RIGHT OUTER JOIN eg_remittance er ON vh1.id=er.paymentvhid,"+
						"voucherheader vh,vouchermis mis,generalledger gl,generalledgerdetail gld,fund f, eg_remittance_gldtl ergl WHERE erd.remittancegldtlid= ergl.id"+
						" AND erd.remittanceid=er.id  AND gl.glcodeid ="+recovery.getChartofaccounts().getId() +" AND vh.id =mis.voucherheaderid AND vh1.status =0 "+
						" AND ergl.gldtlid =gld.id  AND gl.id = gld.generalledgerid  AND gl.voucherheaderid     =vh.id  AND er.fundid =f.id"+
						" AND f.id ="+fund.getId() + " AND vh.status =0 AND vh.voucherDate <= to_date('"+ Constants.DDMMYYYYFORMAT2.format(asOnDate)+"','dd/MM/yyyy') and "+" vh.voucherDate >= to_date('"+Constants.DDMMYYYYFORMAT2.format(financialYearDAO.getFinancialYearByDate(asOnDate).getStartingDate())+"','dd/MM/yyyy') "+deptQuery+partyNameQuery+")group by type,month";
			resultTolDeduction = HibernateUtil.getCurrentSession().createSQLQuery(qryTolDeduction).list();
		}catch(EGOVRuntimeException e) {
			message = e.getMessage();
			return;
		}catch (Exception e) {
			message = e.getMessage();
			return;
		}
		for (Object[] entry : result) {
			for (Object[] dedentry : resultTolDeduction) {
			TDSEntry tds = new TDSEntry();
			String monthChk=DateUtils.getAllMonthsWithFullNames().get(Integer.valueOf(entry[2].toString())+1);
			if(monthChk.equalsIgnoreCase(DateUtils.getAllMonthsWithFullNames().get(Integer.valueOf(dedentry[1].toString())+1)) && (dedentry[0].toString().equalsIgnoreCase(entry[0].toString())) ){
			tds.setNatureOfDeduction(entry[0].toString());
			tds.setTotalRemitted(new BigDecimal(entry[1].toString()));
			tds.setMonth(DateUtils.getAllMonthsWithFullNames().get(Integer.valueOf(entry[2].toString())+1));
			BigDecimal totDeduction= new BigDecimal (dedentry[2].toString()); 
			tds.setTotalDeduction(totDeduction);
			remittedTDS.add(tds);
			}	
		}
		}
	}

	private CVoucherHeader getVoucherHeader() {
		CVoucherHeader voucherHeader = new CVoucherHeader();
		voucherHeader.setFundId(fund);
		Vouchermis   vouchermis=new Vouchermis();
		voucherHeader.setVouchermis(vouchermis);
		voucherHeader.getVouchermis().setDepartmentid(department);
		voucherHeader.setVoucherDate(asOnDate);
		return voucherHeader;
	}
	
@Action(value="/report/pendingTDSReport-ajaxLoadEntites")
	public String ajaxLoadEntites() throws ClassNotFoundException{
		if(parameters.containsKey("recoveryId") && parameters.get("recoveryId")[0] !=null && !"".equals(parameters.get("recoveryId")[0])){
			recovery = (Recovery) persistenceService.find("from Recovery where id=?",Long.valueOf(parameters.get("recoveryId")[0]));
			for (CChartOfAccountDetail detail : recovery.getChartofaccounts().getChartOfAccountDetails()) {
				entitiesList.addAll(egovCommon.loadEntitesFor(detail.getDetailTypeId()));
			}
		}
		return "entities";
	}

	private TDSEntry createTds(EgRemittanceDetail entry) {
		TDSEntry tds = new TDSEntry();
		if(entry.getEgRemittanceGldtl().getRecovery()!=null){
			tds.setPartyCode(entry.getEgRemittanceGldtl().getRecovery().getEgPartytype().getCode());
		}
		tds.setEgRemittanceGlDtlId(entry.getEgRemittanceGldtl().getId());
		tds.setNatureOfDeduction(entry.getEgRemittanceGldtl().getGeneralledgerdetail().getGeneralledger().getVoucherHeaderId().getName());
		tds.setVoucherNumber(entry.getEgRemittanceGldtl().getGeneralledgerdetail().getGeneralledger().getVoucherHeaderId().getVoucherNumber());
		tds.setVoucherDate(Constants.DDMMYYYYFORMAT2.format(entry.getEgRemittanceGldtl().getGeneralledgerdetail().getGeneralledger().getVoucherHeaderId().getVoucherDate()));
		EntityType entityType = getEntity(entry);
		if(entityType!=null){
			tds.setPartyName(entityType.getName());
			tds.setPartyCode(entityType.getCode());
			tds.setPanNo(entityType.getPanno());
		}
		tds.setAmount(entry.getEgRemittanceGldtl().getGldtlamt());
		return tds;
	}

	private EntityType getEntity(EgRemittanceDetail entry) {
		EgovCommon common = new EgovCommon();
		common.setPersistenceService(persistenceService);
		Integer detailKeyId = entry.getEgRemittanceGldtl().getGeneralledgerdetail().getDetailkeyid().intValue();
		EntityType entityType = null;
		try {
			entityType = common.getEntityType(entry.getEgRemittanceGldtl().getGeneralledgerdetail().getAccountdetailtype(),detailKeyId);
		} catch (EGOVException e) {
			e.printStackTrace();
		}
		return entityType;
	}
	
@Action(value="/report/pendingTDSReport-exportXls")
	public String exportXls() throws JRException, IOException{
		populateData();
		ReportRequest reportInput = new ReportRequest(jasperpath, pendingTDS, getParamMap());
		reportInput.setReportFormat(FileFormat.XLS);
		ReportOutput reportOutput = reportService.createReport(reportInput);
		inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
	    return "XLS";
	}
	
@Action(value="/report/pendingTDSReport-exportSummaryXls")
	public String exportSummaryXls() throws JRException, IOException{
		populateSummaryData();
		ReportRequest reportInput = new ReportRequest(summaryJasperpath, remittedTDS, getParamMap());
		reportInput.setReportFormat(FileFormat.XLS);
		ReportOutput reportOutput = reportService.createReport(reportInput);
		inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
	    return "summary-XLS";
	}
	public void validateFinYear()
	{
		if(fromDate!=null)
		{
			Constants.DDMMYYYYFORMAT2.format(fromDate);
			if(financialYearDAO.isSameFinancialYear(fromDate,asOnDate))
			{
				return;
			}else
			{
				addFieldError("fromDate", "Dates are not within same Financial Year");
			}
		}
		
	}
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public Object getModel() {
		return null;
	}

	public void setEgovCommon(EgovCommon egovCommon) {
		this.egovCommon = egovCommon;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setShowRemittedEntries(boolean showRemittedEntries) {
		this.showRemittedEntries = showRemittedEntries;
	}
	
	public boolean getShowRemittedEntries() {
		return showRemittedEntries;
	}

	public boolean isShowRemittedEntries() {
		return showRemittedEntries;
	}

	public void setPendingTDS(List<RemittanceBean> pendingTDS) {
		this.pendingTDS = pendingTDS;
	}

	public List<RemittanceBean> getPendingTDS() {
		return pendingTDS;
	}

	public void setRemittedTDS(List<TDSEntry> remittedTDS) {
		this.remittedTDS = remittedTDS;
	}

	public List<TDSEntry> getRemittedTDS() {
		return remittedTDS;
	}

	public void setRecovery(Recovery recovery) {
		this.recovery = recovery;
	}

	public Recovery getRecovery() {
		return recovery;
	}

	public void setFund(Fund fund) {
		this.fund = fund;
	}

	public Fund getFund() {
		return fund;
	}
	
	public List<EntityType> getEntitiesList() {
		return entitiesList;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDetailKey(Integer detailKey) {
		this.detailKey = detailKey;
	}

	public Integer getDetailKey() {
		return detailKey;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}


	
}
