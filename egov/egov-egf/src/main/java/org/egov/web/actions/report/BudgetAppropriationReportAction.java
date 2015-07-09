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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetReAppropriation;
import org.egov.services.budget.BudgetAppropriationService;
import org.egov.utils.Constants;
import org.egov.utils.ReportHelper;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Transactional;
@Results(value={
		@Result(name="PDF",type="stream",location=Constants.INPUT_STREAM, params={Constants.INPUT_NAME,Constants.INPUT_STREAM,Constants.CONTENT_TYPE,"application/pdf",Constants.CONTENT_DISPOSITION,"no-cache;filename=BudgetAppropriationReport.pdf"}),
		@Result(name="XLS",type="stream",location=Constants.INPUT_STREAM, params={Constants.INPUT_NAME,Constants.INPUT_STREAM,Constants.CONTENT_TYPE,"application/xls",Constants.CONTENT_DISPOSITION,"no-cache;filename=BudgetAppropriationReport.xls"})
	})
@ParentPackage("egov")   
@Transactional(readOnly=true)
public class BudgetAppropriationReportAction  extends BaseFormAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger	LOGGER	= Logger.getLogger(BudgetAppropriationReportAction.class);
	
	private Date fromDate = null;
	private Date toDate = null;
	private InputStream inputStream;
	private ReportHelper reportHelper;
	private List<Budget> budgetList=null;
	private BudgetReAppropriation budgetRep=new BudgetReAppropriation();
	private List<BudgetReAppReportBean> budgetAppropriationList=new ArrayList<BudgetReAppReportBean>();
	private List<BudgetReAppReportBean> budgetDisplayList=new ArrayList<BudgetReAppReportBean>();
	private StringBuffer heading=new StringBuffer();
	private String budgetName;
	private String deptName="";
	private String fundName="";
	private String functionName="";
	private String isFundSelected="false";
	private String isFunctionSelected="false";
	private String isDepartmentSelected="false";
	private BudgetAppropriationService budgetAppropriationService;
	
	
	
	public void prepare(){
	HibernateUtil.getCurrentSession().setDefaultReadOnly(true);
	HibernateUtil.getCurrentSession().setFlushMode(FlushMode.MANUAL);
		super.prepare();
		if(!parameters.containsKey("showDropDown")){
			EgovMasterDataCaching masterCache = EgovMasterDataCaching.getInstance();
			addDropdownData("departmentList", masterCache.get("egi-department"));
			addDropdownData("functionList", masterCache.get("egi-function"));
			addDropdownData("fundDropDownList", masterCache.get("egi-fund"));
			budgetList = persistenceService.findAllBy("from Budget bud where bud.isActiveBudget=1  and bud.parent is null  order by bud.financialYear.id  desc");
			addDropdownData("budList",budgetList);
			      
		}
	}
	@Override
	public Object getModel() {
		return budgetRep;
	}
		
	public BudgetAppropriationReportAction(){
	}
	
@Action(value="/report/budgetAppropriationReport-newForm")
	public String newForm(){
		return NEW;
	}  
	
@Action(value="/report/budgetAppropriationReport-ajaxGenerateReport")
	public String ajaxGenerateReport(){
		if(LOGGER.isInfoEnabled())     LOGGER.info("Starting ajaxGenerateReport..");
		populateReAppropriationData();
		return "result";
	} 
	private void prepareFormattedList() {
		for(int index=0,slNo=1;index<budgetDisplayList.size();index++){
			budgetAppropriationList.add(budgetDisplayList.get(index));
			budgetAppropriationList.get(index).setSlNo(slNo++);
			budgetAppropriationList.get(index).setAppDate(Constants.DDMMYYYYFORMAT2.format(budgetDisplayList.get(index).getAppropriationDate()));
		}
	}

	@SuppressWarnings("unchecked")
	private void populateReAppropriationData() {
		setRelatedEntitesOn();
		Query query = generateQuery();
		query.setResultTransformer(Transformers.aliasToBean(BudgetReAppReportBean.class));
		budgetDisplayList.addAll(query.list());	
	}
	
	private StringBuffer getQueryString() {
		StringBuffer queryString = new StringBuffer();
		String deptQry="";
		String fundQry="";
		String functionQry="";
		
		if(budgetRep.getBudgetDetail().getExecutingDepartment()!=null && budgetRep.getBudgetDetail().getExecutingDepartment().getId()!=null){
			deptQry= " and bd.EXECUTING_DEPARTMENT="+budgetRep.getBudgetDetail().getExecutingDepartment().getId();
		}
		if(budgetRep.getBudgetDetail().getFund()!=null && budgetRep.getBudgetDetail().getFund().getId()!=null){
			fundQry= "  and bd.fund="+budgetRep.getBudgetDetail().getFund().getId();
		}
		if(budgetRep.getBudgetDetail().getFunction()!=null && budgetRep.getBudgetDetail().getFunction().getId()!=null){
			functionQry= "  and bd.function="+budgetRep.getBudgetDetail().getFunction().getId();
		}
		
		queryString = queryString
				.append("select dept.dept_name as department,funct.name as function ,fnd.name as fund ,"
					+" bg.name  as budgetHead,bmisc.sequence_number as budgetAppropriationNo,bmisc.reappropriation_date as appropriationDate,"
					+" bd.approvedamount as actualAmount,br.addition_amount as additionAmount,br.deduction_amount as deductionAmount"
					+" from egf_budget B,egf_budget_reappropriation br,egf_budgetdetail bd,egf_budgetgroup bg,egf_reappropriation_misc bmisc"
					+", eg_department dept,fund fnd , function funct"
					+"  where  bd.id =br.budgetdetail and bd.budgetgroup=bg.id and br.REAPPROPRIATION_MISC=bmisc.id and bd.budget=b.id "
					+" and funct.id=bd.function and fnd.id=bd.fund and dept.id_DEPT= bd.EXECUTING_DEPARTMENT "
					+deptQry+fundQry+functionQry+" and bmisc.reappropriation_date between '"+getFormattedDate(getFromDate()) +"' and '"+getFormattedDate(getToDate())+"'"
					+"  and bd.MATERIALIZEDPATH like ''||(select budinn.MATERIALIZEDPATH ||'%' from egf_budget budinn where budinn.id="+budgetRep.getBudgetDetail().getBudget().getId()+")||''");
		return queryString.append("  order by fnd.id,dept.id_dept,funct.id,bmisc.reappropriation_date");
		 
		}                  


	private Query generateQuery() {
		Query query = HibernateUtil.getCurrentSession().createSQLQuery(
				getQueryString().toString()).addScalar("department").addScalar("function").addScalar(
				"fund").addScalar("budgetHead").addScalar("budgetAppropriationNo").addScalar("appropriationDate").addScalar("actualAmount")
				.addScalar("additionAmount").addScalar("deductionAmount");
		return query;
	}
	
	protected void setRelatedEntitesOn() {
		heading.append("Budget Addition/Deduction Appropriation ");
		if(!getFundName().equals("")){
			heading.append(" in "+getFundName()); isFundSelected="true";
		}if(!getFunctionName().equals("")){
			heading.append(" under "+getFunctionName()); isFunctionSelected="true";		
		}if(!getDeptName().equals("")){
			heading.append(" For "+getDeptName() +"Department"); isDepartmentSelected="true";
		}if(getFromDate()!=null && getToDate()!=null){
			heading.append(" From "+getFormattedDate(getFromDate()) +" To "+getFormattedDate(getToDate()));
		}    
	}
		
	/*
	 * For Pdf/Excel 
	 */
	@SuppressWarnings("unchecked")
	public String getUlbName() {
		Query query = HibernateUtil.getCurrentSession().createSQLQuery("select name from companydetail");
		List<String> result = query.list();
		if (result != null)
			return result.get(0);
		return "";
	}
@Action(value="/report/budgetAppropriationReport-ajaxGenerateReportXls")
	public String ajaxGenerateReportXls() throws Exception{
		populateReAppropriationData();
		prepareFormattedList();
		String title=getUlbName()+"\\n"+heading.toString();
		String subtitle="Amount in Rupess";
		JasperPrint jasper = reportHelper.generateBudgetAppropriationJasperPrint(budgetAppropriationList,title,subtitle,budgetName,getIsFundSelected(),getIsFunctionSelected(),getIsDepartmentSelected());
		inputStream = reportHelper.exportXls(inputStream, jasper);
		return "XLS";
	}            
@Action(value="/report/budgetAppropriationReport-ajaxGenerateReportPdf")
	public String ajaxGenerateReportPdf() throws Exception{
		populateReAppropriationData();
		prepareFormattedList();            
		String title=getUlbName()+"\\n"+heading.toString();
		String subtitle="Amount in Rupess";
		JasperPrint jasper = reportHelper.generateBudgetAppropriationJasperPrint(budgetAppropriationList,title,subtitle,budgetName,getIsFundSelected(),getIsFunctionSelected(),getIsDepartmentSelected());
		inputStream = reportHelper.exportPdf(inputStream, jasper);
		return "PDF";
	}
	
	public String getFormattedDate(Date date) {
		return Constants.DDMMYYYYFORMAT2.format(date);
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
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public ReportHelper getReportHelper() {
		return reportHelper;
	}
	public void setReportHelper(ReportHelper reportHelper) {
		this.reportHelper = reportHelper;
	}
	public StringBuffer getHeading() {
		return heading;
	}
	public BudgetReAppropriation getBudgetRep() {
		return budgetRep;
	}

	public void setBudgetRep(BudgetReAppropriation budgetRep) {
		this.budgetRep = budgetRep;
	}

	public void setHeading(StringBuffer heading) {
		this.heading = heading;
	}

	public List<BudgetReAppReportBean> getBudgetDisplayList() {
		return budgetDisplayList;
	}

	public void setBudgetDisplayList(
			List<BudgetReAppReportBean> budgetDisplayList) {
		this.budgetDisplayList = budgetDisplayList;
	}

	public List<BudgetReAppReportBean> getBudgetAppropriationList() {
		return budgetAppropriationList;
	}

	public void setBudgetAppropriationList(
			List<BudgetReAppReportBean> budgetAppropriationList) {
		this.budgetAppropriationList = budgetAppropriationList;
	}

	public String getIsFundSelected() {
		return isFundSelected;
	}

	public void setIsFundSelected(String isFundSelected) {
		this.isFundSelected = isFundSelected;
	}

	public String getIsFunctionSelected() {
		return isFunctionSelected;
	}

	public void setIsFunctionSelected(String isFunctionSelected) {
		this.isFunctionSelected = isFunctionSelected;
	}

	public String getIsDepartmentSelected() {
		return isDepartmentSelected;
	}

	public void setIsDepartmentSelected(String isDepartmentSelected) {
		this.isDepartmentSelected = isDepartmentSelected;
	}

	public String getBudgetName() {
		return budgetName;
	}

	public void setBudgetName(String budgetName) {
		this.budgetName = budgetName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getFundName() {
		return fundName;
	}

	public void setFundName(String fundName) {
		this.fundName = fundName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
}
