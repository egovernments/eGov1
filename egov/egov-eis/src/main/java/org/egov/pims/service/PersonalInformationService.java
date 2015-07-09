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
package org.egov.pims.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.commons.EgwStatus;
import org.egov.commons.service.CommonsService;
import org.egov.commons.service.EntityTypeService;
import org.egov.commons.utils.EntityType;
import org.egov.eis.entity.EmployeeView;
import org.egov.eis.utils.constants.EisConstants;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.script.service.ScriptService;
import org.egov.infstr.ValidationException;
import org.egov.infstr.config.dao.AppConfigValuesDAO;
import org.egov.infstr.services.Page;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.DateUtils;
import org.egov.infstr.utils.Sequence;
import org.egov.infstr.utils.SequenceGenerator;
import org.egov.pims.model.PersonalInformation;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author DivyaShree
 *
 */
public class PersonalInformationService extends PersistenceService<PersonalInformation, Integer> implements EntityTypeService 
{
	//named query tags
	private static final String ACTIVEEMPSBYLOGGEDINUSER="EMPVIEW-ACTIVE-EMPS-BYLOGGEDINUSER";
	private final String PERSONALINFOBYIDS="PERSONALINFO-BYIDS";
	private final String PERSONALINFOEMPCODESTARTSWITH="PERSONALINFO-EMPCODE-STARTSWITH";
	private final String EMPVIEWBYLOGGEDINUSER="EMPVIEW-EMPS-BYLOGGEDINUSER";
	private final String EMPVIEWDEPTIDSLOGGEDINUSER="EMPVIEW-DEPTIDS-LOGGEDINUSER";
	private static final String EMPVIEWACTIVEEMPS="EMPVIEW-ACTIVE-EMPS"; 
	private static final String EMPVIEWEMPSLASTASSPRD="EMPVIEW-EMPS-LASTASSPRD";
	private CommonsService commonsService;
	private SequenceGenerator sequenceGenerator;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private AppConfigValuesDAO appConfigValuesDAO;
    
	public Session  getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}
	public SequenceGenerator getSequenceGenerator() {
		return sequenceGenerator;
	}

	public void setSequenceGenerator(SequenceGenerator sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}
	private ScriptService scriptService;

	public ScriptService getScriptService() {
		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}


	
	public CommonsService getCommonsService() {
		return commonsService;
	}

	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}

	/**
	 * since it is mapped to only one AccountDetailType -creditor it ignores the input parameter
	 */
	public List<EntityType> getAllActiveEntities(Integer employeeId) {
		List<EntityType> entities=new ArrayList<EntityType>();
		entities.addAll(findAllByNamedQuery("ACTIVE_EMPLOYEES"));
		return entities;
	}

	public List<EntityType> filterActiveEntities(String filterKey, int maxRecords, Integer accountDetailTypeId) {
		Integer pageSize = (maxRecords > 0 ? maxRecords : null);
		List<EntityType> entities=new ArrayList<EntityType>();
		Page pg = findPageByNamedQuery("ACTIVE_EMPLOYEES_STARTSWITH", 0, pageSize,filterKey + "%" ,filterKey + "%");
		entities.addAll(pg.getList());
		return entities;
	}

	/**
	 * when filterbydept set to yes return employee list based on the login user who has the current assignment  
	 * when filterbydept set to no return all employee list  who has the assignment in the current/last assignment 
	 * @param userid
	 * @param autoValue
	 * @param maxRecords 
	 * @return employee list
	 */
	public List<PersonalInformation> getActiveEmpListByUserLogin(Integer userid,String autoValue,int maxRecords)
	{
		Integer pageSize = (maxRecords > 0 ? maxRecords : null);
		autoValue=(autoValue+"%");
		String filterByDept = appConfigValuesDAO.getAppConfigValue("EIS-PAYROLL","FILTERBYDEPT","false");

		if(filterByDept!=null && filterByDept.toUpperCase().equals("YES"))
		{    	
			List deptIdList=getDeptsForLoggedInUser(userid);
			if(deptIdList.isEmpty())
				return Collections.emptyList();
			List<PersonalInformation> personalinfoIdList=findPageByNamedQuery(ACTIVEEMPSBYLOGGEDINUSER, 0, maxRecords,autoValue,deptIdList).getList();
			return personalinfoIdList;
		}
		else
		{
			return findPageByNamedQuery(EMPVIEWACTIVEEMPS, 0,pageSize,autoValue).getList();
			//return findAllByNamedQuery(PERSONALINFOEMPCODESTARTSWITH,autoValue);
		}	


	}
	/**
	 * return employee list based on the login user who has the assignment in the current period/MaxFromdate
	 * @param userid
	 * @param autoValue
	 * @param maxRecords 
	 * @return employee list
	 */
	public List<PersonalInformation> getEmpListByUserLogin(Integer userid,String autoValue,int maxRecords)
	{
		Integer pageSize = (maxRecords > 0 ? maxRecords : null);
		autoValue=(autoValue+"%");
		String filterByDept = appConfigValuesDAO.getAppConfigValue("EIS-PAYROLL","FILTERBYDEPT","false");

		if(filterByDept!=null && filterByDept.toUpperCase().equals("YES"))
		{   
			List deptIdList=getDeptsForLoggedInUser(userid);
			if(deptIdList.isEmpty())
				return Collections.emptyList();

			List personalinfoIdList=findPageByNamedQuery(EMPVIEWBYLOGGEDINUSER, 0, pageSize,autoValue,autoValue,deptIdList).getList();
			return personalinfoIdList;
		}
		else
		{
			return findPageByNamedQuery(EMPVIEWEMPSLASTASSPRD,0,pageSize,autoValue,autoValue).getList();
		}	


	}
	/**
	 * Its applicable only when 'isfiltebydept' is set to yes 
	 * returns the departments for the logged in user dept ,if he/she is HOD then includes those departments as well
	 * @param userId
	 * @return DepartmentLsit of ids
	 */
	private List getDeptsForLoggedInUser(Integer userId){
		List<BigDecimal> deptList=	findPageByNamedQuery(EMPVIEWDEPTIDSLOGGEDINUSER, 0,null,userId,userId).getList();
		List<Integer> deptListInt=new ArrayList<Integer>();
		for(BigDecimal deptId:deptList)
		{ 
			if(deptId!=null)
			{
				deptListInt.add(deptId.intValue());
			}
		}
		return deptListInt;
	}


	public List<EmployeeView> getAllActiveEmployeesEmpViewByPrimaryAssignment(String filterKey, int maxRecords)
	{
		Integer pageSize = (maxRecords > 0 ? maxRecords : null);
		List<EmployeeView> personalInfEntities=new ArrayList<EmployeeView>();
		Page pg = findPageByNamedQuery("ALLACTIVE-EMPS-EMPVIEW", 0,pageSize,filterKey + "%");
		personalInfEntities.addAll(pg.getList());
		return personalInfEntities;
	}

	/**
	 * Returns the generated employee code. The employee code can be based on the employee type.
	 * This calls the script named "pims.employeeCode.generator" to get the next code value.
	 * @param employee
	 * @return
	 */
	public String generateEmployeeCode(PersonalInformation employee) {
		//SequenceGenerator sequenceGenerator = new SequenceGenerator(sessionFactory);
		Object result=scriptService.executeScript("pims.employeeCode.generator", scriptService.createContext("sequenceGenerator",sequenceGenerator,"employee", employee));
		if (result instanceof Sequence) {
			return ((Sequence)result).getFormattedNumber();
		}
		return result.toString();
	}

	/**
	 * Returns List of Employees for the given status  and
	 * Date range considered for the status['Retired','Deceased'] and as of toDate for the status[ 'Employed','Suspended' ]
	 * @param statusid 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public  List<PersonalInformation> getEmployeesByStatus(Integer statusid ,Date fromDate,Date toDate){
		List<PersonalInformation> employeeList ; 
		Criteria criteria=null;

		try
		{
			criteria=getCriteriaForEmpSearchByStatus(statusid,fromDate,toDate);	
			employeeList= criteria.list();

		} catch (HibernateException he) {
			throw new EGOVRuntimeException("Exception:" + he.getMessage(),he);
		} catch (Exception he) {
			throw new EGOVRuntimeException("Exception:" + he.getMessage(),he);

		}
		return employeeList;
	}
	/**
	 * Returns Page  for the given status  and
	 * Date range considered for the status['Retired','Deceased'] and as of toDate for the status[ 'Employed','Suspended' ]
	 * @param statusid 
	 * @param fromDate
	 * @param toDate
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public  Page getEmployeesByStatus(Integer statusid ,Date fromDate,Date toDate,Integer pageNumber,Integer pageSize){

		Criteria criteria=null;			
		criteria=getCriteriaForEmpSearchByStatus(statusid,fromDate,toDate);				
		return new Page(criteria,pageNumber,pageSize);

	}
	/**
	 * Returns total record count  for the given status  and
	 * Date range considered for the status['Retired','Deceased'] and as of toDate for the status[ 'Employed','Suspended' ]
	 * @param statusid 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public  int getTotalCountOfEmployeesByStatus(Integer statusid ,Date fromDate,Date toDate){
		Criteria criteria=null;
		int totalSize=0;
		try
		{

			criteria=getCriteriaForEmpSearchByStatus(statusid,fromDate,toDate);	

			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); 
			criteria.setProjection(Projections.rowCount());
			if(criteria.uniqueResult()!=null)
			{
				totalSize=((Long)criteria.uniqueResult()).intValue();
			}
		} catch (HibernateException he) {
			throw new EGOVRuntimeException("Exception:" + he.getMessage(),he);
		} catch (Exception he) {
			throw new EGOVRuntimeException("Exception:" + he.getMessage(),he);

		}
		return totalSize;
	}

	private Criteria getCriteriaForEmpSearchByStatus(Integer statusid ,Date fromDate,Date toDate)
	{
		EgwStatus egwStatus=commonsService.getEgwStatusById(statusid);
		DetachedCriteria detachCriteriaPersonalInfo=DetachedCriteria.forClass(PersonalInformation.class,"emp");
		if(egwStatus.getModuletype().equals("Employee") && egwStatus.getDescription().equalsIgnoreCase("Employed"))
		{
			detachCriteriaPersonalInfo.createAlias("emp.egpimsAssignment", "assPrd").
			add(Restrictions.and(Restrictions.le("assPrd.fromDate", toDate), Restrictions.or(Restrictions.ge("assPrd.toDate", toDate), Restrictions.isNull("assPrd.toDate")) ));


		}
		else if(egwStatus.getModuletype().equals("Employee") && egwStatus.getDescription().equalsIgnoreCase("Retired"))
		{
			detachCriteriaPersonalInfo.add(Restrictions.between("emp.retirementDate", fromDate, toDate));				

		}
		else if(egwStatus.getModuletype().equals("Employee") && egwStatus.getDescription().equalsIgnoreCase("Deceased"))
		{
			detachCriteriaPersonalInfo.add(Restrictions.between("emp.deathDate", fromDate, toDate));
		}
		return detachCriteriaPersonalInfo.getExecutableCriteria(getCurrentSession());	
	}
	
	/**
	 * This API returns the list of EmployeeView objects which have a current assignment or
	 * assignment as on date based on the parameters in the map.
	 * @param criteriaParams - HashMap<String,Object> where the following keys are supported:-
	 * "departmentId" 	- Pass the id of the department to restrict the employees to
	 * "designationId"  - Pass the id of the designation to restrict the resultset
	 * "isPrimary"      - Possible values "Y" or "N". If "Y", then only employees with 
	 * 					Primary assignment will be returned. If "N" only employees with 
	 * 					temporary assignment is returned. If this key is not present in the map,
	 * 					employees with both temporary as well as primary assignments are returned.
	 * "asOnDate"		- Value should be the date for which the employees need to have an
	 * 					assignment. If this key is not passed, employeed that have an assignment
	 * 					as of today will be returned.
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<EmployeeView> getListOfEmployeeViewBasedOnCriteria(HashMap<String,Object> criteriaParams, Integer pageNo, Integer pageSize) {
		
		List<EmployeeView> employeeList = new ArrayList<EmployeeView>();
		try {
			Criteria criteria=getCurrentSession().createCriteria(EmployeeView.class);
			Date asOnDate;
			for (Map.Entry<String, Object> entry : criteriaParams.entrySet())
			{
				if ("departmentId".equals(entry.getKey())) {
					criteria.createAlias("deptId","department")
					.add(Restrictions.eq("department.id",entry.getValue() ));
				}
				if ("designationId".equals(entry.getKey())) {
					criteria.createAlias("desigId","designation")
					.add(Restrictions.eq("designation.designationId",entry.getValue() ));
				}
				if ("isPrimary".equals(entry.getKey())) {
					criteria.add(Restrictions.eq("isPrimary",entry.getValue() ));
				}
				if ("asOnDate".equals(entry.getKey())) 
					asOnDate = (Date)entry.getValue();
				else
					asOnDate = DateUtils.today();
				criteria.add(Restrictions.and(Restrictions.le("fromDate",asOnDate), Restrictions.ge("toDate",asOnDate)));

				
			}
			criteria.addOrder(Order.asc("id"));
			employeeList = new Page(criteria, pageNo, pageSize).getList();
		} catch (Exception e) {
			throw new EGOVRuntimeException("Error occured in searching for employees",e);
		}
		
		return employeeList;
	}
	/**
	 * This API returns the list of EmployeeView objects which have a current assignment or
	 * assignment as on date based on the parameters in the map.
	 * @param criteriaParams - HashMap<String,Object> where the following keys are supported:-
	 * "departmentId" 	- Pass the List of id of the department to restrict the employees to
	 * "designationId"  - Pass the id of the designation to restrict the resultset
	 * "isPrimary"      - Possible values "Y" or "N". If "Y", then only employees with 
	 * 					Primary assignment will be returned. If "N" only employees with 
	 * 					temporary assignment is returned. If this key is not present in the map,
	 * 					employees with both temporary as well as primary assignments are returned.
	 * "employeeName" 	- Pass employee name.
	 * "employeeCode" 	- Pass employee codes as list.		 
	 * "isActive" 	    - Pass Integer Value either 0 or 1.	 * 
	 * "asOnDate"		- Value should be the date for which the employees need to have an
	 * 					assignment. If this key is not passed, employeed that have an assignment
	 * 					as of today will be returned.
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
public List<EmployeeView> getListOfEmployeeViewBasedOnListOfDesignationAndOtherCriteria(HashMap<String,Object> criteriaParams, Integer pageNo, Integer pageSize) {
		
		List<EmployeeView> employeeList = new ArrayList<EmployeeView>();
		try {
			Criteria criteria=getCurrentSession().createCriteria(EmployeeView.class);
			Date asOnDate;
			for (Map.Entry<String, Object> entry : criteriaParams.entrySet())
			{
				if ("departmentId".equals(entry.getKey())) {
					criteria.createAlias("deptId","department")
					.add(Restrictions.eq("department.id",entry.getValue() ));
				}
				if ("designationId".equals(entry.getKey())) {
					criteria.createAlias("desigId","designation")
					.add(Restrictions.in("designation.designationId",(List<Integer>) entry.getValue()));
				}
				if ("isPrimary".equals(entry.getKey())) {
					criteria.add(Restrictions.eq("isPrimary",entry.getValue() ));
				}
				if("employeeName".equals(entry.getKey()) && entry.getValue()!=null && !"".equals(entry.getValue()))
				{
					criteria.add(Restrictions.ilike("employeeName",entry.getValue().toString() ,org.hibernate.criterion.MatchMode.ANYWHERE));
				}
				if("isActive".equals(entry.getKey()) && entry.getValue()!=null && !"".equals(entry.getValue()))
				{
					criteria.add(Restrictions.eq("isActive",Integer.valueOf(entry.getValue().toString())));
				}
				if ("employeeCode".equals(entry.getKey())) {
					criteria.add(Restrictions.in("employeeCode",(List<String>) entry.getValue()));
				}
				
				if ("asOnDate".equals(entry.getKey())) 
					asOnDate = (Date)entry.getValue();
				else
					asOnDate = DateUtils.today();
				criteria.add(Restrictions.and(Restrictions.le("fromDate",asOnDate), Restrictions.ge("toDate",asOnDate)));

				
			}
			criteria.addOrder(Order.asc("id"));
			employeeList = new Page(criteria, pageNo, pageSize).getList();
		} catch (Exception e) {
			throw new EGOVRuntimeException("Error occured in searching for employees",e);
		}
		
		return employeeList;
	}
	@Override
	public List getAssetCodesForProjectCode(Integer accountdetailkey)
			throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<? extends EntityType> validateEntityForRTGS(List<Long> idsList)
			throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<? extends EntityType> getEntitiesById(List<Long> idsList)
			throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

}
