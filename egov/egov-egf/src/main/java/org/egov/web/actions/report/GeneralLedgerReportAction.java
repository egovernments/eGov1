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

import org.apache.struts2.convention.annotation.Action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.utils.FinancialConstants;
import org.egov.web.actions.voucher.VoucherSearchAction;
import org.hibernate.FlushMode;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.GLEngine.GeneralLedgerBean;
import com.exilant.eGov.src.reports.GeneralLedgerReport;
import com.exilant.eGov.src.reports.GeneralLedgerReportBean;
import com.exilant.exility.common.TaskFailedException;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Transactional(readOnly=true)
@ParentPackage("egov")   
public class GeneralLedgerReportAction extends BaseFormAction {

	private static final Logger	LOGGER = Logger.getLogger(GeneralLedgerReportAction.class);
	private GeneralLedgerReportBean generalLedgerReport = new GeneralLedgerReportBean() ;
	private GeneralLedgerReport generalLedger = new GeneralLedgerReport();
	protected DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	protected LinkedList  generalLedgerDisplayList = new LinkedList();
	String heading = "";

	public GeneralLedgerReportAction() {
		LOGGER.error("creating instance of GeneralLedgerReportAction ");
	}
	
	@Override
	public Object getModel() {
		return generalLedgerReport;
	}
	public void prepareNewForm() {
		super.prepare();
		addDropdownData("fundList", persistenceService.findAllBy(" from Fund where isactive=1 and isnotleaf=0 order by name"));
		addDropdownData("departmentList", persistenceService.findAllBy("from Department order by deptName"));
		addDropdownData("functionaryList", persistenceService.findAllBy(" from Functionary where isactive=1 order by name"));
		addDropdownData("fundsourceList", persistenceService.findAllBy(" from Fundsource where isactive=1 and isnotleaf=0 order by name"));
		addDropdownData("fieldList", persistenceService.findAllBy(" from Boundary b where lower(b.boundaryType.name)='ward' "));
		if(LOGGER.isDebugEnabled())     LOGGER.debug("Inside  Prepare ........");
		
	}

	@SkipValidation
	@Action(value="/report/generalLedgerReport-newForm")
	public String newForm() {
		if(LOGGER.isDebugEnabled())     LOGGER.debug("..Inside NewForm method..");
		return FinancialConstants.STRUTS_RESULT_PAGE_SEARCH;
	}
	
	
	@Validations(requiredFields = { @RequiredFieldValidator(fieldName = "glCode1", message = "", key = FinancialConstants.REQUIRED),
			@RequiredFieldValidator(fieldName = "fund_id", message = "", key = FinancialConstants.REQUIRED),
			@RequiredFieldValidator(fieldName = "startDate", message = "", key = FinancialConstants.REQUIRED),
			@RequiredFieldValidator(fieldName = "endDate", message = "", key = FinancialConstants.REQUIRED)})
	
	@ValidationErrorPage(value=FinancialConstants.STRUTS_RESULT_PAGE_SEARCH)
	
	
	
	@SkipValidation
@Action(value="/report/generalLedgerReport-ajaxSearch")
	public String ajaxSearch() throws TaskFailedException{
		
	HibernateUtil.getCurrentSession().setDefaultReadOnly(true);
	HibernateUtil.getCurrentSession().setFlushMode(FlushMode.MANUAL);
		if(LOGGER.isDebugEnabled())     LOGGER.debug("GeneralLedgerAction | Search | start");
		try {
			generalLedgerDisplayList = generalLedger.getGeneralLedgerList(generalLedgerReport);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(LOGGER.isDebugEnabled())     LOGGER.debug("GeneralLedgerAction | list | End");
		heading=getGLHeading();
		generalLedgerReport.setHeading(getGLHeading());
		prepareNewForm();
		return "results";
	}
	private String getGLHeading() {
		
		String heading="";
		CChartOfAccounts glCode = new CChartOfAccounts();
		Fund fund  = new Fund();
		if(checkNullandEmpty(generalLedgerReport.getGlCode1()) && checkNullandEmpty(generalLedgerReport.getGlCode1())){
			glCode = (CChartOfAccounts) persistenceService.find("from CChartOfAccounts where glcode = ?",generalLedgerReport.getGlCode1());
			fund = (Fund) persistenceService.find("from Fund where id = ?",Integer.parseInt(generalLedgerReport.getFund_id()));
		}
		heading = "General Ledger Report for "+glCode.getGlcode() +":"+ glCode.getName() +" for "+ fund.getName()+" from "+generalLedgerReport.getStartDate()+" to "+generalLedgerReport.getEndDate();
		if(checkNullandEmpty(generalLedgerReport.getDepartmentId()))
		{
			Department dept = (Department) persistenceService.find("from Department where id = ?",Integer.parseInt(generalLedgerReport.getDepartmentId()));
			heading = heading +" under "+dept.getName()+" ";
		}
		if(checkNullandEmpty(generalLedgerReport.getFunctionCode()))
		{
			CFunction function  = (CFunction) persistenceService.find("from CFunction where code = ?",generalLedgerReport.getFunctionCode());
			heading = heading +" in "+function.getName()+" Function ";
		}
		
		if(checkNullandEmpty(generalLedgerReport.getFunctionaryId()))
		{  	
			Functionary functionary  = (Functionary) persistenceService.find("from Functionary where id = ?",Integer.parseInt(generalLedgerReport.getFunctionaryId()));
			heading = heading +" in "+functionary.getName()+" Functionary ";
		}
		
		if(checkNullandEmpty(generalLedgerReport.getFieldId()))
		{
			Boundary ward  = (Boundary) persistenceService.find("from Boundary where id = ?",Integer.parseInt(generalLedgerReport.getFieldId()));
			heading = heading +" in "+ward.getName()+" Field ";		
		}
		return heading;
	}
	private boolean checkNullandEmpty(String column)
	{
		if(column!=null && !column.isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}


	public GeneralLedgerReportBean getGeneralLedgerReport() {
		return generalLedgerReport;
	}

	public void setGeneralLedgerReport(GeneralLedgerReportBean generalLedgerReport) {
		this.generalLedgerReport = generalLedgerReport;
	}

	public GeneralLedgerReport getGeneralLedger() {
		return generalLedger;
	}

	public void setGeneralLedger(GeneralLedgerReport generalLedger) {
		this.generalLedger = generalLedger;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public LinkedList getGeneralLedgerDisplayList() {
		return generalLedgerDisplayList;
	}

	public void setGeneralLedgerDisplayList(LinkedList generalLedgerDisplayList) {
		this.generalLedgerDisplayList = generalLedgerDisplayList;
	}



}
