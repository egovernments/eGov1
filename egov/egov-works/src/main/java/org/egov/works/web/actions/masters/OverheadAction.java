/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It
	   is required that all modified versions of this material be marked in
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program
	   with regards to rights under trademark law for use of the trade names
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.works.web.actions.masters;  
  
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.service.CommonsService;
import org.egov.exceptions.EGOVException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.works.models.masters.ExpenditureType;
import org.egov.works.models.masters.Overhead;
import org.egov.works.services.WorksService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
  
  
@ParentPackage("egov")  
@Result(name=Action.SUCCESS, type="ServletRedirectResult.class", location = "overhead.action")  
public class OverheadAction extends BaseFormAction{ 
	/**
	 *  An instance of Logger
	 */
	private static final Logger logger = Logger.getLogger(OverheadAction.class);
	/**
	 * An instance <code>PersistenceService</code>
	 */  
	private PersistenceService<Overhead,Long> overheadService;  
	
	/**
	 * An instance of <code>Overhead</code> populated from the view.
	 */
	private Overhead overhead= new Overhead();  
	
	/**
	 * A <code>Long</code> value representing the id of the model.
	 */
	private Long id;
	
	/**
	 * A <code>List</code> of <code>Overhead</code> objects representing 
	 * overhead data retrieved from the database.
	 */
	private WorksService worksService;
	

	private List<Overhead> overheadList=null;
	
	private List<ExpenditureType> expenditureTypeList=new ArrayList<ExpenditureType>();
	
	@Autowired
        private CommonsService commonsService;
	
	/**
	 * Default constructor
	 */
	public OverheadAction() {
		addRelatedEntity("account", CChartOfAccounts.class);
	}

	/**
	 * The default action method/
	 * 
	 * @return a <code>String</code> representing the value 'INDEX'
	 */
	public String execute() { 
		return list();  
	}  
	  
	/**
	 * This method is invoked to create a new form.
	 * 
	 * @return a <code>String</code> representing the value 'NEW'
	 */
	public String newform(){ 
		return NEW;  
	}  
	  
	/**
	 * This method is invoked to display the list of over heads.
	 * @return
	 */
	public String list() 
	{  
		overheadList = overheadService.findAllBy(" from Overhead o order by name asc");
		return INDEX;  
	}  
  
	public String edit()
	{  
		return EDIT;  
	}  
	  
	public String save()
	{  
		overheadService.persist(overhead);
		return SUCCESS;  
	}  
	  
	public String create()
	{  
		overheadService.persist(overhead);
		addActionMessage(getText("overhead.save.success","The overhead was saved successfully"));
        return list();  
	}  
	  
	public Object getModel() 
	{
		return overhead;  
	}  

	public void prepare()
	{
		if (id != null) {
			 overhead = overheadService.findById(id, false);
	    }
		
		expenditureTypeList = (List)overheadService.findAllBy("select distinct expenditureType from WorkType");
		
		super.prepare();
		setupDropdownDataExcluding("account");
		try {
			
			List<CChartOfAccounts> accounts = commonsService.getAccountCodeByPurpose(Integer.valueOf(worksService.getWorksConfigValue("OVERHEAD_PURPOSE")));
			addDropdownData("accountList", accounts);
			
		}
		catch (EGOVException e) {
			logger.error("Unable to load accountcode :"+e.getMessage());
			addFieldError("accountcode", "Unable to load accountcode");
		} 
		
		String[] expenditure = parameters.get("expenditure");
		if(!ArrayUtils.isEmpty(expenditure) && !expenditure[0].equals("-1"))
		{
			overhead.setExpenditureType(new ExpenditureType(expenditure[0]));
		}
	}

	public List<Overhead> getOverheadList() {  
		return overheadList;  
	}  
	
	public void setOverheadList(List<Overhead> overheadList) {
		this.overheadList = overheadList;
	}
	
	public PersistenceService<Overhead, Long> getOverheadService() 
	{
		return overheadService;
	}
	public void setOverheadService(PersistenceService<Overhead,Long> service) {  
		this.overheadService= service;  
	}  
	
	public Overhead getOverhead() {
		return overhead;
	}

	public void setOverhead(Overhead overhead) {
		this.overhead = overhead;
	}
	
	public List<ExpenditureType> getExpenditureTypeList() {
		return expenditureTypeList;
	}

	public void setExpenditureTypeList(List<ExpenditureType> expenditureTypeList) {
		this.expenditureTypeList = expenditureTypeList;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CommonsService getCommonsService() {
		return commonsService;
	}

	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}
	public WorksService getWorksService() {
		return worksService;
	}

	public void setWorksService(WorksService worksService) {
		this.worksService = worksService;
	}
}