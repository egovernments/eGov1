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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CFinancialYear;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.infra.admin.master.entity.CityWebsite;
import org.egov.infra.admin.master.service.CityWebsiteService;
import org.egov.infstr.config.dao.AppConfigValuesDAO;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.services.report.FunctionwiseIEService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.utils.ReportHelper;
import org.hibernate.FlushMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;

@Results(value={
		@Result(name="functionwiseIE-PDF",type="stream",location=Constants.INPUT_STREAM, params={Constants.INPUT_NAME,Constants.INPUT_STREAM,Constants.CONTENT_TYPE,"application/pdf","contentDisposition","no-cache;filename=FunctionwiseIE.pdf"}),
		@Result(name="functionwiseIE-XLS",type="stream",location=Constants.INPUT_STREAM, params={Constants.INPUT_NAME,Constants.INPUT_STREAM,Constants.CONTENT_TYPE,"application/xls","contentDisposition","no-cache;filename=FunctionwiseIE.xls"}),
		@Result(name="functionwiseIE-HTML",type="stream",location=Constants.INPUT_STREAM, params={Constants.INPUT_NAME,Constants.INPUT_STREAM,Constants.CONTENT_TYPE,"text/html"})
	})
@Transactional(readOnly=true)
@ParentPackage("egov")
public class FunctionwiseIEAction extends ReportAction
{
	private static final long serialVersionUID = 1L;
	protected InputStream inputStream;
	private ReportHelper reportHelper;
	private FunctionwiseIEService functionwiseIEService;
	private final FunctionwiseIE functionwiseIE = new FunctionwiseIE();
	private CityWebsiteService cityWebsiteDAO;
	private CityWebsite cityWebsite;
	private @Autowired AppConfigValuesDAO appConfigValuesDAO;	
	private FinancialYearDAO financialYearDAO;
	private String heading="";
	private Date todayDate;
	public FinancialYearDAO getFinancialYearDAO() {
		return financialYearDAO;
	}

	public void setFinancialYearDAO(FinancialYearDAO financialYearDAO) {
		this.financialYearDAO = financialYearDAO;
	}

	private EgovMasterDataCaching masterCache = EgovMasterDataCaching.getInstance();
	private static final Logger LOGGER = Logger.getLogger(FunctionwiseIEAction.class);
	private List<CommonReportBean> ieWithBudgetList;
	public void setCityWebsiteService(final CityWebsiteService cityWebsiteDAO) {
		this.cityWebsiteDAO = cityWebsiteDAO;
	}
	
	public void setFunctionwiseIEService(final FunctionwiseIEService functionwiseIEService) {
		this.functionwiseIEService = functionwiseIEService;
	}
	public void prepare()
	{
	HibernateUtil.getCurrentSession().setDefaultReadOnly(true);
	HibernateUtil.getCurrentSession().setFlushMode(FlushMode.MANUAL);
		super.prepare();
		if(reportSearch.getStartDate()==null || reportSearch.getStartDate().equals(""))
			reportSearch.setStartDate(sdf.format(((CFinancialYear)persistenceService.find(" from CFinancialYear where startingDate <= '"+formatter.format(new Date())+"' and endingDate >= '"+formatter.format(new Date())+"'")).getStartingDate()));
		if(reportSearch.getEndDate()==null || reportSearch.getEndDate().equals(""))
			reportSearch.setEndDate(sdf.format(new Date()));
		 setTodayDate(new Date());
	}
	public void preparebeforesearchWithBudget()
	{
		addDropdownData("fundList",masterCache.get("egi-fund"));
		addDropdownData("functionList", masterCache.get("egi-function"));
	}
	@Override
	public Object getModel() {
		return reportSearch;
	}
	@SkipValidation
	@Action(value="/report/functionwiseIE-beforesearch")
	public String beforesearch()
	{
		return REPORT;
	}
	@SkipValidation
	@Action(value="/report/functionwiseIE-beforeSearchWithBudget")
	public String beforeSearchWithBudget()
	{
		return "reportWithBudget";
	}
	@SkipValidation
	@Action(value="/report/functionwiseIE-exportMajorAndMinorCodewise")
	public String exportMajorAndMinorCodewise()
	{
		try {
			searchWithBudget();
			removEmptyRows(reportSearch);
			if(reportSearch.getExportType().equalsIgnoreCase("xls"))
			{
				inputStream = reportHelper.exportXls(inputStream, reportHelper.exportMajorAndMinorCodewise(ieWithBudgetList,cityWebsite.getCityName(),reportSearch,heading));
				return "functionwiseIE-XLS";
			}
				else
				{
			  inputStream = reportHelper.exportPdf(inputStream, reportHelper.exportMajorAndMinorCodewise(ieWithBudgetList,cityWebsite.getCityName(),reportSearch,heading));
			  return "functionwiseIE-PDF";
				}
		} catch (JRException e) {
			LOGGER.error(e,e);
		} catch (IOException e) {
			LOGGER.error(e,e);
		} catch (Exception e) {
			LOGGER.error(e,e);
		}
		return "functionwiseIE-XLS"; 
	}
	@SkipValidation
	@Action(value="/report/functionwiseIE-exportDeptwise")
	public String exportDeptwise()
	{
		try {
			deptWiseIEWithBudget();
			removEmptyRows(reportSearch);
			if(reportSearch.getExportType().equalsIgnoreCase("xls"))
			{
				inputStream = reportHelper.exportXls(inputStream, reportHelper.exportDeptwise(ieWithBudgetList,cityWebsite.getCityName(),reportSearch,heading));
				return "functionwiseIE-XLS";
			}
			else
			{
				inputStream = reportHelper.exportPdf(inputStream, reportHelper.exportDeptwise(ieWithBudgetList,cityWebsite.getCityName(),reportSearch,heading));
				return "functionwiseIE-PDF";
			}
		} catch (JRException e) {
			LOGGER.error(e,e);
		} catch (IOException e) {
			LOGGER.error(e,e);
		} catch (Exception e) {
			LOGGER.error(e,e);
		}
		return "functionwiseIE-XLS"; 
	}
	@SkipValidation
	@Action(value="/report/functionwiseIE-exportDetailwise")
	public String exportDetailwise()
	{
		try {
			detailWiseIEWithBudget();
			removEmptyRows(reportSearch);
			if(reportSearch.getExportType().equalsIgnoreCase("xls"))
			{
				inputStream = reportHelper.exportXls(inputStream, reportHelper.exportDetailwise(ieWithBudgetList,cityWebsite.getCityName(),reportSearch,heading));
				return "functionwiseIE-XLS";
			}
			else
			{
				inputStream = reportHelper.exportPdf(inputStream, reportHelper.exportDetailwise(ieWithBudgetList,cityWebsite.getCityName(),reportSearch,heading));
				return "functionwiseIE-PDF";
			}
		} catch (JRException e) {
			LOGGER.error(e,e);
		} catch (IOException e) {
			LOGGER.error(e,e);
		} catch (Exception e) {
			LOGGER.error(e,e);
		}
		return "functionwiseIE-XLS"; 
	}
	
	/**
	 * 
	 * @param reportSearch
	 * will be called only while generating pdf 
	 * JSP already has this logic
	 */
	private void removEmptyRows(ReportSearch reportSearch) {
		 List<CommonReportBean> ieWithBudgetList2 =new ArrayList<CommonReportBean>();
		 ieWithBudgetList2.addAll(ieWithBudgetList);
		if(reportSearch.getIncExp().equalsIgnoreCase("E"))
		{
		 for(CommonReportBean crb:ieWithBudgetList2)
		 {
			 if(crb.isZero())
				 ieWithBudgetList.remove(crb);
		 }
		}
		else
			
			{
			 for(CommonReportBean crb:ieWithBudgetList2)
			 {
				 if(crb.isZeroForIncome())
					 ieWithBudgetList.remove(crb);
			 }
			}
		int i=1;
		 for(CommonReportBean crb:ieWithBudgetList)
		 {
			 crb.setSlNo(i++);
		 }
	}

	private void savetofile(String name) {
		 FileOutputStream fos = null;
		 ObjectOutputStream out = null;
		   try
		   {
		  fos = new FileOutputStream(name);
		  out = new ObjectOutputStream(fos);
		  out.writeObject(ieWithBudgetList);
		   out.close();
		   }
		   catch (Exception e) {
			
		}
		
	}
	/*private void getFromfile(String name) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		   try
		   {
			   fis = new FileInputStream(name);
			   
			  in = new ObjectInputStream(fis);
			  
			  ieWithBudgetList =(ArrayList<CommonReportBean>) in.readObject();
			  in.close();
		   
		   }
		   catch (Exception e) {
			
		}
		
	}*/

	@SkipValidation
	public String searchWithBudget() throws Exception
	{
		
		setDatasForBudgetWise();
		populateDataSourceWithBudget(reportSearch);
		heading = generateHeading();
		return "resultWithBudget";    
	}

	private void setDatasForBudgetWise() {
		Integer majorCodeLen = Integer.valueOf(appConfigValuesDAO.getConfigValuesByModuleAndKey
				(Constants.EGF,FinancialConstants.APPCONFIG_COA_MAJORCODE_LENGTH).get(0).getValue());
		reportSearch.setMajorCodeLen(majorCodeLen);
		Integer minorCodeLen = Integer.valueOf(appConfigValuesDAO.getConfigValuesByModuleAndKey
				(Constants.EGF,FinancialConstants.APPCONFIG_COA_MINORCODE_LENGTH).get(0).getValue());
		
		reportSearch.setMinorCodeLen(minorCodeLen);
		
		if(reportSearch.getAsOnDate()!=null)  
		{
			CFinancialYear financialYearByDate = getFinancialYearDAO().getFinancialYearByDate(reportSearch.getAsOnDate());
			reportSearch.setFinYearId(financialYearByDate.getId());
			reportSearch.setYearStartDate(financialYearByDate.getStartingDate());
			setPreviousYearDates();
		}
/*		if(reportSearch.getIncExp().equalsIgnoreCase("I"))
		{
			setPreviousYearDates();
		}*/
	}

	private void setPreviousYearDates() {
		CFinancialYear previousfinancialYear = getFinancialYearDAO().getPreviousFinancialYearByDate(reportSearch.getAsOnDate());
		reportSearch.setPreviousFinYearId(previousfinancialYear.getId());
		reportSearch.setPreviousYearStartDate(previousfinancialYear.getStartingDate());  
		Calendar cal = Calendar.getInstance();
		cal.setTime(reportSearch.getAsOnDate());       
		cal.add(Calendar.YEAR, -1);
		reportSearch.setPreviousYearDate(cal.getTime());
	}
	private String generateHeading() {
	 StringBuffer heading=new StringBuffer(256);
	 heading.append(" FunctionWise ");
	 if(reportSearch.getIncExp().equalsIgnoreCase("E"))
	 {
		 heading.append(" Expense Subsidary Register ");
	 }else{
		 heading.append(" Income Subsidary Register ");
	 }
	 if(reportSearch.getFunction()!=null && reportSearch.getFunction().getId()!=null &&  reportSearch.getFunction().getId()!=-1)
	 {
		 heading.append(" For the Function Code ");
		 String  code =(String) persistenceService.find("select code from CFunction where id=?",reportSearch.getFunction().getId());
		 heading.append(code);
	 }
	 if(reportSearch.getScheduleName()!=null)
	 {
		 heading.append(" For Schedule "+reportSearch.getScheduleName());
	 }
	 if(reportSearch.getDepartment()!=null && !reportSearch.getDepartment().getName().isEmpty())
	 {
		 heading.append(" For Department "+reportSearch.getDepartment().getName());
	 }
	 if(reportSearch.getFund()!=null && reportSearch.getFund().getId()!=-1)
	 {
		 heading.append(" In Fund ");
		 String  name =(String) persistenceService.find("select name from Fund where id=?",reportSearch.getFund().getId());
		 heading.append(name);
	 }
	 
	 heading.append(" from " +sdf.format(reportSearch.getYearStartDate())+" - "+sdf.format(reportSearch.getAsOnDate()));
	 
	 return heading.toString();
		
	}

	@Action(value="/report/functionwiseIE-deptWiseIEWithBudget")
	public String deptWiseIEWithBudget() throws Exception
	{
		setDatasForBudgetWise();
		reportSearch.setByDepartment(true);
		
		reportSearch.setDeptList(masterCache.get("egi-department"));
		
		populateDataSourceWithBudget(reportSearch);
		 /*if(reportSearch.getIncExp().equalsIgnoreCase("E")) 
			 getFromfile("deptE");
		 else
			 getFromfile("deptI");*/
		heading = generateHeading();
		return "deptWiseWithBudget";    
	}
	@Action(value="/report/functionwiseIE-detailWiseIEWithBudget")
	public String detailWiseIEWithBudget() throws Exception
	{
	 setDatasForBudgetWise();
	//override minor code length with detail code for detail report
	Integer minorCodeLen = Integer.valueOf(appConfigValuesDAO.getConfigValuesByModuleAndKey
			(Constants.EGF,FinancialConstants.APPCONFIG_COA_DETAILCODE_LENGTH).get(0).getValue());
	reportSearch.setMinorCodeLen(minorCodeLen);
	reportSearch.setByDepartment(true);
	reportSearch.setByDetailCode(true);
	reportSearch.setDeptList(masterCache.get("egi-department"));
	
	populateDataSourceWithBudget(reportSearch);
	/* if(reportSearch.getIncExp().equalsIgnoreCase("E")) 
		 getFromfile("detailE");
	 else
		 getFromfile("detailI");*/
	heading = generateHeading();
	return "detailWiseWithBudget";
		
	}
	
	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String search() throws Exception
	{
		Integer majorCodeLen = Integer.valueOf(appConfigValuesDAO.getConfigValuesByModuleAndKey
				(Constants.EGF,FinancialConstants.APPCONFIG_COA_MAJORCODE_LENGTH).get(0).getValue());
		reportSearch.setMajorCodeLen(majorCodeLen);
		populateDataSource(reportSearch);
		
		return "print";
	}
	public String generateFunctionwiseIEHtml() throws Exception
	{
		populateDataSource(reportSearch);
		inputStream = reportHelper.exportHtml(inputStream, reportHelper.generateFunctionwiseIEJasperPrint(functionwiseIE,cityWebsite.getCityName(),getvalue(reportSearch.getIncExp())));
		return "functionwiseIE-HTML";
	}
	@Action(value="/report/functionwiseIE-generateFunctionwiseIEPdf")
	public String generateFunctionwiseIEPdf() throws Exception{
		populateDataSource(reportSearch);
		inputStream = reportHelper.exportPdf(inputStream, reportHelper.generateFunctionwiseIEJasperPrint(functionwiseIE,cityWebsite.getCityName(),getvalue(reportSearch.getIncExp())));
		return "functionwiseIE-PDF";
	}
	@Action(value="/report/functionwiseIE-generateFunctionwiseIEXls")
	public String generateFunctionwiseIEXls() throws Exception{
		populateDataSource(reportSearch);
		inputStream = reportHelper.exportXls(inputStream, reportHelper.generateFunctionwiseIEJasperPrint(functionwiseIE,cityWebsite.getCityName(),getvalue(reportSearch.getIncExp())));
		return "functionwiseIE-XLS";
	}
	public void populateDataSource(ReportSearch reportSearch)throws Exception
	{
		//functionwiseIEService.setReportSearch(reportSearch);
		functionwiseIEService.populateData(functionwiseIE,reportSearch);
		cityWebsite = cityWebsiteDAO.getCityWebSiteByURL((String) getSession().get("cityurl"));
		functionwiseIE.setCityName(cityWebsite.getCityName());
	}
	
	public void populateDataSourceWithBudget(ReportSearch reportSearch)throws Exception
	{
		if(reportSearch.getIncExp().equalsIgnoreCase("E"))
			ieWithBudgetList = functionwiseIEService.populateDataWithBudget(functionwiseIE,reportSearch);
		else
			ieWithBudgetList= functionwiseIEService.populateIncomeDataWithBudget(functionwiseIE,reportSearch);
		
		cityWebsite = cityWebsiteDAO.getCityWebSiteByURL((String) getSession().get("cityurl"));
		functionwiseIE.setCityName(cityWebsite.getCityName());
	}
	
	public List<CommonReportBean> getIeWithBudgetList() {
		return ieWithBudgetList;
	}

	public void setIeWithBudgetList(List<CommonReportBean> ieWithBudgetList) {
		this.ieWithBudgetList = ieWithBudgetList;
	}

	public String getvalue(final String incExp)
	{
		if("I".equals(incExp))
			return "Income";
		else
			return "Expense";
	}
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setReportHelper(final ReportHelper reportHelper) {
		this.reportHelper = reportHelper;
	}
	public void validate()
	{
		if(reportSearch.getIncExp()==null || reportSearch.getIncExp().equals("-1") )
			addFieldError("incExp", getMessage("report.income.expense.mandatory"));
		if(reportSearch.getStartDate()==null || reportSearch.getStartDate().equals(""))
			addFieldError("startDate", getMessage("report.startdate.mandatory"));
		if(reportSearch.getEndDate()==null || reportSearch.getEndDate().equals(""))
			addFieldError("endDate", getMessage("report.enddate.mandatory"));
		try{
			if(!reportSearch.getStartDate().equals("") )
				 sdf.parse(reportSearch.getStartDate());
		}catch(Exception e){
			LOGGER.error("ERROR"+e.getMessage(), e);
			addFieldError("startDate", getMessage("report.startdate.invalid.format"));}
		try{
			if(!reportSearch.getEndDate().equals("") )
				 sdf.parse(reportSearch.getEndDate());
		}catch(Exception e){
			LOGGER.error("ERROR"+e.getMessage(), e);
			addFieldError("endDate", getMessage("report.enddate.invalid.format"));}
		super.validate();
	}
	public FunctionwiseIE getFunctionwiseIE() {
		return functionwiseIE;
	}
	protected String getMessage(String key) {
		return getText(key);
	}

	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(Date todayDate) {
		this.todayDate = todayDate;
	}
}
