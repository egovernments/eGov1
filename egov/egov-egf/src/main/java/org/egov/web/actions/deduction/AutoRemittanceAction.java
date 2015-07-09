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
package org.egov.web.actions.deduction;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.egov.dao.recoveries.TdsHibernateDAO;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.model.deduction.DepartmentDOMapping;
import org.egov.model.recoveries.Recovery;
import org.egov.model.recoveries.RemittanceSchedulerLog;
import org.egov.services.deduction.ScheduledRemittanceService;
import org.egov.utils.FinancialConstants;
import org.hibernate.HibernateException;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly=true)
public class AutoRemittanceAction extends BaseFormAction {

	private final static Logger LOGGER=Logger.getLogger(AutoRemittanceAction.class);
	private static final String MANUAL = "manual";
	private ScheduledRemittanceService scheduledRemittanceService;
	private String glcode;
	private Integer dept;
	private String drawingOfficer;
	private Date lastRunDate;
	private Map<String,String> coaMap;
	private	EgovMasterDataCaching masterCache = EgovMasterDataCaching.getInstance();
	private List<DepartmentDOMapping> deptDOList;
	private RemittanceSchedulerLog remittanceScheduler;
	private Map<String,String> lastRunDateMap ;
	private TdsHibernateDAO tdsDAO;
	
	@Override
	public Object getModel() {
			return null;
	}
	
@Action(value="/deduction/autoRemittance-manualSchedule")
	public String manualSchedule()
	{
		try {
			coaMap = new LinkedHashMap<String,String>();
			List<Recovery> allActiveAutoRemitTds = tdsDAO.getAllActiveAutoRemitTds();
			
			for(Recovery r:allActiveAutoRemitTds)
			{
				coaMap.put(r.getChartofaccounts().getGlcode(), r.getChartofaccounts().getGlcode()+"-"+r.getChartofaccounts().getName());	
			}
			
			addDropdownData("departmentList", masterCache.get("egi-department"));
			deptDOList = persistenceService.findAllBy("from DepartmentDOMapping where department is not null  ");
			
			List<Object[]> list =HibernateUtil.getCurrentSession().
					createSQLQuery("select glcode, to_char(max(lastrundate),'dd/mm/yyyy') from egf_remittance_scheduler where glcode is not null and sch_type='A' "+
							" GROUP by glcode order by glcode").list();
			lastRunDateMap=new HashMap<String,String>();
			for(Object[] ob:list)
			{
				lastRunDateMap.put((String)ob[0],(String) ob[1]);
			}
		} catch (EGOVRuntimeException e) {
			addActionError("failed");
		} catch (HibernateException e) {
			addActionError("failed");
		} catch (Exception e) {
			addActionError("failed");
	}
		
		return "manual";
		
	}
	
	@ValidationErrorPage(value="messages")
	public String schedule()
	{
		try {
			LOGGER.info("Inside RemittanceJob");
			remittanceScheduler = new RemittanceSchedulerLog();
			remittanceScheduler.setGlcode(glcode);
			remittanceScheduler.setSchType(FinancialConstants.REMITTANCE_SCHEDULER_SCHEDULAR_TYPE_MANUAL);
			remittanceScheduler.setSchJobName("Manual");
			remittanceScheduler.setLastRunDate(new Date());
			remittanceScheduler.setCreatedDate(new Date());
			remittanceScheduler.setCreatedBy(EgovThreadLocals.getUserId().intValue());
			remittanceScheduler.setStatus("Started");      
			scheduledRemittanceService.getRemittanceSchedulerLogService().persist(remittanceScheduler);
			Long schedularLogId=remittanceScheduler.getId();
			boolean searchRecovery = scheduledRemittanceService.searchRecovery(glcode,"Manual",schedularLogId,dept,lastRunDate);
			if(searchRecovery==false)
			{
				addActionMessage(getText("schedular.failed"));
				addActionMessage(scheduledRemittanceService.getErrorMessage().toString());
			}else
			{
				addActionMessage(getText("schedular.succeful"));
			}
		}
		catch(ValidationException e)
		{
			addActionMessage(getText("schedular.failed"));
			throw new ValidationException(Arrays.asList(new ValidationError(scheduledRemittanceService.getErrorMessage().toString(),scheduledRemittanceService.getErrorMessage().toString())));	
		}
		
		catch(Exception e)
		{
			addActionMessage(getText("schedular.failed"));
			throw new ValidationException(Arrays.asList(new ValidationError(scheduledRemittanceService.getErrorMessage().toString(),scheduledRemittanceService.getErrorMessage().toString())));	
		}
		List<String> findAllBy = (List<String>)scheduledRemittanceService.getRemittancePersistenceService().getPersistenceService().findAllBy("select voucherheaderId.voucherNumber from " +
				"RemittanceSchedulePayment  where schId.id=?",remittanceScheduler.getId());
		if(findAllBy.isEmpty())
		{
			addActionMessage(" No Payments Created ");
		}else
		{
		addActionMessage(" Payment vouchernumbers listed below");
		addActionMessage(findAllBy.toString().replace('[', ' ').replace(']', ' '));
		}
		return "messages";  
	}
	
	
	public void setScheduledRemittanceService(
			ScheduledRemittanceService scheduledRemittanceService) {
		this.scheduledRemittanceService = scheduledRemittanceService;     
	}





	public String getGlcode() {
		return glcode;
	}





	public void setGlcode(String glcode) {
		this.glcode = glcode;
	}





	public Integer getDept() {
		return dept;
	}





	public void setDept(Integer dept) {
		this.dept = dept;
	}





	public String getDrawingOfficer() {
		return drawingOfficer;
	}





	public void setDrawingOfficer(String drawingOfficer) {
		this.drawingOfficer = drawingOfficer;
	}





	public Date getLastRunDate() {  
		return lastRunDate;
	}





	public void setLastRunDate(Date lastRunDate) {
		this.lastRunDate = lastRunDate;
	}

	public Map<String, String> getCoaMap() {
		return coaMap;
	}

	public void setCoaMap(Map<String, String> coaMap) {
		this.coaMap = coaMap;
	}

	public RemittanceSchedulerLog getRemittanceScheduler() {
		return remittanceScheduler;
	}

	public void setRemittanceScheduler(RemittanceSchedulerLog remittanceScheduler) {
		this.remittanceScheduler = remittanceScheduler;
	}

	public List<DepartmentDOMapping> getDeptDOList() {
		return deptDOList;
	}

	public void setDeptDOList(List<DepartmentDOMapping> deptDOList) {
		this.deptDOList = deptDOList;
	}

	public Map<String, String> getLastRunDateMap() {
		return lastRunDateMap;
	}

	public void setLastRunDateMap(Map<String, String> lastRunDateMap) {
		this.lastRunDateMap = lastRunDateMap;
	}

	public void setTdsDAO(TdsHibernateDAO tdsDAO) {
		this.tdsDAO = tdsDAO;
	}

}
