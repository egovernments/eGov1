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

package org.egov.collection.web.actions.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.convention.annotation.Results;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.service.ServiceCategoryService;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.Fundsource;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.models.ServiceAccountDetails;
import org.egov.infstr.models.ServiceCategory;
import org.egov.infstr.models.ServiceDetails;
import org.egov.infstr.models.ServiceSubledgerInfo;
import org.egov.infstr.services.PersistenceService;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

@ParentPackage("egov")
@Namespace("/service")
@ResultPath("/WEB-INF/jsp/")
@Results({
    @Result(name=ServiceDetailsAction.NEW,location="service/serviceDetails-new.jsp"),
    @Result(name="list",location="service/serviceDetails-list.jsp"),
    @Result(name=ServiceDetailsAction.BEFORECREATE,location="service/serviceDetails-beforeCreate.jsp"),
    @Result(name="codeUniqueCheck",location="service/serviceDetails-codeUniqueCheck.jsp"),
    @Result(name=ServiceDetailsAction.MESSAGE,location="service/ serviceDetails-message.jsp")
  })
public class ServiceDetailsAction extends BaseFormAction {

	private static final long serialVersionUID = 1L;
	@Autowired
	private ServiceCategoryService serviceCategoryService;
	private PersistenceService<ServiceDetails, Long> serviceDetailsService;
	private ServiceDetails serviceDetails = new ServiceDetails();
	protected static final String BEFORECREATE =  "beforeCreate" ; 
	protected static final String BEFOREMODIFY =  "beforeModify" ; 
	protected static final String MESSAGE =  "message" ; 
	private List<ServiceAccountDetails> accountDetails = new ArrayList<ServiceAccountDetails>();
	private List<ServiceSubledgerInfo> subledgerDetails = new ArrayList<ServiceSubledgerInfo> ();
	private List<Long> departmentList = new ArrayList<Long>(); 
	private List<ServiceDetails> serviceList;
	private Boolean isVoucherApproved = Boolean.FALSE;
	@Autowired
	private DepartmentService departmentService;
	
	public ServiceDetailsAction(){
		
		addRelatedEntity("serviceCategory", ServiceCategory.class);
		addRelatedEntity("fund", Fund.class);
		addRelatedEntity("scheme", Scheme.class);
		addRelatedEntity("subscheme", SubScheme.class);
		addRelatedEntity("fundSource", Fundsource.class);
		addRelatedEntity("functionary", Functionary.class);
		addRelatedEntity("function", CFunction.class);
	}

	@Override
	public ServiceDetails getModel() {
		return serviceDetails;
	}

	@Action(value="/serviceDetails-newform")
	public String newform(){
		addDropdownData("serviceCategoryList", serviceCategoryService.getAllActiveServiceCategories());
		return NEW;
	}
	
	@Override
	public void prepare() {
		super.prepare();
		if(null != parameters.get("serviceId") && StringUtils.isNotEmpty(parameters.get("serviceId")[0])){
			serviceDetails = serviceDetailsService.findById(Long.valueOf(parameters.get("serviceId")[0]),false);
			accountDetails.addAll(serviceDetails.getServiceAccountDtls());
			for (ServiceAccountDetails account : serviceDetails.getServiceAccountDtls()) {
				subledgerDetails.addAll(account.getSubledgerDetails());
				
			}
			for(Department department : serviceDetails.getServiceDept()){
				departmentList.add(department.getId());
			}
		}else if(null != serviceDetails.getServiceCategory() && null != serviceDetails.getServiceCategory().getCode()){
			ServiceCategory category = serviceCategoryService.findByCode(serviceDetails.getServiceCategory().getCode());
			serviceDetails.setServiceCategory(category);
		} 
		addDropdownData("departmentList", departmentService.getAllDepartments());
		addDropdownData("functionaryList", getPersistenceService().findAllBy("from Functionary where isactive=1 order by upper(name)"));
		addDropdownData("fundList", getPersistenceService().findAllBy("from Fund where isactive = 1 and isNotLeaf!=1 order by upper(name)"));
		addDropdownData("fundsourceList", getPersistenceService().findAllBy("from Fundsource where isActive='1' order by upper(name)"));
		addDropdownData("functionList", getPersistenceService().findAllBy("from CFunction where isactive = 1 AND isnotleaf=0 order by upper(name)"));
		if( null != serviceDetails.getFund() && serviceDetails.getFund().getId() != -1){
			addDropdownData("schemeList", getPersistenceService().findAllBy(" from Scheme where fund.id=?", serviceDetails.getFund().getId()));
		}else{
			addDropdownData("schemeList",  Collections.EMPTY_LIST );
		}
		if(null != serviceDetails.getScheme() && serviceDetails.getScheme().getId() != -1 ){
			addDropdownData("subschemeList", getPersistenceService().findAllBy("from SubScheme where scheme.id=? and isActive='1'order by name", serviceDetails.getScheme().getId()));
		}else{
			addDropdownData("subschemeList", Collections.EMPTY_LIST);
		}
	}
	
	@Action(value="/serviceDetails-beforeCreate")
	public String beforeCreate(){
		accountDetails.add(new ServiceAccountDetails());
		subledgerDetails.add(new ServiceSubledgerInfo());
		return BEFORECREATE;
	}
	
	@ValidationErrorPage(value=BEFORECREATE)
	@Action(value="serviceDetails-create")
	public String create(){
		insertOrUpdateService();
		if(hasActionErrors()){
			return BEFORECREATE;
		}
		return MESSAGE;
	}

	@Action(value="/serviceDetails-listServices")
	public String listServices(){
		return "list";
	}
	
	public String view(){
		
		return "view";
	}
	
	@ValidationErrorPage(value=BEFOREMODIFY)
	public String beforeModify(){
		
		if( null == this.accountDetails || this.accountDetails.isEmpty()) {
			this.accountDetails.add(new ServiceAccountDetails());
		}
		
		if( null == this.subledgerDetails || this.subledgerDetails.isEmpty()) {
			this.subledgerDetails.add(new ServiceSubledgerInfo());
		}
		return BEFOREMODIFY;
	}
	
	@SuppressWarnings("unchecked")
	@ValidationErrorPage(value=BEFOREMODIFY)
	public String modify(){
		List<ServiceAccountDetails> accountList =(List<ServiceAccountDetails>) getPersistenceService().getSession().
		createCriteria(ServiceAccountDetails.class).add(Restrictions.eq("serviceDetails.id", serviceDetails.getId())).list();
		
		for (ServiceAccountDetails serviceAccountDetails : accountList) {
			
			Query qry = getPersistenceService().getSession().createQuery("delete from ServiceSubledgerInfo where serviceAccountDetail.id=:accountId");
			qry.setLong("accountId", serviceAccountDetails.getId());
			qry.executeUpdate();
		}
		
		Query qry = getPersistenceService().getSession().createQuery("delete from ServiceAccountDetails where serviceDetails.id=:serviceId");
		qry.setLong("serviceId", serviceDetails.getId());
		qry.executeUpdate();
		insertOrUpdateService();
		if(hasActionErrors()){
			return BEFOREMODIFY;
		}
		return MESSAGE;
	}
	
	private void insertOrUpdateService() {
		removeEmptyRowsAccoutDetail(accountDetails);
		removeEmptyRowsSubledger(subledgerDetails);
		if(validateAccountDetails()){
			formatServiceDetails();
			if (serviceDetails.getVoucherCreation().equals(Boolean.TRUE))
			{
				isVoucherApproved=serviceDetails.getIsVoucherApproved();
				serviceDetails.setIsVoucherApproved(isVoucherApproved);
			}
			serviceDetailsService.persist(serviceDetails);
			addActionMessage(getText("service.create.success.msg",new String[]{getModel().getCode(),getModel().getName()}));
		}
		if(subledgerDetails.isEmpty()){
			subledgerDetails.add(new ServiceSubledgerInfo());
		}
		if(accountDetails .isEmpty()){
			accountDetails.add(new ServiceAccountDetails());
		}
	}
	
	
	private void formatServiceDetails(){
	
		for(Long deptId : departmentList){
			
			Department dept = (Department) getPersistenceService().find(" from Department where id= ?",deptId);
			serviceDetails.addServiceDept(dept);
		}
		
		for(ServiceAccountDetails account : accountDetails){
			
			ServiceAccountDetails serviceAccount = new  ServiceAccountDetails();
			serviceAccount.setAmount(account.getAmount());
			CChartOfAccounts glCodeId = (CChartOfAccounts)persistenceService.find(" from CChartOfAccounts where id =?",account.getGlCodeId().getId());
			serviceAccount.setGlCodeId(glCodeId);
			if(null != account.getFunction()  && null != account.getFunction().getId()){
				CFunction function = (CFunction)persistenceService.find("from CFunction where id=?",account.getFunction().getId());
				serviceAccount.setFunction(function);
			}
			
			serviceAccount.setServiceDetails(serviceDetails);
			for(ServiceSubledgerInfo subledger : subledgerDetails){
				
				if(subledger.getServiceAccountDetail().getGlCodeId().getId().equals( account.getGlCodeId().getId())){
					
					ServiceSubledgerInfo subledgerInfo = new  ServiceSubledgerInfo();
					Accountdetailtype accdetailtype = (Accountdetailtype) getPersistenceService().findByNamedQuery(
							CollectionConstants.QUERY_ACCOUNTDETAILTYPE_BY_ID, subledger.getDetailType().getId());
					subledgerInfo.setDetailType(accdetailtype);
					subledgerInfo.setDetailKeyId(subledger.getDetailKeyId());
					subledgerInfo.setAmount(subledger.getAmount());
					subledgerInfo.setServiceAccountDetail(serviceAccount);
					serviceAccount.addSubledgerDetails(subledgerInfo);
				}
				
			}
			serviceDetails.addServiceAccountDtls(serviceAccount);
		}
		
	    }
	                       
	private void removeEmptyRowsAccoutDetail(List<ServiceAccountDetails> list) {
		for (Iterator<ServiceAccountDetails> detail = list.iterator(); detail.hasNext();) {
			ServiceAccountDetails next = detail.next();
			if ( null != next  && (null == next.getGlCodeId() || null == next.getGlCodeId().getId() || next.getGlCodeId().getId().toString().trim().isEmpty()) 
					&& next.getAmount().compareTo(BigDecimal.ZERO) ==0) {
					detail.remove();
			}
			else if(null == next)
			{
				detail.remove();
			}
		}
	}
	
	protected void removeEmptyRowsSubledger(List<ServiceSubledgerInfo>  list) {
		for (Iterator<ServiceSubledgerInfo> detail = list.iterator(); detail.hasNext();) {
			ServiceSubledgerInfo next = detail.next();
			if( (null!= next) && (null == next.getServiceAccountDetail() || null ==  next.getServiceAccountDetail().getGlCodeId() 
					|| null == next.getServiceAccountDetail().getGlCodeId().getId() || next.getServiceAccountDetail().getGlCodeId().getId()
					== 0 || next.getServiceAccountDetail().getGlCodeId().getId() == -1)) {
				
					detail.remove();
				
			}else if(null == next)
			{
				detail.remove();
			}
				
		}
	
	}
	private boolean validateAccountDetails(){
		int index=0;
		for (ServiceAccountDetails account : accountDetails) {

			if(null != account.getGlCodeId() && null!= account.getGlCodeId().getGlcode() 
					&& account.getAmount().compareTo(BigDecimal.ZERO) == 0){
				addActionError(getText("service.accdetail.amountZero",new String[]{""+ ++index,account.getGlCodeId().getGlcode()}));
				return Boolean.FALSE;
			}
			else if(account.getAmount().compareTo(BigDecimal.ZERO) >0 && ( null == account.getGlCodeId() || null == account.getGlCodeId().getId())){
				addActionError(getText("service.accdetail.accmissing",new String[]{""+ ++index}));
				return Boolean.FALSE;
			}

		}
		return validateSubledger();
	}
	
	private boolean validateSubledger(){
		Map<String, BigDecimal> accountDetailAmount = new HashMap<String, BigDecimal>();
		for (ServiceAccountDetails account : accountDetails) {
			CChartOfAccountDetail  chartOfAccountDetail = (CChartOfAccountDetail) getPersistenceService().find(" from CChartOfAccountDetail" +
					" where glCodeId.id=?", account.getGlCodeId().getId());
			if(null != chartOfAccountDetail){
				accountDetailAmount.put(account.getGlCodeId().getGlcode(), account.getAmount());
			}
		}
		
		Map<String, BigDecimal> subledgerAmount = new HashMap<String, BigDecimal>();
	
		for (ServiceSubledgerInfo subledger : subledgerDetails) {
			
			if(null == subledger.getDetailType() || null == subledger.getDetailType().getId() || subledger.getDetailType().getId() == 0){
				
				addActionError(getText("service.accdetailType.entrymissing",new String[]{subledger.getServiceAccountDetail().getGlCodeId().getGlcode()}));
				return Boolean.FALSE;
			}/*else if(null == subledger.getDetailKeyId()){
				
				addActionError(getText("service.accdetailKey.entrymissing",new String[]{subledger.getServiceAccountDetail().getGlCodeId().getGlcode()}));
				
				return Boolean.FALSE;
			}*/
			
			else if(null  != subledgerAmount.get(subledger.getServiceAccountDetail().getGlCodeId().getGlcode())){
				
					BigDecimal amount =  subledgerAmount.get(subledger.getServiceAccountDetail().getGlCodeId().getGlcode());
					subledgerAmount.put(subledger.getServiceAccountDetail().getGlCodeId().getGlcode(), amount.add(subledger.getAmount()));
			}
			else {
				
				subledgerAmount.put(subledger.getServiceAccountDetail().getGlCodeId().getGlcode(),subledger.getAmount());
			}
			
		}
		
		for ( Map.Entry<String, BigDecimal> entry  : accountDetailAmount.entrySet()) {
			
			String key = entry.getKey();
			BigDecimal value = entry.getValue();
			if(null == subledgerAmount.get(key)){
				addActionError(getText("service.accdetail.entrymissing",new String[]{key}));
				return Boolean.FALSE;
			}else if(subledgerAmount.get(key).compareTo(value) != 0){
				addActionError(getText("service.subledger.amtnotmatchinng",new String[]{key}));
				return Boolean.FALSE;
			}
		}
		
		return Boolean.TRUE;
	}
	
	@Action(value="/serviceDetails-codeUniqueCheck")
	public String codeUniqueCheck(){
		
		return "codeUniqueCheck";
	}
	public boolean getCodeCheck(){
		
		boolean codeExistsOrNot = false;
		ServiceDetails service = (ServiceDetails)serviceDetailsService.find("from ServiceDetails where code='"+serviceDetails.getCode()+"'");
		if(null != service ){
			codeExistsOrNot = true; 
		}
		return codeExistsOrNot;
	}
	
	public ServiceDetails getServiceDetails() {
		return serviceDetails;
	}

	public void setServiceDetails(ServiceDetails serviceDetails) {
		this.serviceDetails = serviceDetails;
	}

	public List<ServiceAccountDetails> getAccountDetails() {
		return accountDetails;
	}

	public void setAccountDetails(List<ServiceAccountDetails> accountDetails) {
		this.accountDetails = accountDetails;
	}

	public List<ServiceSubledgerInfo> getSubledgerDetails() {
		return subledgerDetails;
	}

	public void setSubledgerDetails(List<ServiceSubledgerInfo> subledgerDetails) {
		this.subledgerDetails = subledgerDetails;
	}

	public void setServiceDetailsService(
			PersistenceService<ServiceDetails, Long> serviceDetailsService) {
		this.serviceDetailsService = serviceDetailsService;
	}

	public List<Long> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<Long> departmentList) {
		this.departmentList = departmentList;
	}

	public List<ServiceDetails> getServiceList() {
		return serviceList;
	}

}
